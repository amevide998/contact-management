package hdscode.springrestfull.controller;


import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import hdscode.springrestfull.entity.User;
import hdscode.springrestfull.model.RegisterUserRequest;
import hdscode.springrestfull.model.UpdateUserRequest;
import hdscode.springrestfull.model.UserResponse;
import hdscode.springrestfull.model.WebResponse;
import hdscode.springrestfull.repository.UserRepository;
import hdscode.springrestfull.security.BCrypt;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.MockMvcBuilder.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Slf4j
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp(){
        userRepository.deleteAll();
    }

    @Test
    void testRegisterSuccess() throws Exception {
        RegisterUserRequest request = new RegisterUserRequest();
        request.setUsername("hdscode");
        request.setPassword("amevide");
        request.setName("davide");

        mockMvc.perform(
                post("/api/users")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
        ).andExpect(
                status().isOk()
        ).andDo(result -> {
            WebResponse<String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<WebResponse<String>>() {
            });

            assertEquals("Ok", response.getData());
            log.info("test register success, response : {}", response);
        });

    }

    @Test
    void testRegisterFailed() throws Exception {
        RegisterUserRequest request = new RegisterUserRequest();
        request.setUsername("");
        request.setPassword("");
        request.setName("");

        mockMvc.perform(
                post("/api/users")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
        ).andExpect(
                status().isBadRequest()
        ).andDo(result -> {
            WebResponse<String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<WebResponse<String>>() {
            });

            assertNotNull(response.getErrors());
            log.info("test register failed, response : {}", response);
        });

    }

    @Test
    void testRegisterDuplicate() throws Exception {

        User user = new User();
        user.setUsername("duplicate");
        user.setPassword("password");
        user.setName("duplicate");
        userRepository.save(user);

        RegisterUserRequest request = new RegisterUserRequest();
        request.setUsername("duplicate");
        request.setPassword("password");
        request.setName("duplicate");

        mockMvc.perform(
                post("/api/users")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
        ).andExpect(
                status().isBadRequest()
        ).andDo(result -> {
            WebResponse<String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<WebResponse<String>>() {
            });
            assertNotNull(response.getErrors());
            log.info("test register duplicate, response error : {}", response);
        });

    }

    @Test
    void getUserUnauthorized() throws Exception {
        mockMvc.perform(
                get("/api/users/current")
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-API-TOKEN", "wrongtoken")
        ).andExpectAll(
                status().isUnauthorized()
        ).andDo(result -> {
            WebResponse<String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<WebResponse<String>>() {
            });

            assertNotNull(response.getErrors());
            log.info("test get user unauthorized, response : {}", response);
        });
    }

    @Test
    void getUserTokenNotFound() throws Exception {
        mockMvc.perform(
                get("/api/users/current")
                        .accept(MediaType.APPLICATION_JSON)
        ).andExpectAll(
                status().isUnauthorized()
        ).andDo(result -> {
            WebResponse<String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<WebResponse<String>>() {
            });

            assertNotNull(response.getErrors());
            log.info("test get user token nof found, response : {}", response);
        });
    }

    @Test
    void getUserTokenExpired() throws Exception {

        // Create fake user
        User user = new User();
        user.setUsername("hdscode");
        user.setPassword("password");
        user.setName("hadin davidi");
        user.setToken("usertoken");
        user.setTokenExpiredAt(System.currentTimeMillis() - 999999);
        userRepository.save(user);


        mockMvc.perform(
                get("/api/users/current")
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-API-TOKEN", "usertoken")
        ).andExpectAll(
                status().isUnauthorized()
        ).andDo(result -> {
            WebResponse<String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });

            assertNotNull(response.getErrors());
            log.info("test get user token expired, response : {}", response);
        });
    }

    @Test
    void getUserTokenValid() throws Exception {

        // Create fake user
        User user = new User();
        user.setUsername("hdscode");
        user.setPassword("password");
        user.setName("hadin davidi");
        user.setToken("usertoken");
        user.setTokenExpiredAt(System.currentTimeMillis() + 999999);
        userRepository.save(user);


        mockMvc.perform(
                get("/api/users/current")
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-API-TOKEN", "usertoken")
        ).andExpectAll(
                status().isOk()
        ).andDo(result -> {
            WebResponse<UserResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<WebResponse<UserResponse>>() {
            });

            assertEquals(user.getUsername(), response.getData().getUsername());
            assertEquals(user.getName(), response.getData().getName());
            log.info("test get user token valid, response : {}", response);
        });
    }

    @Test
    void updateUserUnauthorized() throws Exception {

        User user = new User();
        user.setUsername("hdscode");
        user.setPassword("password");
        user.setName("hadin davidi");
        user.setToken("usertoken");
        userRepository.save(user);

        UpdateUserRequest request = new UpdateUserRequest();
        request.setName("hadin sianturi");
        request.setPassword("amevide");

        mockMvc.perform(
                patch("/api/users/current")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new UpdateUserRequest()))
                        .header("X-API-TOKEN", "wrongtoken")
        ).andExpectAll(
                status().isUnauthorized()
        ).andDo(result -> {
            WebResponse<String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<WebResponse<String>>() {
            });

            assertNotNull(response.getErrors());
            log.info("test update user unauthorized, response : {}", response);
        });
    }

    @Test
    void updateUserUpdateName() throws Exception {

        User user = new User();
        user.setUsername("hdscode");
        user.setPassword("password");
        user.setName("hadin davidi");
        user.setToken("usertoken");
        user.setTokenExpiredAt(System.currentTimeMillis() + 10000);
        userRepository.save(user);

        UpdateUserRequest updateUserRequest = new UpdateUserRequest();
        updateUserRequest.setName("hadin sianturi");
        updateUserRequest.setPassword("password");

        log.info("test update - request : {}" ,updateUserRequest);

        mockMvc.perform(
                patch("/api/users/current")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateUserRequest))
                        .header("X-API-TOKEN", "usertoken")
        ).andExpectAll(
                status().isOk()
        ).andDo(result -> {
            WebResponse<UserResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });

            User userDb = userRepository.findById("hdscode").orElseThrow();

            // checking in db
            assertEquals(updateUserRequest.getName(), userDb.getName());
            assertEquals(user.getToken(), userDb.getToken());

            // checking response
            assertNotNull(response.getData().getName());
            assertEquals("hadin sianturi",response.getData().getName());
            log.info("test update user unauthorized, response : {}", response);
        });
    }


    @Test
    void updateUserUpdatePassword() throws Exception {

        User user = new User();
        user.setUsername("hdscode");
        user.setPassword("password");
        user.setName("hadin davidi");
        user.setToken("usertoken");
        user.setTokenExpiredAt(System.currentTimeMillis() + 10000);
        userRepository.save(user);

        UpdateUserRequest updateUserRequest = new UpdateUserRequest();
        updateUserRequest.setPassword("newpassword");

        log.info("test update - request : {}" ,updateUserRequest);

        mockMvc.perform(
                patch("/api/users/current")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateUserRequest))
                        .header("X-API-TOKEN", "usertoken")
        ).andExpectAll(
                status().isOk()
        ).andDo(result -> {
            WebResponse<UserResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });

            User userDb = userRepository.findById("hdscode").orElseThrow();

            // checking in db
            assertEquals(user.getName(), userDb.getName());
            assertEquals(user.getToken(), userDb.getToken());

            // checking password
            assertTrue(BCrypt.checkpw("newpassword", userDb.getPassword()));

            // checking response
            assertNotNull(response.getData().getName());
            assertNotNull(response.getData().getUsername());
            assertEquals(user.getName(), response.getData().getName());
            assertEquals(user.getUsername(), response.getData().getUsername());
            log.info("test update user unauthorized, response : {}", response);
        });
    }

}