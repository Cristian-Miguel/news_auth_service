package com.auth.auth_service.command.controller;

import com.auth.auth_service.command.dto.SignUpRequest;
import com.auth.auth_service.command.handler.SignUpHandler;
import com.auth.auth_service.shared.dto.AuthResponse;
import com.auth.auth_service.shared.dto.GenericResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth/command")
@RequiredArgsConstructor
public class AuthCommandController {

    private final SignUpHandler signUpHandler;

    @PostMapping(value = "/sign_up")
    public ResponseEntity<GenericResponse<AuthResponse>> signUp(@RequestBody SignUpRequest request) {
        AuthResponse token = signUpHandler.singUp(request);
        GenericResponse<AuthResponse> response = new GenericResponse<>(true, HttpStatus.CREATED.getReasonPhrase(), token);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

}
