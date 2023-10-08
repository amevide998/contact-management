package hdscode.springrestfull.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;
import hdscode.springrestfull.entity.Contact;
import hdscode.springrestfull.entity.User;
import hdscode.springrestfull.model.ContactResponse;
import hdscode.springrestfull.model.CreateContactRequest;
import hdscode.springrestfull.model.WebResponse;
import hdscode.springrestfull.repository.ContactRepository;
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
class ContactControllerTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ContactRepository contactRepository;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp(){
        contactRepository.deleteAll();
        userRepository.deleteAll();

        User user = new User();
        user.setUsername("hdscode");
        user.setPassword(BCrypt.hashpw("amevide", BCrypt.gensalt()));
        user.setName("hadin davidi");
        user.setToken("testtoken");
        user.setTokenExpiredAt(System.currentTimeMillis() + 100000);
        userRepository.save(user);
    }

    @Test
    void createContactUnauthorized() throws Exception{
        mockMvc.perform(
                post("/api/contacts")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-API-TOKEN", "wrongtoken")
        ).andExpectAll(
                status().isUnauthorized()
        ).andDo(result -> {
            WebResponse<String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<WebResponse<String>>() {
            });

            assertNotNull(response.getErrors());
            log.info("test create contact unauthorized, response : {}", response);
        });
    }

    @Test
    void createContactEmptyFirstName() throws Exception{
        CreateContactRequest request = new CreateContactRequest();
        request.setFirstName("");
        request.setLastName("");
        request.setPhone("");
        request.setEmail("noed");

        mockMvc.perform(
                post("/api/contacts")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-API-TOKEN", "testtoken")
                        .content(objectMapper.writeValueAsString(request))
        ).andExpectAll(
                status().isBadRequest()
        ).andDo(result -> {
            WebResponse<String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<WebResponse<String>>() {
            });

            assertNotNull(response.getErrors());
            log.info("test create contact empty first name, response : {}", response);
        });
    }

    @Test
    void createContactEmailValidation() throws Exception{
        CreateContactRequest request = new CreateContactRequest();
        request.setFirstName("monkey");
        request.setLastName("luffy");
        request.setEmail("luffy");
        request.setPhone("3123214");

        mockMvc.perform(
                post("/api/contacts")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-API-TOKEN", "testtoken")
                        .content(objectMapper.writeValueAsString(request))
        ).andExpectAll(
                status().isBadRequest()
        ).andDo(result -> {
            WebResponse<String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<WebResponse<String>>() {
            });

            assertNotNull(response.getErrors());
            log.info("test create contact email validation, response : {}", response);
        });
    }

    @Test
    void createContactSuccess() throws Exception{
        CreateContactRequest request = new CreateContactRequest();
        request.setFirstName("monkey");
        request.setLastName("luffy");
        request.setEmail("luffy@gmail.com");
        request.setPhone("3123214");

        mockMvc.perform(
                post("/api/contacts")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-API-TOKEN", "testtoken")
                        .content(objectMapper.writeValueAsString(request))
        ).andExpectAll(
                status().isOk()
        ).andDo(result -> {
            WebResponse<ContactResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<WebResponse<ContactResponse>>() {
            });

            assertNull(response.getErrors());
            Contact contactdb = contactRepository.findById(response.getData().getId()).orElse(null);
            assertNotNull(contactdb);
            assertEquals("monkey", contactdb.getFirstname());
            assertEquals("luffy", contactdb.getLastname());
            assertEquals("luffy@gmail.com", contactdb.getEmail());
            assertEquals("3123214", contactdb.getPhone());
            log.info("test create contact success, response : {}", response.getData());
        });
    }

}