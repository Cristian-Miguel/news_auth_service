package com.auth.auth_service.signout;

import org.junit.jupiter.api.TestInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.auth.auth_service.auth.infrastructure.adapter.input.rest.data.request.SignInRequest;
import com.auth.auth_service.auth.infrastructure.adapter.input.rest.data.response.AuthResponse;
import com.auth.auth_service.shared.infrastructure.adapter.input.rest.data.response.GenericResponse;
import com.auth.auth_service.shared.infrastructure.constant.ErrorMessage;
import com.auth.auth_service.user.domain.exception.UserNotFoundException;
import com.auth.auth_service.user.infrastructure.adapter.output.persistence.entity.UserEntity;
import com.auth.auth_service.user.infrastructure.adapter.output.persistence.repository.UserRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;

@SpringBootTest
@AutoConfigureMockMvc
public class SignOutTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ErrorMessage em;

    private final String ROUTE = "/api/auth/sign_out";
    private final String expiredAccessToken =  "eyJhbGciOiJIUzI1NiJ9.eyJyb2xlIjoiQURNSU5JU1RSQVRPUiIsImVtYWlsIjoiUm9zYV9MZW1rZUBob3RtYWlsLmNvbSIsInN1YiI6Ik1heGllX1RlcnJ5IiwiaWF0IjoxNzQxNDUyNTQ0LCJleHAiOjE3NDE0NTM0NDR9.32SLag2ye-PnhRjWuF49DkzpDnZRbvOtEdjUqEjdJSc";
    private final String expiredRefreshToken = "eyJhbGciOiJIUzI1NiJ9.eyJ1dWlkIjoiYzQyMjEyOTUtZTAwYy00YmUzLTlmZTQtZjAyMjczNDYxMTM4Iiwic3ViIjoiTWF4aWVfVGVycnkiLCJpYXQiOjE3MzkxNDYzNDQsImV4cCI6MTc0MDQ0MjM0NH0.2oo0zjKf--f376mkAkI9oYYMNaGtdbMU-tymvCrABAk";
    private final String malformedAccessToken = "eyJhbGciOiJIUzI1NiJ9.eyJyb2xlIjoiQURNSU5JU1RSQVRPUiIsImVtYWlsIjoiUm9zYV9MZW1rZUBob3RtYWlsLmNvbSIsInN1YiI6Ik1heGllX1RlcnJ5IiwiaWF0IjoxNzQxNDUyNTQ0LCJleHAiOjE3NDE0NTM0NDR9";
    private final String malformedRefreshToken = "eyJhbGciOiJIUzI1NiJ9.eyJ1dWlkIjoiYzQyMjEyOTUtZTAwYy00YmUzLTlmZTQtZjAyMjczNDYxMTM4Iiwic3ViIjoiTWF4aWVfVGVycnkiLCJpYXQiOjE3MzkxNDYzNDQsImV4cCI6MTc0MDQ0MjM0NH0";

    private AuthResponse getAuthResponseOfSignIn() throws Exception {
       final String usernameExistent = "Maxie_Terry";
       final String password = "password";
       final String RouteSignIn = "/api/auth/sign_in";
       UserEntity user = userRepository.findByUsername(usernameExistent)
        .orElseThrow(
                () -> new UserNotFoundException(
                        em.buildUsernameDontExistError(usernameExistent)
                )
        );

        user.setFailAttempts(0);
        user.setLockTime(null);

        SignInRequest request = new SignInRequest(
                usernameExistent,
                password
        );

        String requestJson = objectMapper.writeValueAsString(request);
        
        MvcResult response = mockMvc.perform(
                MockMvcRequestBuilders.post(RouteSignIn)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson)
        )
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value(true))
        .andExpect(jsonPath("$.message").value(HttpStatus.OK.getReasonPhrase()))
        .andExpect(jsonPath("$.data.accessToken").exists())
        .andExpect(jsonPath("$.data.accessToken").isNotEmpty())
        .andExpect(jsonPath("$.data.refreshToken").exists())
        .andExpect(jsonPath("$.data.refreshToken").isNotEmpty())
        .andReturn();

        String responseContent = response.getResponse().getContentAsString();
        ObjectMapper objectMapper = new ObjectMapper();
        GenericResponse<AuthResponse> result = objectMapper.readValue(
                responseContent, 
                new TypeReference<GenericResponse<AuthResponse>>() {}
        );
        
        return result.getData();
    }
    
    @Test
    public void successSignOut(){
        try{
                AuthResponse request = getAuthResponseOfSignIn();

                String requestJson = objectMapper.writeValueAsString(request);

                mockMvc.perform(
                                MockMvcRequestBuilders.post(ROUTE)
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(requestJson)
                        )
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.success").exists())
                        .andExpect(jsonPath("$.success").value(true))
                        .andExpect(jsonPath("$.message").exists())
                        .andExpect(jsonPath("$.message").value(HttpStatus.OK.getReasonPhrase()))
                        .andExpect(jsonPath("$.data").exists())
                        .andExpect(jsonPath("$.data").isNotEmpty())
                        .andExpect(jsonPath("$.data").value("Successful sign out"))
                ;
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    @Test
    public void sendWithoutAccessToken(){
        try{
                AuthResponse request = getAuthResponseOfSignIn();
                request.setAccessToken(null);

                String requestJson = objectMapper.writeValueAsString(request);

                mockMvc.perform(
                                MockMvcRequestBuilders.post(ROUTE)
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(requestJson)
                        )
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.success").exists())
                        .andExpect(jsonPath("$.success").value(true))
                        .andExpect(jsonPath("$.message").exists())
                        .andExpect(jsonPath("$.message").value(HttpStatus.OK.getReasonPhrase()))
                        .andExpect(jsonPath("$.data").exists())
                        .andExpect(jsonPath("$.data").isNotEmpty())
                        .andExpect(jsonPath("$.data").value("Successful sign out"))
                ;
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    @Test
    public void sendWithoutRefreshToken(){
        try{
                AuthResponse request = getAuthResponseOfSignIn();
                request.setRefreshToken(null);

                String requestJson = objectMapper.writeValueAsString(request);

                mockMvc.perform(
                        MockMvcRequestBuilders.post(ROUTE)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestJson)
                )
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.timestamp").isNotEmpty())
                .andExpect(jsonPath("$.status").value(HttpStatus.UNAUTHORIZED.value()))
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.message").isNotEmpty())
                .andExpect(jsonPath("$.path").value("uri="+ROUTE))
                ;
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    @Test
    public void sendWithoutTokens(){
        try{
                AuthResponse request = getAuthResponseOfSignIn();
                request.setAccessToken(null);
                request.setRefreshToken(null);

                String requestJson = objectMapper.writeValueAsString(request);

                mockMvc.perform(
                        MockMvcRequestBuilders.post(ROUTE)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestJson)
                )
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.timestamp").isNotEmpty())
                .andExpect(jsonPath("$.status").value(HttpStatus.UNAUTHORIZED.value()))
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.message").isNotEmpty())
                .andExpect(jsonPath("$.path").value("uri="+ROUTE))
                ;
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    @Test
    public void sendWithAccessTokenMalformedAndNullRefreshToken(){
        try{
                AuthResponse request = getAuthResponseOfSignIn();
                request.setAccessToken(malformedAccessToken);
                request.setRefreshToken(null);

                String requestJson = objectMapper.writeValueAsString(request);

                mockMvc.perform(
                        MockMvcRequestBuilders.post(ROUTE)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestJson)
                )
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.timestamp").isNotEmpty())
                .andExpect(jsonPath("$.status").value(HttpStatus.UNAUTHORIZED.value()))
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.message").isNotEmpty())
                .andExpect(jsonPath("$.path").value("uri="+ROUTE))
                ;
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    @Test
    public void sendWithRefreshTokenMalformedAndNullAccessToken(){
        try{
                AuthResponse request = getAuthResponseOfSignIn();
                request.setAccessToken(null);
                request.setRefreshToken(malformedRefreshToken);

                String requestJson = objectMapper.writeValueAsString(request);

                mockMvc.perform(
                        MockMvcRequestBuilders.post(ROUTE)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestJson)
                )
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.timestamp").isNotEmpty())
                .andExpect(jsonPath("$.status").value(HttpStatus.FORBIDDEN.value()))
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.message").isNotEmpty())
                .andExpect(jsonPath("$.path").value("uri="+ROUTE))
                ;
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    @Test
    public void sendWithAccessTokenMalformedAndValidRefreshToken(){
        try{
            AuthResponse request = getAuthResponseOfSignIn();
            request.setAccessToken(null);

            String requestJson = objectMapper.writeValueAsString(request);

            mockMvc.perform(
                            MockMvcRequestBuilders.post(ROUTE)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(requestJson)
                    )
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").exists())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.message").exists())
                    .andExpect(jsonPath("$.message").value(HttpStatus.OK.getReasonPhrase()))
                    .andExpect(jsonPath("$.data").exists())
                    .andExpect(jsonPath("$.data").isNotEmpty())
                    .andExpect(jsonPath("$.data").value("Successful sign out"))
            ;
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    @Test
    public void sendWithRefreshTokenMalformedAndValidAccessToken(){
        try{
                AuthResponse request = getAuthResponseOfSignIn();
                request.setRefreshToken(malformedRefreshToken);

                String requestJson = objectMapper.writeValueAsString(request);

                mockMvc.perform(
                        MockMvcRequestBuilders.post(ROUTE)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestJson)
                )
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.timestamp").isNotEmpty())
                .andExpect(jsonPath("$.status").value(HttpStatus.FORBIDDEN.value()))
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.message").isNotEmpty())
                .andExpect(jsonPath("$.path").value("uri="+ROUTE))
                ;
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    @Test
    public void sendWithAccessTokenMalformedAndRefreshTokenMalformed(){
        try{
                AuthResponse request = getAuthResponseOfSignIn();
                request.setAccessToken(malformedAccessToken);
                request.setRefreshToken(malformedRefreshToken);

                String requestJson = objectMapper.writeValueAsString(request);

                mockMvc.perform(
                        MockMvcRequestBuilders.post(ROUTE)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestJson)
                )
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.timestamp").isNotEmpty())
                .andExpect(jsonPath("$.status").value(HttpStatus.FORBIDDEN.value()))
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.message").isNotEmpty())
                .andExpect(jsonPath("$.path").value("uri="+ROUTE))
                ;
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    @Test
    public void sendWithAccessTokenExpiredAndNullRefreshToken(){
        try{
                AuthResponse request = getAuthResponseOfSignIn();
                request.setAccessToken(expiredAccessToken);
                request.setRefreshToken(null);

                String requestJson = objectMapper.writeValueAsString(request);

                mockMvc.perform(
                        MockMvcRequestBuilders.post(ROUTE)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestJson)
                )
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.timestamp").isNotEmpty())
                .andExpect(jsonPath("$.status").value(HttpStatus.UNAUTHORIZED.value()))
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.message").isNotEmpty())
                .andExpect(jsonPath("$.path").value("uri="+ROUTE))
                ;
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    @Test
    public void sendWithRefreshTokenExpiredAndNullAccessToken(){
        try{
                AuthResponse request = getAuthResponseOfSignIn();
                request.setAccessToken(null);
                request.setRefreshToken(expiredRefreshToken);

                String requestJson = objectMapper.writeValueAsString(request);

                mockMvc.perform(
                        MockMvcRequestBuilders.post(ROUTE)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestJson)
                )
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.timestamp").isNotEmpty())
                .andExpect(jsonPath("$.status").value(HttpStatus.FORBIDDEN.value()))
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.message").isNotEmpty())
                .andExpect(jsonPath("$.path").value("uri="+ROUTE))
                ;
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    @Test
    public void sendWithAccessTokenExpiredAndValidRefreshToken(){
        try{
            AuthResponse request = getAuthResponseOfSignIn();
            request.setAccessToken(null);

            String requestJson = objectMapper.writeValueAsString(request);

            mockMvc.perform(
                            MockMvcRequestBuilders.post(ROUTE)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(requestJson)
                    )
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").exists())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.message").exists())
                    .andExpect(jsonPath("$.message").value(HttpStatus.OK.getReasonPhrase()))
                    .andExpect(jsonPath("$.data").exists())
                    .andExpect(jsonPath("$.data").isNotEmpty())
                    .andExpect(jsonPath("$.data").value("Successful sign out"))
            ;
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    @Test
    public void sendWithRefreshTokenExpiredAndValidAccessToken(){
        try{
                AuthResponse request = getAuthResponseOfSignIn();
                request.setRefreshToken(expiredRefreshToken);

                String requestJson = objectMapper.writeValueAsString(request);

                mockMvc.perform(
                        MockMvcRequestBuilders.post(ROUTE)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestJson)
                )
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.timestamp").isNotEmpty())
                .andExpect(jsonPath("$.status").value(HttpStatus.FORBIDDEN.value()))
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.message").isNotEmpty())
                .andExpect(jsonPath("$.path").value("uri="+ROUTE))
                ;
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    @Test
    public void sendWithAccessTokenExpiredAndRefreshTokenMalformed(){
        try{
                AuthResponse request = getAuthResponseOfSignIn();
                request.setAccessToken(expiredAccessToken);
                request.setRefreshToken(malformedRefreshToken);

                String requestJson = objectMapper.writeValueAsString(request);

                mockMvc.perform(
                        MockMvcRequestBuilders.post(ROUTE)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestJson)
                )
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.timestamp").isNotEmpty())
                .andExpect(jsonPath("$.status").value(HttpStatus.FORBIDDEN.value()))
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.message").isNotEmpty())
                .andExpect(jsonPath("$.path").value("uri="+ROUTE))
                ;
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    @Test
    public void sendWithRefreshTokenExpiredAndAccessTokenMalformed(){
        try{
                AuthResponse request = getAuthResponseOfSignIn();
                request.setAccessToken(malformedAccessToken);
                request.setRefreshToken(expiredRefreshToken);

                String requestJson = objectMapper.writeValueAsString(request);

                mockMvc.perform(
                        MockMvcRequestBuilders.post(ROUTE)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestJson)
                )
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.timestamp").isNotEmpty())
                .andExpect(jsonPath("$.status").value(HttpStatus.FORBIDDEN.value()))
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.message").isNotEmpty())
                .andExpect(jsonPath("$.path").value("uri="+ROUTE))
                ;
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    @Test
    public void sendWithRefreshTokenExpiredAndAccessTokenExpired(){
        try{
                AuthResponse request = getAuthResponseOfSignIn();
                request.setAccessToken(expiredAccessToken);
                request.setRefreshToken(expiredRefreshToken);

                String requestJson = objectMapper.writeValueAsString(request);

                mockMvc.perform(
                        MockMvcRequestBuilders.post(ROUTE)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestJson)
                )
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.timestamp").isNotEmpty())
                .andExpect(jsonPath("$.status").value(HttpStatus.FORBIDDEN.value()))
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.message").isNotEmpty())
                .andExpect(jsonPath("$.path").value("uri="+ROUTE))
                ;
        }catch(Exception e){
            e.printStackTrace();
        }
    }
}
