package com.contest.sports_programming_server.service;

import com.contest.sports_programming_server.dto.*;
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

    private ArrayList<TestResult> runTests(
            String userNumber,
            Language language,
            int memoryLimit,
            int timeLimit,
            String solution,
            ArrayList<TestCase> tests
    ) {

        ArrayList<TestResult> results = new ArrayList<>();

        try {
            // 1. Создаем временную директорию для этого запуска
            Path tmpDir = Files.createTempDirectory("judge_" + userNumber + "_");

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

                // 4.3 Запуск
                ProcessBuilder pb = new ProcessBuilder(
                        "docker", "run", "--rm",
                        "-m", memoryLimit + "m",
                        "--cpus=1.0",
                        "-v", tmpDir.toAbsolutePath() + ":/app",
                        "-w", "/app",
                        image,
                        "sh", "-c", "timeout " + timeLimit + "s " + runCmd + " < test.in > test.out"
                );
                pb.directory(tmpDir.toFile());
                Process p = pb.start();
                long start = System.nanoTime();
                int exitCode = p.waitFor();
                long end = System.nanoTime();

                // 4.4 Читаем вывод и результат
                String output = Files.exists(outputFile) ? Files.readString(outputFile) : "";
                res.setTimeSeconds((end - start) / 1_000_000_000.0 - 0.5);

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
            safeDeleteDirectory(tmpDir);

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

    private void safeDeleteDirectory(Path dir) {
        if (dir == null) return;
        try {
            // Убедимся, что все процессы завершились
            // (если вызывается сразу после run/compile)
            Thread.sleep(200); // иногда нужно дать ОС закрыть дескрипторы

            try (var paths = Files.walk(dir)) {
                paths.sorted(Comparator.reverseOrder()) // сначала файлы, потом папки
                        .forEach(path -> {
                            try {
                                Files.deleteIfExists(path);
                            } catch (IOException e) {
                                System.err.println("Не удалось удалить: " + path + " (" + e.getMessage() + ")");
                            }
                        });
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

}
