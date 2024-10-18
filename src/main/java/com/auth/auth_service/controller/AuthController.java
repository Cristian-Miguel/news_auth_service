package com.auth.auth_service.controller;

import com.auth.auth_service.dto.SignInRequest;
import com.auth.auth_service.dto.SignUpRequest;
import com.auth.auth_service.dto.AuthResponse;
import com.auth.auth_service.dto.GenericResponse;
import com.auth.auth_service.service.RefreshTokenService;
import com.auth.auth_service.service.SignInService;
import com.auth.auth_service.service.SignOutService;
import com.auth.auth_service.service.SignUpService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    @Autowired
    private SignUpService signUpService;

    @Autowired
    private SignInService signInService;

    @Autowired
    private SignOutService signOutService;

    @Autowired
    private RefreshTokenService refreshTokenService;

    @PostMapping(value = "/sign_up")
    public ResponseEntity<GenericResponse<AuthResponse>> signUp(@RequestBody @Valid SignUpRequest request) {
        AuthResponse token = signUpService.signUp(request);
        GenericResponse<AuthResponse> response = new GenericResponse<>(true, HttpStatus.CREATED.getReasonPhrase(), token);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping(value = "/sign_in")
    public ResponseEntity<GenericResponse<AuthResponse>> signIn(@RequestBody @Valid SignInRequest request) {
        AuthResponse token = signInService.signIn(request);
        GenericResponse<AuthResponse> response = new GenericResponse<>(true, HttpStatus.OK.getReasonPhrase(), token);

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PostMapping(value = "/validate")
    public ResponseEntity<GenericResponse<AuthResponse>> validateToken(@RequestHeader("Authorization") String token){
        AuthResponse authToken = refreshTokenService.validateToken(token);
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + authToken.getAccessToken());
        GenericResponse<AuthResponse> responseBody = new GenericResponse<>(true, HttpStatus.OK.getReasonPhrase(), authToken);

        ResponseEntity<GenericResponse<AuthResponse>> response = new ResponseEntity<>(responseBody, headers, HttpStatus.OK);
        return response;
    }

    @PostMapping(value = "/sign_out")
    public ResponseEntity<GenericResponse<String>> signOut(@RequestBody AuthResponse request) {
        String message = signOutService.signOut(request.getRefreshToken());
        GenericResponse<String> response = new GenericResponse<>(true, HttpStatus.OK.getReasonPhrase(), message);

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PostMapping(value = "/refresh_token")
    public ResponseEntity<GenericResponse<AuthResponse>> refreshToken(@RequestBody AuthResponse request){
        AuthResponse authResponse = refreshTokenService.refreshToken(request.getRefreshToken());

        GenericResponse<AuthResponse> response = new GenericResponse<>(true, HttpStatus.OK.getReasonPhrase(), authResponse);

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
