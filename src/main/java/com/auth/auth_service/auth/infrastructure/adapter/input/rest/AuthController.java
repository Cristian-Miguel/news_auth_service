package com.auth.auth_service.auth.infrastructure.adapter.input.rest;

import com.auth.auth_service.auth.application.port.input.RefreshTokenUseCase;
import com.auth.auth_service.auth.application.port.input.SignInUseCase;
import com.auth.auth_service.auth.application.port.input.SignOutUseCase;
import com.auth.auth_service.auth.application.port.input.SignUpUseCase;
import com.auth.auth_service.auth.infrastructure.adapter.input.rest.data.request.SignInRequest;
import com.auth.auth_service.auth.infrastructure.adapter.input.rest.data.request.SignUpRequest;
import com.auth.auth_service.auth.infrastructure.adapter.input.rest.data.response.AuthResponse;
import com.auth.auth_service.auth.infrastructure.adapter.input.rest.mapper.AuthRestMapper;
import com.auth.auth_service.shared.infrastructure.adapter.input.rest.data.response.GenericResponse;
import com.auth.auth_service.user.domain.model.User;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final SignUpUseCase signUpUseCase;
    private final SignInUseCase signInUseCase;
    private final SignOutUseCase signOutUseCase;
    private final RefreshTokenUseCase refreshTokenUseCase;

    private final AuthRestMapper authRestMapper;

    @PostMapping(value = "/sign_up")
    public ResponseEntity<GenericResponse<AuthResponse>> signUp(@RequestBody @Valid SignUpRequest request) {
        User user = authRestMapper.toUser(request);
        AuthResponse token = signUpUseCase.signUp(user);
        GenericResponse<AuthResponse> response = new GenericResponse<>(true, HttpStatus.CREATED.getReasonPhrase(), token);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping(value = "/sign_in")
    public ResponseEntity<GenericResponse<AuthResponse>> signIn(@RequestBody @Valid SignInRequest request) {
        User user = authRestMapper.toUser(request);
        AuthResponse token = signInUseCase.signIn(user);
        GenericResponse<AuthResponse> response = new GenericResponse<>(true, HttpStatus.OK.getReasonPhrase(), token);

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PostMapping(value = "/validate")
    public ResponseEntity<GenericResponse<AuthResponse>> validateToken(@RequestHeader("Authorization") String token){
        AuthResponse authToken = refreshTokenUseCase.validateToken(token);
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + authToken.getAccessToken());
        GenericResponse<AuthResponse> responseBody = new GenericResponse<>(true, HttpStatus.OK.getReasonPhrase(), authToken);

        ResponseEntity<GenericResponse<AuthResponse>> response = new ResponseEntity<>(responseBody, headers, HttpStatus.OK);
        return response;
    }

    @PostMapping(value = "/sign_out")
    public ResponseEntity<GenericResponse<String>> signOut(@RequestBody AuthResponse request) {
        String message = signOutUseCase.signOut(request.getRefreshToken());
        GenericResponse<String> response = new GenericResponse<>(true, HttpStatus.OK.getReasonPhrase(), message);

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PostMapping(value = "/refresh_token")
    public ResponseEntity<GenericResponse<AuthResponse>> refreshToken(@RequestBody AuthResponse request){
        AuthResponse authResponse = refreshTokenUseCase.refreshToken(request.getRefreshToken());

        GenericResponse<AuthResponse> response = new GenericResponse<>(true, HttpStatus.OK.getReasonPhrase(), authResponse);

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
