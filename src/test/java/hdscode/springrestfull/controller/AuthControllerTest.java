package hdscode.springrestfull.controller;


import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import hdscode.springrestfull.entity.User;
import hdscode.springrestfull.model.LoginUserRequest;
import hdscode.springrestfull.model.RegisterUserRequest;
import hdscode.springrestfull.model.TokenResponse;
import hdscode.springrestfull.model.WebResponse;
import hdscode.springrestfull.repository.AddressRepository;
import hdscode.springrestfull.repository.ContactRepository;
import hdscode.springrestfull.repository.UserRepository;
import hdscode.springrestfull.security.BCrypt;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.MockMvcBuilder.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Slf4j
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ContactRepository contactRepository;

    @Autowired
    private AddressRepository addressRepository;


    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp(){
        addressRepository.deleteAll();
        contactRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void testLoginSuccess() throws Exception {

        User user = new User();
        user.setName("hadin davidi");
        user.setUsername("hdscode");
        user.setPassword(BCrypt.hashpw("amevide", BCrypt.gensalt()));
        userRepository.save(user);

        LoginUserRequest request = new LoginUserRequest();
        request.setUsername("hdscode");
        request.setPassword("amevide");

        mockMvc.perform(
                post("/api/auth/login")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
        ).andExpectAll(
                status().isOk()
        ).andDo(result -> {
            WebResponse<TokenResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });

            User userDb = userRepository.findById("hdscode").orElse(null);
            assert userDb != null;
            assertNotNull(response.getData().getToken());
            assertNotNull(response.getData().getExpiredAt());
            assertEquals(userDb.getToken(),response.getData().getToken());
            assertEquals(userDb.getTokenExpiredAt(), response.getData().getExpiredAt());
            log.info("test login success, response : {}", response);
        });

    }

    @Test
    void testLoginFailed() throws Exception {

        LoginUserRequest request = new LoginUserRequest();
        request.setUsername("notfound");
        request.setPassword("notfound");

        mockMvc.perform(
                post("/api/auth/login")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
        ).andExpectAll(
                status().isUnauthorized()
        ).andDo(result -> {
            WebResponse<String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });

            assertNotNull(response.getErrors());
            log.info("test login failed user not found, response : {}", response);
        });

    }

    @Test
    void testLoginWrongPassword() throws Exception {

        User user = new User();
        user.setName("hadin davidi");
        user.setUsername("hdscode");
        user.setPassword(BCrypt.hashpw("amevide", BCrypt.gensalt()));
        userRepository.save(user);

        LoginUserRequest request = new LoginUserRequest();
        request.setUsername("hdscode");
        request.setPassword("wrongpassword");

        mockMvc.perform(
                post("/api/auth/login")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
        ).andExpectAll(
                status().isUnauthorized()
        ).andDo(result -> {
            WebResponse<String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });

            assertNotNull(response.getErrors());
            log.info("test login wrong password, response : {}", response);
        });
    }

    @Test
    void userLogoutUnauthorized() throws Exception{
        mockMvc.perform(
                delete("/api/auth/logout")
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpectAll(
                status().isUnauthorized()
        ).andDo(result -> {
            WebResponse<String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<WebResponse<String>>() {
            });

            assertNotNull(response.getErrors());
            log.info("auth test - logout unauthorized response : {}", response);

        });
    }

    @Test
    void userLogoutSuccess() throws Exception{
        User user = new User();
        user.setUsername("testing");
        user.setName("testing");
        user.setPassword(BCrypt.hashpw("testing", BCrypt.gensalt()));
        user.setToken("testing");
        user.setTokenExpiredAt(System.currentTimeMillis() + 10000);
        userRepository.save(user);

        mockMvc.perform(
                delete("/api/auth/logout")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-API-TOKEN", "testing")
        ).andExpectAll(
                status().isOk()
        ).andDo(result -> {
            WebResponse<String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<WebResponse<String>>() {
            });

            assertNull(response.getErrors());
            log.info("auth test - logout unauthorized response : {}", response);

            // CEK IN DB

            User userDB = userRepository.findById("testing").orElse(null);
            assertNotNull(userDB);
            assertNull(userDB.getToken());
            assertNull(userDB.getTokenExpiredAt());

        });
    }

}