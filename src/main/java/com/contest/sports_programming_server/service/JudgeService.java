package com.contest.sports_programming_server.service;

import com.contest.sports_programming_server.dto.*;
import com.contest.sports_programming_server.entity.AttemptEntity;
import com.contest.sports_programming_server.entity.ContestParticipantEntity;
import com.contest.sports_programming_server.entity.TaskEntity;
import com.contest.sports_programming_server.mapper.TestMapper;
import com.contest.sports_programming_server.repository.ContestParticipantRepository;
import com.contest.sports_programming_server.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
@Slf4j(topic = "ContestLogger")
public class JudgeService {

    private final ContestParticipantRepository contestParticipantRepository;
    private final TaskRepository taskRepository;
    private final TestService testService;
    private final TestMapper testMapper;
    private final AttemptService attemptService;

    private static final Map<Language, String> LANGUAGE_IMAGES = Map.of(
            Language.JAVA, "openjdk:21-jdk",
            Language.CPP, "gcc-time:14",
            Language.C, "gcc-time:14",
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

    private static class ParseResult {
        double timeSeconds = 0.0;
        long memoryBytes = 0L;
    }

    public List<TestResult> runTests(
            String userNumber,
            Language language,
            int memoryLimit,
            int timeLimit,
            String solution,
            List<TestCase> tests
    ) {

        List<TestResult> results = new ArrayList<>();

        try {
            // 1. Создаем временную директорию для этого запуска
            Path tmpDir = Files.createTempDirectory("judge_" + userNumber + "_");
            log.debug("Created directory {}", tmpDir.toAbsolutePath());


            // 2. Файл с решением
            String ext = getExtension(language);
            Path solutionFile = tmpDir.resolve("solution." + ext);
            log.debug("Created solution file {}", solutionFile.toAbsolutePath());

            Files.writeString(solutionFile, solution);

            // 3. Компиляция (если нужно)
            if (language != Language.PYTHON) {
                int compileCode = compileSolution(language, solutionFile, tmpDir, results);
                if (compileCode != 0) {
                    return results;
                }
                log.debug("Compilation completed");
            }

            // 4. Создаём переиспользуемые файлы input/output
            log.debug("Service files creation");
            Path inputFile = tmpDir.resolve("test.in");
            Path outputFile = tmpDir.resolve("test.out");
            Path statsFile = tmpDir.resolve("test.stats");

            // 4. Прогон тестов
            for (TestCase tc : tests) {
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

                String innerCmd = "/usr/bin/time -v timeout " + timeLimit + "s " + runCmd +
                        " < test.in > test.out 2> test.stats";

                // 4.3 Запуск
                ProcessBuilder pb = new ProcessBuilder(
                        "docker", "run", "--rm",
                        "-m", memoryLimit + "m",
                        "--cpus=1.0",
                        "-v", tmpDir.toAbsolutePath() + ":/app",
                        "-w", "/app",
                        image,
                        "sh", "-c", innerCmd
                );

                pb.directory(tmpDir.toFile());
                Process p = pb.start();

                int exitCode = p.waitFor();

                // 4.4 Читаем вывод и результат
                String output = Files.exists(outputFile) ? Files.readString(outputFile) : "";

                ParseResult parseResult = parseStatsFile(statsFile);
                res.setTimeSeconds(parseResult.timeSeconds);
                res.setMemoryBytes(parseResult.memoryBytes);

                if (exitCode == 124) {
                    res.setPassed(false);
                    res.setReason("Превышение ограничения по времени");
                } else if (exitCode == 137) {
                    res.setPassed(false);
                    res.setReason("Превышение ограничения по памяти");
                } else if (exitCode != 0) {
                    res.setPassed(false);
                    res.setReason("Ошибка в ходе исполнения");
                } else if (output.trim().equals(tc.getExpected().trim())) {
                    res.setPassed(true);
                    res.setReason("Верно");
                } else {
                    res.setPassed(false);
                    res.setReason("Неверный результат");
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


    private int compileSolution(Language lang, Path sourceFile, Path workDir, List<TestResult> results)
            throws IOException, InterruptedException {
        String image = getImage(lang);
        String compileCmd;

        switch (lang) {
            case CPP -> compileCmd = "g++ " + sourceFile.getFileName().toString() + " -o solution";
            case C -> compileCmd = "gcc " + sourceFile.getFileName().toString() + " -o solution";
            case JAVA -> compileCmd = "javac " + sourceFile.getFileName().toString();
            default -> { return 0; }
        }

        ProcessBuilder pb = new ProcessBuilder(
                "docker", "run", "--rm",
                "-v", workDir.toAbsolutePath() + ":/app",
                "-w", "/app",
                image,
                "sh", "-c", compileCmd
        );

        pb.directory(workDir.toFile());
        pb.redirectErrorStream(true);
        Process p = pb.start();

        String output = new String(p.getInputStream().readAllBytes());
        int exitCode = p.waitFor();

        if (exitCode != 0) {
            TestResult compileError = new TestResult();
            compileError.setPassed(false);
            compileError.setReason("Ошибка компиляции");
            results.add(compileError);
        }

        return exitCode;
    }

    private static ParseResult parseStatsFile(Path statsFile) {
        ParseResult result = new ParseResult();
        if (!Files.exists(statsFile)) {
            return result;  // если файла нет
        }

        try (BufferedReader br = new BufferedReader(new FileReader(statsFile.toFile()))) {
            String line;
            Pattern elapsedPattern = Pattern.compile("Elapsed \\(wall clock\\) time \\(h:mm:ss or m:ss\\): (.+)");
            Pattern memoryPattern = Pattern.compile("Maximum resident set size \\(kbytes\\): (\\d+)");

            while ((line = br.readLine()) != null) {
                Matcher elapsedMatcher = elapsedPattern.matcher(line);
                if (elapsedMatcher.find()) {
                    String value = elapsedMatcher.group(1).trim();
                    result.timeSeconds = parseElapsedTime(value);
                }
                Matcher memoryMatcher = memoryPattern.matcher(line);
                if (memoryMatcher.find()) {
                    long kb = Long.parseLong(memoryMatcher.group(1));
                    result.memoryBytes = kb * 1024;
                }
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }

        return result;
    }

    private static double parseElapsedTime(String value) {
        String[] parts = value.split(":");
        double seconds = 0.0;
        if (parts.length == 3) { // h:mm:ss
            seconds += Integer.parseInt(parts[0]) * 3600;
            seconds += Integer.parseInt(parts[1]) * 60;
            seconds += Double.parseDouble(parts[2]);
        } else if (parts.length == 2) { // m:ss
            seconds += Integer.parseInt(parts[0]) * 60;
            seconds += Double.parseDouble(parts[1]);
        } else {
            seconds = Double.parseDouble(parts[0]);
        }
        return seconds;
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

    public AttemptEntity runOpenTests(UUID contestParticipantId, TaskCheckRequest request) {

        var contestParticipant = contestParticipantRepository.findById(contestParticipantId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Participant not found"));

        var task = taskRepository.findById(request.getTaskId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Task not found"));

        var tests = testService.getPublicTestsByTask(task.getId());

        var testResults = runTests(
                contestParticipant.getLogin(),
                request.getLanguage(),
                task.getMemoryLimit(),
                task.getTimeLimit(),
                request.getSolution(),
                testMapper.toTCList(tests)
        );

        return attemptService.addAttempt(
                contestParticipant,
                task,
                request.getLanguage(),
                request.getSolution(),
                tests,
                testResults
        );
    }

    public AttemptEntity runAllTests(ContestParticipantEntity participant, TaskEntity task, Solution solution) {

        var testResults = runTests(
                participant.getLogin(),
                solution.language(),
                task.getMemoryLimit(),
                task.getTimeLimit(),
                solution.text(),
                testMapper.toTCList(task.getTests())
        );

        return attemptService.addAttempt(
                participant,
                task,
                solution.language(),
                solution.text(),
                task.getTests(),
                testResults
        );
    }
}
