package com.contest.sports_programming_server.controller;

import com.contest.sports_programming_server.dto.request.LoginRequest;
import com.contest.sports_programming_server.dto.response.LoginResponse;
import com.contest.sports_programming_server.service.UserService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Auth")
public class AuthController {

    private final UserService userService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody @Valid LoginRequest request) {
        LoginResponse response = userService.login(request.getLogin(), request.getPassword());
        return ResponseEntity.ok(response);
    }

}
