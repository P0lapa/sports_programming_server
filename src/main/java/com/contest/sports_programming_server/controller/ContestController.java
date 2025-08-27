package com.contest.sports_programming_server.controller;

import com.contest.sports_programming_server.dto.GreetingDto;
import com.contest.sports_programming_server.dto.TaskDto;
import com.contest.sports_programming_server.service.ContestService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.antlr.v4.runtime.misc.NotNull;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/contest")
@RequiredArgsConstructor
public class ContestController {



    // Приветственное сообщение
    @GetMapping("/hello")
    public GreetingDto hello() {
//        return new GreetingDto(contestService.greeting());
        return null;
    }

    // Получение задачи по id
    @GetMapping("/tasks/{id}")
    public TaskDto getProblem(@PathVariable @NotNull Long id) {
//        return contestService.getProblem(id);
        return null;
    }

    @PutMapping("/tasks/{id}")
    public Object tryTheTask() {
        return null;
    }

    @PostMapping("/tasks/{id}")
    public Object answerTheTask() {
        return null;
    }

    // Сохранение и проверка решения
//    @PutMapping("/submissions")
//    public SubmissionResponse submitAndJudge(@Valid @RequestBody SubmissionRequest request) {
//        return contestService.submitAndJudge(request);
//    }
//
//    // Финальная отправка на задачу
//    @PostMapping("/tasks/{id}")
//    public FinalizeResponse finalizeSubmission(@PathVariable Long id,
//                                               @Valid @RequestBody FinalizeRequest request) {
//        return contestService.finalizeSubmission(id, request.submissionId(), request.answer());
//    }
}
