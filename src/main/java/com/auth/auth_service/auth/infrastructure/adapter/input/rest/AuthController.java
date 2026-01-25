package com.auth.auth_service.auth.infrastructure.adapter.input.rest;

import com.auth.auth_service.auth.application.port.input.*;
import com.auth.auth_service.auth.domain.model.Authentication;
import com.auth.auth_service.auth.infrastructure.adapter.input.rest.data.request.*;
import com.auth.auth_service.auth.infrastructure.adapter.input.rest.data.response.AuthResponse;
import com.auth.auth_service.auth.infrastructure.adapter.input.rest.mapper.AuthRestMapper;
import com.auth.auth_service.shared.infrastructure.adapter.input.rest.data.response.GenericResponse;
import com.auth.auth_service.user.domain.model.User;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
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
    private final VerifiedEmailUseCase verifiedEmailUseCase;

    private final AuthRestMapper authRestMapper;

    @Value("${jwt.expired-date-refresh}")
    private long EXPIRED_DATE_REFRESH;

    // --- HELPER PARA CREAR COOKIES ---
    private ResponseCookie buildCookie(String name, String value, long maxAge) {
        return ResponseCookie.from(name, value)
                .httpOnly(true)
                .secure(false)
                .path("/")
                .maxAge(maxAge)
                .sameSite("Lax")
                .build();
    }

    @PostMapping(value = "/sign_up")
    public ResponseEntity<GenericResponse<String>> signUp(@RequestBody @Valid SignUpRequest request) {
        User user = authRestMapper.toUser(request);
        user.getRole().setEnumName(request.getRole());
        Authentication result = signUpUseCase.signUp(user);
        AuthResponse authComponents = authRestMapper.toAuthResponse(result);

        long refreshCookieAge = (EXPIRED_DATE_REFRESH / 1000);

        ResponseCookie accessCookie = buildCookie("accessToken", authComponents.getAccessToken(), refreshCookieAge);
        ResponseCookie refreshCookie = buildCookie("refreshToken", authComponents.getRefreshToken(), refreshCookieAge);

        GenericResponse<String> response = new GenericResponse<>(true, HttpStatus.CREATED.getReasonPhrase(), "Sign up successful.");

        return ResponseEntity.status(HttpStatus.CREATED)
                .header(HttpHeaders.SET_COOKIE, accessCookie.toString())
                .header(HttpHeaders.SET_COOKIE, refreshCookie.toString())
                .body(response);
    }

    @PostMapping(value = "/sign_in")
    public ResponseEntity<GenericResponse<String>> signIn(@RequestBody @Valid SignInRequest request) {
        User user = authRestMapper.toUser(request);
        Authentication result = signInUseCase.signIn(user);
        AuthResponse authComponents = authRestMapper.toAuthResponse(result);

        long refreshCookieAge = (EXPIRED_DATE_REFRESH / 1000);

        ResponseCookie accessCookie = buildCookie("accessToken", authComponents.getAccessToken(), refreshCookieAge);
        ResponseCookie refreshCookie = buildCookie("refreshToken", authComponents.getRefreshToken(), refreshCookieAge);

        GenericResponse<String> response = new GenericResponse<>(true, HttpStatus.OK.getReasonPhrase(), "Sign in successful.");

        return ResponseEntity.status(HttpStatus.OK)
                .header(HttpHeaders.SET_COOKIE, accessCookie.toString())
                .header(HttpHeaders.SET_COOKIE, refreshCookie.toString())
                .body(response);
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
    public ResponseEntity<GenericResponse<String>> signOut(
        @CookieValue(name = "refreshToken", required = false) String cookieRefreshToken
    ) {
        String tokenToRevoke = cookieRefreshToken;

        if (tokenToRevoke != null) {
            signOutUseCase.signOut(tokenToRevoke);
        }

        ResponseCookie cleanAccess = buildCookie("accessToken", "", 0);
        ResponseCookie cleanRefresh = buildCookie("refreshToken", "", 0);

        GenericResponse<String> response = new GenericResponse<>(true, HttpStatus.OK.getReasonPhrase(), "Sign out successful");

        return ResponseEntity.status(HttpStatus.OK)
                .header(HttpHeaders.SET_COOKIE, cleanAccess.toString())
                .header(HttpHeaders.SET_COOKIE, cleanRefresh.toString())
                .body(response);
    }

    @PostMapping(value = "/sign_out_all")
    public ResponseEntity<GenericResponse<String>> signOutAllSession(@RequestBody AuthResponse request) {
        String message = signOutUseCase.signOutAllSession(request.getRefreshToken());
        
        ResponseCookie cleanAccess = buildCookie("accessToken", "", 0);
        ResponseCookie cleanRefresh = buildCookie("refreshToken", "", 0);

        GenericResponse<String> response = new GenericResponse<>(true, HttpStatus.OK.getReasonPhrase(), message);

        return ResponseEntity.status(HttpStatus.OK)
                .header(HttpHeaders.SET_COOKIE, cleanAccess.toString())
                .header(HttpHeaders.SET_COOKIE, cleanRefresh.toString())
                .body(response);
    }

    @PostMapping(value = "/refresh_token")
    public ResponseEntity<GenericResponse<String>> refreshToken(
            @CookieValue(name = "refreshToken", required = false) String cookieRefreshToken
    ) {
        String tokenToUse = (cookieRefreshToken != null && !cookieRefreshToken.isEmpty()) 
                            ? cookieRefreshToken : null;

        if (tokenToUse == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new GenericResponse<>(false, "Error", "No refresh token provided"));
        }

        Authentication result = refreshTokenUseCase.refreshToken(tokenToUse);
        AuthResponse authComponents = authRestMapper.toAuthResponse(result);

        long refreshCookieAge = (EXPIRED_DATE_REFRESH / 1000);

        ResponseCookie accessCookie = buildCookie("accessToken", authComponents.getAccessToken(), refreshCookieAge);
        ResponseCookie refreshCookie = buildCookie("refreshToken", authComponents.getRefreshToken(), refreshCookieAge);

        GenericResponse<String> response = new GenericResponse<>(true, HttpStatus.OK.getReasonPhrase(), "Token refreshed successfully.");

        return ResponseEntity.status(HttpStatus.OK)
                .header(HttpHeaders.SET_COOKIE, accessCookie.toString())
                .header(HttpHeaders.SET_COOKIE, refreshCookie.toString())
                .body(response);
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

    @PostMapping(value = "/validate_email")
    public ResponseEntity<GenericResponse<String>> validateEmail(@RequestBody VerifiedOrValidateEmailRequest request) {
        User user = authRestMapper.toUser(request);

        String result = verifiedEmailUseCase.validateEmail(user);

        GenericResponse<String> response = new GenericResponse<>(true, HttpStatus.OK.getReasonPhrase(), result);

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PostMapping(value = "/verified_email")
    public ResponseEntity<GenericResponse<String>> verifiedEmail(@RequestBody VerifiedOrValidateEmailRequest request) {
        User user = authRestMapper.toUser(request);

        String result = verifiedEmailUseCase.verifiedEmail(user);

        GenericResponse<String> response = new GenericResponse<>(true, HttpStatus.OK.getReasonPhrase(), result);

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

}
