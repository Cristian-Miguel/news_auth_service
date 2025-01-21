package com.auth.auth_service.auth.infrastructure.adapter.input.rest;

import com.auth.auth_service.auth.application.port.input.*;
import com.auth.auth_service.auth.domain.model.Authentication;
import com.auth.auth_service.auth.infrastructure.adapter.input.rest.data.request.ResetPasswordValidateRequest;
import com.auth.auth_service.auth.infrastructure.adapter.input.rest.data.request.SendResetPasswordRequest;
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
    private final ResetPasswordCaseUse resetPasswordCaseUse;

    private final AuthRestMapper authRestMapper;

    @PostMapping(value = "/sign_up")
    public ResponseEntity<GenericResponse<AuthResponse>> signUp(@RequestBody @Valid SignUpRequest request) {
        User user = authRestMapper.toUser(request);
        Authentication result = signUpUseCase.signUp(user);
        AuthResponse authComponents = authRestMapper.toAuthResponse(result);

        GenericResponse<AuthResponse> response = new GenericResponse<>(true, HttpStatus.CREATED.getReasonPhrase(), authComponents);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping(value = "/sign_in")
    public ResponseEntity<GenericResponse<AuthResponse>> signIn(@RequestBody @Valid SignInRequest request) {
        User user = authRestMapper.toUser(request);
        Authentication result = signInUseCase.signIn(user);
        AuthResponse authComponents = authRestMapper.toAuthResponse(result);

        GenericResponse<AuthResponse> response = new GenericResponse<>(true, HttpStatus.OK.getReasonPhrase(), authComponents);

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PostMapping(value = "/validate")
    public ResponseEntity<GenericResponse<AuthResponse>> validateToken(@RequestHeader("Authorization") String token) {
        Authentication result = refreshTokenUseCase.validateToken(token);
        AuthResponse authComponents = authRestMapper.toAuthResponse(result);

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + authComponents.getAccessToken());
        GenericResponse<AuthResponse> responseBody = new GenericResponse<>(true, HttpStatus.OK.getReasonPhrase(), authComponents);

        return new ResponseEntity<>(responseBody, headers, HttpStatus.OK);
    }

    @PostMapping(value = "/sign_out")
    public ResponseEntity<GenericResponse<String>> signOut(@RequestBody AuthResponse request) {
        String message = signOutUseCase.signOut(request.getRefreshToken());
        GenericResponse<String> response = new GenericResponse<>(true, HttpStatus.OK.getReasonPhrase(), message);

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PostMapping(value = "/refresh_token")
    public ResponseEntity<GenericResponse<AuthResponse>> refreshToken(@RequestBody AuthResponse request) {
        Authentication result = refreshTokenUseCase.refreshToken(request.getRefreshToken());
        AuthResponse authComponents = authRestMapper.toAuthResponse(result);

        GenericResponse<AuthResponse> response = new GenericResponse<>(true, HttpStatus.OK.getReasonPhrase(), authComponents);

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PostMapping(value = "/send_reset_password_email")
    public ResponseEntity<GenericResponse<String>> sendResetPasswordByEmail(@RequestBody SendResetPasswordRequest request) {
        User user = authRestMapper.toUser(request);
        String result = resetPasswordCaseUse.sendResetPasswordByEmail(user);

        GenericResponse<String> response = new GenericResponse<>(true, HttpStatus.OK.getReasonPhrase(), result);

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PostMapping(value = "/reset_password_validated")
    public ResponseEntity<GenericResponse<String>> resetPasswordValidated(
            @RequestHeader("Authorization") String token,
            @RequestBody ResetPasswordValidateRequest request) {
        User user = authRestMapper.toUser(request);
        String result = resetPasswordCaseUse.resetPasswordValidated(user, token);

        GenericResponse<String> response = new GenericResponse<>(true, HttpStatus.OK.getReasonPhrase(), result);

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

}
