package com.auth.auth_service.signin;

import com.auth.auth_service.dto.SignInRequest;
import com.auth.auth_service.dto.SignUpRequest;
import com.auth.auth_service.exception.UserNotFoundException;
import com.auth.auth_service.model.User;
import com.auth.auth_service.repository.UserRepository;
import com.auth.auth_service.shared.constant.ErrorMessage;
import com.auth.auth_service.shared.constant.SystemConstant;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class SignInTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ErrorMessage em;

    private final String ROUTE = "/api/auth/sign_in";
    private final String usernameExistent = "Tressa54";
    private final String password = "password";

    @Test
    public void successSignIn() throws Exception {
        //Change the data in the database to run this error
        User user = userRepository.findByUsername(usernameExistent)
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

        mockMvc.perform(
                        MockMvcRequestBuilders.post(ROUTE)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestJson)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value(HttpStatus.OK.getReasonPhrase()))
                .andExpect(jsonPath("$.data.token").exists())
                .andExpect(jsonPath("$.data.token").isNotEmpty())
        ;
    }

    @Test
    public void nullUsernameRequestInSignIn() throws Exception {
        SignInRequest request = new SignInRequest(
                null,
                password
        );

        String requestJson = objectMapper.writeValueAsString(request);

        mockMvc.perform(
                        MockMvcRequestBuilders.post(ROUTE)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestJson)
                )
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.timestamp").isNotEmpty())
                .andExpect(jsonPath("$.status").value(HttpStatus.INTERNAL_SERVER_ERROR.value()))
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.message").isNotEmpty())
                .andExpect(jsonPath("$.path").value("uri="+ROUTE))
        ;
    }

    @Test
    public void emptyUsernameRequestInSignIn() throws Exception{
        SignInRequest request = new SignInRequest(
                "",
                password
        );

        String requestJson = objectMapper.writeValueAsString(request);

        mockMvc.perform(
                        MockMvcRequestBuilders.post(ROUTE)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestJson)
                )
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.timestamp").isNotEmpty())
                .andExpect(jsonPath("$.status").value(HttpStatus.INTERNAL_SERVER_ERROR.value()))
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.message").isNotEmpty())
                .andExpect(jsonPath("$.path").value("uri="+ROUTE))
        ;
    }

    @Test
    public void notFoundUsernameRequestInSignIn() throws Exception{
        SignInRequest request = new SignInRequest(
                "null",
                password
        );

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
                .andExpect(jsonPath("$.message").value(em.BAD_CREDENTIALS))
                .andExpect(jsonPath("$.path").value("uri="+ROUTE))
        ;
    }

    @Test
    public void nullPasswordRequestInSignIn() throws Exception {
        //Change the data in the database to run this error
        User user = userRepository.findByUsername(usernameExistent)
                .orElseThrow(
                        () -> new UserNotFoundException(
                                em.buildUsernameDontExistError(usernameExistent)
                        )
                );

        user.setFailAttempts(0);
        user.setLockTime(null);

        userRepository.save(user);

        SignInRequest request = new SignInRequest(
                usernameExistent,
                null
        );

        String requestJson = objectMapper.writeValueAsString(request);

        mockMvc.perform(
                        MockMvcRequestBuilders.post(ROUTE)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestJson)
                )
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.timestamp").isNotEmpty())
                .andExpect(jsonPath("$.status").value(HttpStatus.INTERNAL_SERVER_ERROR.value()))
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.message").isNotEmpty())
                .andExpect(jsonPath("$.path").value("uri="+ROUTE))
        ;
    }

    @Test
    public void emptyPasswordRequestInSignIn() throws Exception {
        //Change the data in the database to run this error
        User user = userRepository.findByUsername(usernameExistent)
                .orElseThrow(
                        () -> new UserNotFoundException(
                                em.buildUsernameDontExistError(usernameExistent)
                        )
                );

        user.setFailAttempts(0);
        user.setLockTime(null);

        userRepository.save(user);

        SignInRequest request = new SignInRequest(
                usernameExistent,
                ""
        );

        String requestJson = objectMapper.writeValueAsString(request);

        mockMvc.perform(
                        MockMvcRequestBuilders.post(ROUTE)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestJson)
                )
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.timestamp").isNotEmpty())
                .andExpect(jsonPath("$.status").value(HttpStatus.INTERNAL_SERVER_ERROR.value()))
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.message").isNotEmpty())
                .andExpect(jsonPath("$.path").value("uri="+ROUTE))
        ;
    }

    @Test
    public void notMatchPasswordRequestInSignIn() throws Exception{
        //Change the data in the database to run this error
        User user = userRepository.findByUsername(usernameExistent)
                .orElseThrow(
                        () -> new UserNotFoundException(
                                em.buildUsernameDontExistError(usernameExistent)
                        )
                );

        user.setFailAttempts(0);
        user.setLockTime(null);

        userRepository.save(user);

        SignInRequest request = new SignInRequest(
                usernameExistent,
                "null"
        );

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
                .andExpect(jsonPath("$.message").value(em.BAD_CREDENTIALS))
                .andExpect(jsonPath("$.path").value("uri="+ROUTE))
        ;
    }

    @Test
    public void passAttemptToSignIn() throws Exception{
        //Change the data in the database to run this error
        User user = userRepository.findByUsername(usernameExistent)
                .orElseThrow(
                        () -> new UserNotFoundException(
                                em.buildUsernameDontExistError(usernameExistent)
                        )
                );

        user.setFailAttempts(SystemConstant.MAX_FAILED_ATTEMPTS.getValue());
        user.setLockTime(null);

        userRepository.save(user);

        SignInRequest request = new SignInRequest(
                usernameExistent,
                "null"
        );

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
                .andExpect(jsonPath("$.message").value(em.LOCKED_ACCOUNT))
                .andExpect(jsonPath("$.path").value("uri="+ROUTE))
        ;
    }

    @Test
    public void signInWhenAccountWasLocked() throws Exception{
        //Change the data in the database to run this error
        User user = userRepository.findByUsername(usernameExistent)
                .orElseThrow(
                        () -> new UserNotFoundException(
                                em.buildUsernameDontExistError(usernameExistent)
                        )
                );

        user.setFailAttempts(SystemConstant.MAX_FAILED_ATTEMPTS.getValue());
        user.setLockTime(LocalDateTime.now());

        userRepository.save(user);

        SignInRequest request = new SignInRequest(
                usernameExistent,
                "null"
        );

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
                .andExpect(jsonPath("$.message").value(em.LOCKED_ACCOUNT))
                .andExpect(jsonPath("$.path").value("uri="+ROUTE))
        ;
    }

    @Test
    public void afterPassTimeToLockAccountForSoManyAttemptsOfSignIn() throws Exception{
        //Change the data in the database to run this error
        User user = userRepository.findByUsername(usernameExistent)
                .orElseThrow(
                        () -> new UserNotFoundException(
                                em.buildUsernameDontExistError(usernameExistent)
                        )
                );

        user.setFailAttempts(SystemConstant.MAX_FAILED_ATTEMPTS.getValue());
        user.setLockTime(LocalDateTime.now().minusMinutes(SystemConstant.LOCK_DURATION_MINUTES.getValue()+1));

        userRepository.save(user);

        SignInRequest request = new SignInRequest(
                usernameExistent,
                password
        );

        String requestJson = objectMapper.writeValueAsString(request);

        mockMvc.perform(
                        MockMvcRequestBuilders.post(ROUTE)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestJson)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value(HttpStatus.OK.getReasonPhrase()))
                .andExpect(jsonPath("$.data.token").exists())
                .andExpect(jsonPath("$.data.token").isNotEmpty())
        ;
    }
}
