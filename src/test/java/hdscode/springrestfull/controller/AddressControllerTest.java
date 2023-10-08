package hdscode.springrestfull.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import hdscode.springrestfull.entity.Address;
import hdscode.springrestfull.entity.Contact;
import hdscode.springrestfull.entity.User;
import hdscode.springrestfull.model.AddressResponse;
import hdscode.springrestfull.model.CreateAddressRequest;
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
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.MockMvcBuilder.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Slf4j
class AddressControllerTest {

    @Autowired
    ContactRepository contactRepository;

    @Autowired
    AddressRepository addressRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @BeforeEach
    void setUp(){
        addressRepository.deleteAll();
        contactRepository.deleteAll();
        userRepository.deleteAll();

        User user = new User();
        user.setUsername("hdscode");
        user.setPassword(BCrypt.hashpw("amevide", BCrypt.gensalt()));
        user.setName("hadin davidi");
        user.setToken("testtoken");
        user.setTokenExpiredAt(System.currentTimeMillis() + 100000);
        userRepository.save(user);

        Contact contact = new Contact();
        contact.setId("test1234");
        contact.setFirstname("hdscode");
        contact.setLastname("vide");
        contact.setEmail("davide@gmail.com");
        contact.setPhone("231441241");
        contact.setUser(user);
        contactRepository.save(contact);

    }

    @Test
    void createAddressBadRequest() throws Exception {

        CreateAddressRequest request = new CreateAddressRequest();
        request.setCountry("");

        mockMvc.perform(
                post("/api/contacts/test1234/addresses")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .header("X-API-TOKEN", "testtoken")
        ).andExpectAll(
                status().isBadRequest()
        ).andDo(result -> {
            WebResponse<AddressResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });
            assertNotNull(response.getErrors());
            log.info("test create address bad request, response : {}", response);
        });

    }

    @Test
    void createAddressSuccess() throws Exception {

        CreateAddressRequest request = new CreateAddressRequest();
        request.setCity("tangerang");
        request.setStreet("pagedangan");
        request.setProvince("Banten");
        request.setPostalCode("12345");
        request.setCountry("indonesia");

        mockMvc.perform(
                post("/api/contacts/test1234/addresses")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .header("X-API-TOKEN", "testtoken")
        ).andExpectAll(
                status().isOk()
        ).andDo(result -> {
            WebResponse<AddressResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });
            assertNull(response.getErrors());

            // cek response
            assertEquals("tangerang", response.getData().getCity());
            assertEquals("pagedangan", response.getData().getStreet());
            assertEquals("Banten", response.getData().getProvince());
            assertEquals("12345", response.getData().getPostalCode());
            assertEquals("indonesia", response.getData().getCountry());

            // cek db
            Address addressDb = addressRepository.findById(response.getData().getId()).orElse(null);
            assertNotNull(addressDb);
            assertEquals("tangerang", addressDb.getCity());
            assertEquals("pagedangan", addressDb.getStreet());
            assertEquals("Banten", addressDb.getProvince());
            assertEquals("12345", addressDb.getPostalCode());
            assertEquals("indonesia", addressDb.getCountry());


            log.info("test create address success, response : {}", response.getData());
        });

    }

    @Test
    void getAddressBadRequest() throws Exception {
        mockMvc.perform(
                get("/api/contacts/test1234/addresses/2345")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-API-TOKEN", "testtoken")
        ).andExpectAll(
                status().isNotFound()
        ).andDo(result -> {
            WebResponse<AddressResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });
            assertNotNull(response.getErrors());
            log.info("test create address bad request, response : {}", response);
        });

    }

    @Test
    void getAddressSuccess() throws Exception {

        Contact contact = contactRepository.findById("test1234").orElse(null);
        Address address = new Address();
        address.setId("2345");
        address.setCity("tangerang");
        address.setStreet("pagedangan");
        address.setProvince("Banten");
        address.setPostalCode("12345");
        address.setCountry("indonesia");
        address.setContact(contact);
        addressRepository.save(address);

        mockMvc.perform(
                get("/api/contacts/test1234/addresses/2345")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-API-TOKEN", "testtoken")
        ).andExpectAll(
                status().isOk()
        ).andDo(result -> {
            WebResponse<AddressResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });
            assertNull(response.getErrors());

            // cek response
            assertEquals("2345", response.getData().getId());
            assertEquals("tangerang", response.getData().getCity());
            assertEquals("pagedangan", response.getData().getStreet());
            assertEquals("Banten", response.getData().getProvince());
            assertEquals("12345", response.getData().getPostalCode());
            assertEquals("indonesia", response.getData().getCountry());

            log.info("test create address success, response : {}", response.getData());
        });

    }
}