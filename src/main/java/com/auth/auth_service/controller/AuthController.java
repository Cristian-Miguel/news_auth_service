package com.auth.auth_service.controller;

import com.auth.auth_service.dto.SignInRequest;
import com.auth.auth_service.dto.SignUpRequest;
import com.auth.auth_service.dto.AuthResponse;
import com.auth.auth_service.dto.GenericResponse;
import com.auth.auth_service.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping(value = "/sign_up")
    public ResponseEntity<GenericResponse<AuthResponse>> signUp(@RequestBody @Valid SignUpRequest request) {
        AuthResponse token = authService.signUp(request);
        GenericResponse<AuthResponse> response = new GenericResponse<>(true, HttpStatus.CREATED.getReasonPhrase(), token);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping(value = "/sign_in")
    public ResponseEntity<GenericResponse<AuthResponse>> signIn(@RequestBody @Valid SignInRequest request) {
        AuthResponse token = authService.signIn(request);
        GenericResponse<AuthResponse> response = new GenericResponse<>(true, HttpStatus.OK.getReasonPhrase(), token);

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

}
