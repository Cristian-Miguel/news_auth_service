package com.auth.auth_service.signup;

import com.auth.auth_service.dto.SignUpRequest;
import com.auth.auth_service.model.User;
import com.auth.auth_service.repository.UserRepository;
import com.auth.auth_service.shared.constant.RoleEnum;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.javafaker.Faker;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.time.LocalDate;
import java.time.ZoneId;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class SignupTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    private final String ROUTE = "/api/auth/sign_up";

    private Faker faker = new Faker();

    private final String email = faker.internet().emailAddress();

    private final String username = faker.name().username();

    private final String password = faker.internet().password();

    private final String firstName = faker.name().firstName();

    private final String lastName = faker.name().lastName();

    private final RoleEnum role = RoleEnum.READERS;

    private final LocalDate birthDate =
            faker.date().past(3650, java.util.concurrent.TimeUnit.DAYS)
                    .toInstant()
                    .atZone(ZoneId.systemDefault()).toLocalDate();

    @Test
    public void successSignUp() throws Exception {

        SignUpRequest request = new SignUpRequest(
                email,
                password,
                username,
                firstName,
                lastName,
                role,
                birthDate
        );

        String requestJson = objectMapper.writeValueAsString(request);

        mockMvc.perform(
                MockMvcRequestBuilders.post(ROUTE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson)
        )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value(HttpStatus.CREATED.getReasonPhrase()))
                .andExpect(jsonPath("$.data.token").exists())
                .andExpect(jsonPath("$.data.token").isNotEmpty())
        ;

    }

    @Test
    public void nullEmailRequestInSignUp() throws Exception {
        SignUpRequest request = new SignUpRequest(
                null,
                password,
                username,
                firstName,
                lastName,
                role,
                birthDate
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
                .andExpect(jsonPath("$.path").value("uri=/api/auth/command/sign_up"))
        ;
    }

    @Test
    public void emptyEmailRequestInSignUp() throws Exception {
        SignUpRequest request = new SignUpRequest(
                "",
                password,
                username,
                firstName,
                lastName,
                role,
                birthDate
        );

        String requestJson = objectMapper.writeValueAsString(request);

        mockMvc.perform(
                        MockMvcRequestBuilders.post(ROUTE)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestJson)
                )
                .andExpect(status().is5xxServerError())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.timestamp").isNotEmpty())
                .andExpect(jsonPath("$.status").value(HttpStatus.INTERNAL_SERVER_ERROR.value()))
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.message").isNotEmpty())
                .andExpect(jsonPath("$.path").value("uri="+ROUTE))
        ;
    }

    @Test
    public void notFormatEmailRequestInSignUp() throws Exception {
        SignUpRequest request = new SignUpRequest(
                "email",
                password,
                username,
                firstName,
                lastName,
                role,
                birthDate
        );

        String requestJson = objectMapper.writeValueAsString(request);

        mockMvc.perform(
                        MockMvcRequestBuilders.post(ROUTE)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestJson)
                )
                .andExpect(status().is5xxServerError())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.timestamp").isNotEmpty())
                .andExpect(jsonPath("$.status").value(HttpStatus.INTERNAL_SERVER_ERROR.value()))
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.message").isNotEmpty())
                .andExpect(jsonPath("$.path").value("uri="+ROUTE))
        ;
    }

    @Test
    public void duplicateEmailRequestInSignUp() throws Exception {
        long id = 1;
        User user = userRepository.findById(id).orElseThrow();

        SignUpRequest request = new SignUpRequest(
                user.getEmail(),
                password,
                username,
                firstName,
                lastName,
                role,
                birthDate
        );

        String requestJson = objectMapper.writeValueAsString(request);

        mockMvc.perform(
                        MockMvcRequestBuilders.post(ROUTE)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestJson)
                )
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.timestamp").isNotEmpty())
                .andExpect(jsonPath("$.status").value(HttpStatus.CONFLICT.value()))
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.message").isNotEmpty())
                .andExpect(jsonPath("$.path").value("uri="+ROUTE))
        ;
    }

    @Test
    public void nullUsernameRequestInSignUp() throws Exception {
        SignUpRequest request = new SignUpRequest(
                email,
                password,
                null,
                firstName,
                lastName,
                role,
                birthDate
        );

        String requestJson = objectMapper.writeValueAsString(request);

        mockMvc.perform(
                        MockMvcRequestBuilders.post(ROUTE)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestJson)
                )
                .andExpect(status().is5xxServerError())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.timestamp").isNotEmpty())
                .andExpect(jsonPath("$.status").value(HttpStatus.INTERNAL_SERVER_ERROR.value()))
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.message").isNotEmpty())
                .andExpect(jsonPath("$.path").value("uri="+ROUTE))
        ;
    }

    @Test
    public void emptyUsernameRequestInSignUp() throws Exception {
        SignUpRequest request = new SignUpRequest(
                email,
                password,
                "",
                firstName,
                lastName,
                role,
                birthDate
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
    public void duplicateUsernameRequestInSignUp() throws Exception {
        long id = 1;
        User user = userRepository.findById(id).orElseThrow();

        SignUpRequest request = new SignUpRequest(
                email,
                password,
                user.getUsername(),
                firstName,
                lastName,
                role,
                birthDate
        );

        String requestJson = objectMapper.writeValueAsString(request);

        mockMvc.perform(
                        MockMvcRequestBuilders.post(ROUTE)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestJson)
                )
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.timestamp").isNotEmpty())
                .andExpect(jsonPath("$.status").value(HttpStatus.CONFLICT.value()))
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.message").isNotEmpty())
                .andExpect(jsonPath("$.path").value("uri="+ROUTE))
        ;
    }

    @Test
    public void nullPasswordRequestInSignUp() throws Exception {
        SignUpRequest request = new SignUpRequest(
                email,
                null,
                username,
                firstName,
                lastName,
                role,
                birthDate
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
    public void emptyPasswordRequestInSignUp() throws Exception {
        SignUpRequest request = new SignUpRequest(
                email,
                "",
                username,
                firstName,
                lastName,
                role,
                birthDate
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
    public void nullFirstnameRequestInSignUp() throws Exception {
        SignUpRequest request = new SignUpRequest(
                email,
                password,
                username,
                null,
                lastName,
                role,
                birthDate
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
    public void emptyFirstnameRequestInSignUp() throws Exception {
        SignUpRequest request = new SignUpRequest(
                email,
                password,
                username,
                "",
                lastName,
                role,
                birthDate
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
    public void nullLastnameRequestInSignUp() throws Exception {
        SignUpRequest request = new SignUpRequest(
                email,
                password,
                username,
                firstName,
                null,
                role,
                birthDate
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
    public void emptyLastnameRequestInSignUp() throws Exception {
        SignUpRequest request = new SignUpRequest(
                email,
                password,
                username,
                firstName,
                "",
                role,
                birthDate
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
    public void nullRoleRequestInSignUp() throws Exception {
        SignUpRequest request = new SignUpRequest(
                email,
                password,
                username,
                firstName,
                lastName,
                null,
                birthDate
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
    public void notAEnumRoleRequestInSignUp() throws Exception {
        String requestJson = "{\n" +
                "   \"email\":\""+email+"\",\n" +
                "   \"password\":\""+password+"\",\n" +
                "   \"username\":\""+username+"\",//\n" +
                "   \"firstName\":\""+firstName+"\",\n" +
                "   \"lastName\":\""+lastName+"\",\n" +
                "   \"role\":\"NOTROLE\",\n" +
                "   \"birthDate\":\"1997-03-17\"\n" +
                "}";

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
    public void nullBirthDateRequestInSignUp() throws Exception {
        SignUpRequest request = new SignUpRequest(
                email,
                password,
                username,
                firstName,
                lastName,
                role,
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
    public void presentBirthDateRequestInSignUp() throws Exception {
        SignUpRequest request = new SignUpRequest(
                email,
                password,
                username,
                null,
                lastName,
                role,
                LocalDate.now()
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
    public void futureBirthDateRequestInSignUp() throws Exception {
        SignUpRequest request = new SignUpRequest(
                email,
                password,
                username,
                null,
                lastName,
                role,
                faker.date().future(3650, java.util.concurrent.TimeUnit.DAYS)
                        .toInstant()
                        .atZone(ZoneId.systemDefault()).toLocalDate()
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

}
