package com.contest.sports_programming_server.service;

import com.contest.sports_programming_server.dto.*;
import com.contest.sports_programming_server.entity.TaskEntity;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Map;

@Service
public class TestService {


    private static final Map<Language, String> LANGUAGE_IMAGES = Map.of(
            Language.JAVA, "openjdk:21-jdk",
            Language.CPP, "gcc:14",
            Language.C, "gcc:14",
            Language.PYTHON, "python:3.12"
    );

    private String getImage(Language lang) {
        return LANGUAGE_IMAGES.get(lang);
    }

    private static final Map<Language, String> LANGUAGE_EXTENSIONS = Map.of(
            Language.JAVA, "jar",
            Language.CPP, "cpp",
            Language.C, "c",
            Language.PYTHON, "py"
    );

    private String getExtension(Language lang) {
        return LANGUAGE_EXTENSIONS.get(lang);
    }

    public TaskCheckResponse runOpenTests(TaskCheckRequest request) {
        return new TaskCheckResponse();
    }

    public TaskCheckResponse runAllTests(TaskCheckRequest request) {

        // Получить Task с помошью request.task_id
        //
        return new TaskCheckResponse();
    }

    private ArrayList<TestResult> runTests(
            String userNumber,
            Language language,
            TaskEntity task,
            String solution,
            ArrayList<TestCase> tests
    ) {

        ArrayList<TestResult> results = new ArrayList<>();

        try {
            // 1. Создаем временную директорию для этого запуска
            Path tmpDir = Files.createTempDirectory("judge_" + userNumber);

            // 2. Файл с решением
            String ext = getExtension(language);
            Path solutionFile = tmpDir.resolve("solution." + ext);
            Files.writeString(solutionFile, solution);

            // 3. Компиляция (если нужно)
            if (language != Language.PYTHON) {
                int compileCode = compileSolution(language, solutionFile, tmpDir);
                if (compileCode != 0) {
                    TestResult compileError = new TestResult();
                    compileError.setPassed(false);
                    compileError.setReason("CE"); // Compilation Error
                    results.add(compileError);
                    return results;
                }
            }

            // 4. Создаём переиспользуемые файлы input/output
            Path inputFile = tmpDir.resolve("test.in");
            Path outputFile = tmpDir.resolve("test.out");
            Path timeFile = tmpDir.resolve("time.txt");

            // 4. Прогон тестов
            for (int i = 0; i < tests.size(); i++) {
                TestCase tc = tests.get(i);
                TestResult res = new TestResult();

                // 4.1 Записываем input
                Files.writeString(inputFile, tc.getInput());

                // 4.2 Собираем команду для запуска внутри Docker
                String image = getImage(language);
                String runCmd;

                switch (language) {
                    case CPP, C -> runCmd = "./solution";
                    case JAVA -> runCmd = "java Solution"; // если класс называется Solution
                    case PYTHON -> runCmd = "python3 solution.py";
                    default -> throw new IllegalArgumentException("Unsupported language");
                }

                String command = String.format("docker run --rm -m %dm --cpus=1.0 -v %s:/app -w /app %s \"timeout %ds sh -c 'time  %s < test.in > test.out' 2> time.txt\"",
                        task.getMemoryLimit(), tmpDir.toAbsolutePath(), image, task.getTimeLimit(), runCmd);


                // 4.3 Запуск
                ProcessBuilder pb = new ProcessBuilder(command);
                pb.directory(tmpDir.toFile());
                Process p = pb.start();
                int exitCode = p.waitFor();

                // 4.4 Читаем вывод и результат
                String output = Files.exists(outputFile) ? Files.readString(outputFile) : "";
                double timeUsed = Files.exists(timeFile)
                        ? Double.parseDouble(Files.readString(timeFile).trim())
                        : -1.0;

                res.setTimeSeconds(0.0);

                if (exitCode == 124) {
                    res.setPassed(false);
                    res.setReason("TLE");
                } else if (exitCode == 137) {
                    res.setPassed(false);
                    res.setReason("MLE");
                } else if (exitCode != 0) {
                    res.setPassed(false);
                    res.setReason("RTE");
                } else if (output.trim().equals(tc.getExpected().trim())) {
                    res.setPassed(true);
                    res.setReason("OK");
                } else {
                    res.setPassed(false);
                    res.setReason("WA");
                }

                results.add(res);
            }

            // 5. Очистка
            Files.walk(tmpDir)
                    .sorted(Comparator.reverseOrder())
                    .forEach(path -> {
                        try {
                            Files.deleteIfExists(path);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    });

        } catch (Exception e) {
            e.printStackTrace();
            TestResult err = new TestResult();
            err.setPassed(false);
            err.setReason("SERVER_ERROR");
            results.add(err);
        }

        return results;
    }


    private int compileSolution(Language lang, Path sourceFile, Path workDir) throws IOException, InterruptedException {
        String image = getImage(lang);
        String compileCmd;

        switch (lang) {
            case CPP -> {
                // g++ solution.cpp -o solution
                compileCmd = "g++ " + sourceFile.getFileName().toString() + " -o solution";
            }
            case C -> {
                // gcc solution.c -o solution
                compileCmd = "gcc " + sourceFile.getFileName().toString() + " -o solution";
            }
            case JAVA -> {
                // javac Solution.java
                compileCmd = "javac " + sourceFile.getFileName().toString();
            }
            default -> {
                return 0; // Для Python компиляция не нужна
            }
        }

        String command = String.format("docker run --rm -v %s:/app -w /app %s sh -c %s", workDir.toAbsolutePath(), image, compileCmd);

        ProcessBuilder pb = new ProcessBuilder(command);
        pb.directory(workDir.toFile());
        pb.redirectErrorStream(true);
        Process p = pb.start();

        String output = new String(p.getInputStream().readAllBytes());
        int exitCode = p.waitFor();

        if (exitCode != 0) {
            System.err.println("Compilation failed:\n" + output);
        }

        return exitCode;
    }

}
