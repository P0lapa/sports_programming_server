package com.contest.sports_programming_server.service;

import com.contest.sports_programming_server.dto.*;
import com.contest.sports_programming_server.dto.request.CreateTestRequest;
import com.contest.sports_programming_server.entity.TaskEntity;
import com.contest.sports_programming_server.entity.TestEntity;
import com.contest.sports_programming_server.mapper.DtoTestMapper;
import com.contest.sports_programming_server.repository.TestRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class TestService {
    //Очень большой сервис, давай разделим 1 будет только заниматься запуском тестов, а другой, например AttemptService
    // будет вызывать функции отсюда
    private final TestRepository repository;
    private final DtoTestMapper dtoMapper;
    private final TaskService taskService;

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

            // 2. Файл с решением
            String ext = getExtension(language);
            Path solutionFile = tmpDir.resolve("solution." + ext);
            Files.writeString(solutionFile, solution);

            // 3. Компиляция (если нужно)
            if (language != Language.PYTHON) {
                int compileCode = compileSolution(language, solutionFile, tmpDir, results);
                if (compileCode != 0) {
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
            log.error(e.getMessage(), e);
            TestResult err = new TestResult();
            err.setPassed(false);
            err.setReason("SERVER_ERROR");
            results.add(err);
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
            compileError.setReason("CE: " + output.trim());
            results.add(compileError);
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
            log.error(e.getMessage(), e);
        }
    }

    @Transactional(readOnly = true)
    public List<TestEntity> getPublicTestsByTask(UUID taskId) {
        return repository.findAllByTask_IdAndIsPublicTrue(taskId);
    }

    @Transactional(readOnly = true)
    public List<TestEntity> getTestsByTask(UUID taskId) {
        return repository.findAllByTask_Id(taskId);
    }

    @Transactional(readOnly = true)
    public List<TestDto> getTestsByTaskAsDto(UUID taskId) {
        return dtoMapper.toDtoList(getTestsByTask(taskId));
    }

    @Transactional
    public TestEntity createTest(UUID taskId, Boolean isPublic, String input, String expected) {

        TaskEntity task = taskService.getTaskOrThrow(taskId);

        int testNumber = repository.countByTask_Id(taskId) + 1;

        var entity =  TestEntity.builder()
                .task(task)
                .testNumber(testNumber)
                .isPublic(isPublic)
                .inputData(input)
                .expectedOutput(expected)
                .build();
        return repository.save(entity);
    }

    @Transactional
    public TestDto createTestAsDto(UUID taskId, CreateTestRequest request) {
        TestEntity entity = createTest(taskId, request.getIsPublic(), request.getInput(), request.getExpected());
        return dtoMapper.toDto(entity);
    }

    @Transactional
    public TestDto updateTest(UUID testId, TestDto request) {
        TestEntity entity = getTestOrThrow(testId);

        dtoMapper.updateEntityFromDto(request, entity);

        TestEntity saved = repository.save(entity);
        return dtoMapper.toDto(saved);
    }

    @Transactional
    public void deleteTest(UUID testId) {
        if(!repository.existsById(testId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Test not found");
        }
        repository.deleteById(testId);
    }

    public TestEntity getTestOrThrow(UUID id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Test not found"));
    }

}