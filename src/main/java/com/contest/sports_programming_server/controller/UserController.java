package com.contest.sports_programming_server.controller;

import com.contest.sports_programming_server.dto.request.LoginRequest;
import com.contest.sports_programming_server.dto.response.LoginResponse;
import com.contest.sports_programming_server.service.UserService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "User")
public class UserController {
    //Пытался сделать хеширование пароля, но не получилось пока, поэтому есть закомментированные классы
    private final UserService userService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody @Valid LoginRequest request) {
        LoginResponse response = userService.loginPlainText(request.getLogin(), request.getPassword());
        return ResponseEntity.ok(response); // HTTP 200 с LoginResponse
    }

    //TODO: Сделать здесь эндпоинт для регистрации участников
}
