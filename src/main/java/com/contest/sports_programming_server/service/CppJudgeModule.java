package com.contest.sports_programming_server.service;

import com.contest.sports_programming_server.dto.TestCase;
import com.contest.sports_programming_server.dto.TestResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

@Component
@Slf4j
public class CppJudgeModule {

    public List<TestResult> runTestsC(
            String userNumber,
            int memoryLimitMb,
            int timeLimitSeconds,
            String solution,
            List<TestCase> tests
    ) {
        List<TestResult> results = new ArrayList<>();

        try {
            // 1. Создаём временную директорию для запуска
            Path tmpDir = Files.createTempDirectory("judge_" + userNumber + "_");

            // 2. Файл с кодом
            Path solutionFile = tmpDir.resolve("solution.cpp");
            Files.writeString(solutionFile, solution);

            // 3. Компиляция
            int compileCode = compileSolutionC(solutionFile, tmpDir, results);
            if (compileCode != 0) {
                return results;
            }

            // 4. Прогон тестов
            Path inputFile = tmpDir.resolve("test.in");
            Path outputFile = tmpDir.resolve("test.out");

            for (TestCase tc : tests) {
                TestResult res = new TestResult();

                // 4.1 Записываем input
                Files.writeString(inputFile, tc.getInput());

                // 4.2 Сбор команды nsjail
                String runCmd = "./solution";
                List<String> cmd = Arrays.asList(
                        "nsjail",
                        "-Mo",
                        "--chroot", tmpDir.toAbsolutePath().toString(),
                        "--time_limit", String.valueOf(timeLimitSeconds),
                        "--rlimit_as", String.valueOf(memoryLimitMb * 1024 * 1024),
                        "--disable_proc",
                        "--",
                        "sh", "-c", runCmd + " < " + inputFile.toAbsolutePath() + " > " + outputFile.toAbsolutePath()
                );

                // 4.3 Запуск
                ProcessBuilder pb = new ProcessBuilder(cmd);
                pb.directory(tmpDir.toFile());
                Process p = pb.start();
                int exitCode = p.waitFor();

                // 4.4 Чтение вывода
                String output = Files.exists(outputFile) ? Files.readString(outputFile) : "";

                // 4.5 Определяем результат
                if (exitCode != 0) {
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
            log.error(e.getMessage(), e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }

        return results;
    }


    private int compileSolutionC(Path sourceFile, Path workDir, List<TestResult> results)
            throws IOException, InterruptedException {

        ProcessBuilder pb = getProcessBuilder(sourceFile, workDir);

        Process p = pb.start();
        String output = new String(p.getInputStream().readAllBytes());
        int exitCode = p.waitFor();

        if (exitCode != 0) {
            TestResult compileError = new TestResult();
            compileError.setPassed(false);
            compileError.setReason("CE: " + output.trim());
            results.add(compileError);
        }

        return exitCode;
    }

    private static ProcessBuilder getProcessBuilder(Path sourceFile, Path workDir) {
        String compileCmd = "g++ " + sourceFile.getFileName().toString() + " -o solution";

        List<String> cmd = Arrays.asList(
                "nsjail",
                "-Mo",
                "--chroot", workDir.toAbsolutePath().toString(),
                "--time_limit", "10",           // лимит компиляции в секундах
                "--rlimit_as", "500000000",     // ~500 MB для компиляции
                "--disable_proc",
                "--",
                "sh", "-c", compileCmd
        );

        ProcessBuilder pb = new ProcessBuilder(cmd);
        pb.directory(workDir.toFile());
        pb.redirectErrorStream(true);
        return pb;
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
            log.error(e.getMessage(), e);
        }
    }

}
