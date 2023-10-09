package hdscode.springrestfull.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;
import hdscode.springrestfull.entity.Contact;
import hdscode.springrestfull.entity.User;
import hdscode.springrestfull.model.ContactResponse;
import hdscode.springrestfull.model.CreateContactRequest;
import hdscode.springrestfull.model.UpdateContactRequest;
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

import java.util.List;
import java.util.UUID;

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
    private AddressRepository addressRepository;


    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

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
            assertEquals("hdscode", contactdb.getUser().getUsername());
            log.info("test create contact success, response : {}", response.getData());
        });
    }

    @Test
    void getContactUnauthorized() throws Exception{
        String id = UUID.randomUUID().toString();

        mockMvc.perform(
                get("/api/contacts/"+id)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-API-TOKEN", "wrongtoken")
        ).andExpectAll(
                status().isUnauthorized()
        ).andDo(result -> {
            WebResponse<Contact> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<WebResponse<Contact>>() {
            });
            assertNotNull(response.getErrors());

            // cek to db
            log.info("test get contact unauthorized, response : {}", response);
        });
    }

    @Test
    void getContactNotFound() throws Exception{

        mockMvc.perform(
                get("/api/contacts/1231")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-API-TOKEN", "testtoken")
        ).andExpectAll(
                status().isNotFound()
        ).andDo(result -> {
            WebResponse<ContactResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });
            assertNotNull(response.getErrors());

            log.info("test get contact not found, response : {}", response);
        });
    }

    @Test
    void getContactSuccess() throws Exception{

        Contact contact = new Contact();
        User user = userRepository.findById("hdscode").orElse(null);
        String id = UUID.randomUUID().toString();
        contact.setId(id);
        contact.setFirstname("hdscode");
        contact.setLastname("vide");
        contact.setEmail("davide@gmail.com");
        contact.setPhone("231441241");
        contact.setUser(user);
        contactRepository.save(contact);


        mockMvc.perform(
                get("/api/contacts/"+id)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-API-TOKEN", "testtoken")
        ).andExpectAll(
                status().isOk()
        ).andDo(result -> {
            WebResponse<ContactResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });
            assertNull(response.getErrors());

            // cek to db
            Contact contactdb = contactRepository.findById(response.getData().getId()).orElse(null);
            assertNotNull(contactdb);

            assertEquals(contactdb.getId(), response.getData().getId());
            assertEquals(contactdb.getFirstname(), response.getData().getFirstName());
            assertEquals(contactdb.getLastname(), response.getData().getLastName());
            assertEquals(contactdb.getEmail(), response.getData().getEmail());
            assertEquals(contactdb.getPhone(), response.getData().getPhone());

        });
    }

    @Test
    void updateContactUnauthorized() throws Exception{
        String id = UUID.randomUUID().toString();

        Contact contact = new Contact();

        mockMvc.perform(
                put("/api/contacts/"+id)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-API-TOKEN", "wrongtoken")
                        .content(objectMapper.writeValueAsString(contact))
        ).andExpectAll(
                status().isUnauthorized()
        ).andDo(result -> {
            WebResponse<Contact> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<WebResponse<Contact>>() {
            });
            assertNotNull(response.getErrors());

            // cek to db
            log.info("test get contact unauthorized, response : {}", response);
        });
    }

    @Test
    void updateContactNotFound() throws Exception{

        Contact contact = new Contact();
        User user = userRepository.findById("hdscode").orElse(null);
        String id = UUID.randomUUID().toString();
        contact.setId(id);
        contact.setFirstname("hdscode");
        contact.setLastname("vide");
        contact.setEmail("davide@gmail.com");
        contact.setPhone("231441241");
        contact.setUser(user);
        contactRepository.save(contact);

        UpdateContactRequest updateContactRequest = new UpdateContactRequest();
        updateContactRequest.setFirstName("update");
        updateContactRequest.setLastName("update");
        updateContactRequest.setPhone("update");
        updateContactRequest.setEmail("update@gmail.com");

        mockMvc.perform(
                put("/api/contacts/1231")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-API-TOKEN", "testtoken")
                        .content(objectMapper.writeValueAsString(updateContactRequest))
        ).andExpectAll(
                status().isNotFound()
        ).andDo(result -> {
            WebResponse<ContactResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });
            assertNotNull(response.getErrors());

            log.info("test update contact not found, response : {}", response);
        });
    }

    @Test
    void updateContactSuccess() throws Exception{

        Contact contact = new Contact();
        User user = userRepository.findById("hdscode").orElse(null);
        String id = UUID.randomUUID().toString();
        contact.setId(id);
        contact.setFirstname("hdscode");
        contact.setLastname("vide");
        contact.setEmail("davide@gmail.com");
        contact.setPhone("231441241");
        contact.setUser(user);
        contactRepository.save(contact);

        UpdateContactRequest updateContactRequest = new UpdateContactRequest();
        updateContactRequest.setFirstName("update");
        updateContactRequest.setLastName("update");
        updateContactRequest.setPhone("update");
        updateContactRequest.setEmail("update@gmail.com");


        mockMvc.perform(
                put("/api/contacts/"+id)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-API-TOKEN", "testtoken")
                        .content(objectMapper.writeValueAsString(updateContactRequest))
        ).andExpectAll(
                status().isOk()
        ).andDo(result -> {
            WebResponse<ContactResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });
            assertNull(response.getErrors());

            // cek to db
            Contact contactDb = contactRepository.findFirstByUserAndId(user, id).orElse(null);
            assertNotNull(contactDb);

            assertEquals(contactDb.getId(), id);
            assertEquals(contactDb.getFirstname(), updateContactRequest.getFirstName());
            assertEquals(contactDb.getLastname(), updateContactRequest.getLastName());
            assertEquals(contactDb.getEmail(), updateContactRequest.getEmail());
            assertEquals(contactDb.getPhone(), updateContactRequest.getPhone());

            // cek response

            assertEquals(id, response.getData().getId());
            assertEquals("update", response.getData().getFirstName());
            assertEquals("update", response.getData().getFirstName());
            assertEquals("update@gmail.com", response.getData().getEmail());
            assertEquals("update", response.getData().getPhone());


            log.info("test update contact success , response : {}", response);
        });
    }

    @Test
    void deleteContactNotFound() throws Exception{

        mockMvc.perform(
                delete("/api/contacts/1231")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-API-TOKEN", "testtoken")
        ).andExpectAll(
                status().isNotFound()
        ).andDo(result -> {
            WebResponse<ContactResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });
            assertNotNull(response.getErrors());

            log.info("test delete contact not found, response : {}", response);
        });
    }

    @Test
    void deleteContactSuccess() throws Exception{

        Contact contact = new Contact();
        User user = userRepository.findById("hdscode").orElse(null);
        String id = UUID.randomUUID().toString();
        contact.setId(id);
        contact.setFirstname("hdscode");
        contact.setLastname("vide");
        contact.setEmail("davide@gmail.com");
        contact.setPhone("231441241");
        contact.setUser(user);
        contactRepository.save(contact);


        mockMvc.perform(
                delete("/api/contacts/"+id)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-API-TOKEN", "testtoken")
        ).andExpectAll(
                status().isOk()
        ).andDo(result -> {
            WebResponse<String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });
            assertNull(response.getErrors());
            assertNotNull(response.getData());

            // cek to db
            Contact contactDb = contactRepository.findById(contact.getId()).orElse(null);
            assertNull(contactDb);

        });
    }

    @Test
    void searchNotFound() throws Exception{
        mockMvc.perform(
                get("/api/contacts")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-API-TOKEN", "testtoken")
        ).andExpectAll(
                status().isOk()
        ).andDo(result -> {
            WebResponse<List<ContactResponse>> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });
            assertNull(response.getErrors());

            assertEquals(0, response.getData().size());
            assertEquals(0, response.getPagingResponse().getTotalPages());
            assertEquals(0, response.getPagingResponse().getCurrentPage());
            assertEquals(10, response.getPagingResponse().getSize());
        });
    }

    @Test
    void searchUsingName() throws Exception{
        User user = userRepository.findById("hdscode").orElse(null);

        for(int i = 0; i < 10; i ++){
            Contact contact = new Contact();
            String id = UUID.randomUUID().toString();
            contact.setId(id);
            contact.setFirstname("hdscode "+i);
            contact.setLastname("vide");
            contact.setEmail("davide@gmail.com");
            contact.setPhone("231441241");
            contact.setUser(user);
            contactRepository.save(contact);
        }

        mockMvc.perform(
                get("/api/contacts")
                        .accept(MediaType.APPLICATION_JSON)
                        .queryParam( "name", "hdscode")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-API-TOKEN", "testtoken")
        ).andExpectAll(
                status().isOk()
        ).andDo(result -> {
            WebResponse<List<ContactResponse>> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });
            assertNull(response.getErrors());

            assertEquals(10, response.getData().size());
            assertEquals(1, response.getPagingResponse().getTotalPages());
            assertEquals(0, response.getPagingResponse().getCurrentPage());
            assertEquals(10, response.getPagingResponse().getSize());
        });
    }

    @Test
    void searchUsingEmail() throws Exception{
        User user = userRepository.findById("hdscode").orElse(null);

        for(int i = 0; i < 10; i ++){
            Contact contact = new Contact();
            String id = UUID.randomUUID().toString();
            contact.setId(id);
            contact.setFirstname("hdscode "+i);
            contact.setLastname("vide");
            contact.setEmail("davide@gmail.com");
            contact.setPhone("231441241");
            contact.setUser(user);
            contactRepository.save(contact);
        }

        mockMvc.perform(
                get("/api/contacts")
                        .accept(MediaType.APPLICATION_JSON)
                        .queryParam( "email", "davide")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-API-TOKEN", "testtoken")
        ).andExpectAll(
                status().isOk()
        ).andDo(result -> {
            WebResponse<List<ContactResponse>> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });
            assertNull(response.getErrors());

            assertEquals(10, response.getData().size());
            assertEquals(1, response.getPagingResponse().getTotalPages());
            assertEquals(0, response.getPagingResponse().getCurrentPage());
            assertEquals(10, response.getPagingResponse().getSize());
        });
    }

    @Test
    void searchUsingPhone() throws Exception{
        User user = userRepository.findById("hdscode").orElse(null);

        for(int i = 0; i < 10; i ++){
            Contact contact = new Contact();
            String id = UUID.randomUUID().toString();
            contact.setId(id);
            contact.setFirstname("hdscode "+i);
            contact.setLastname("vide");
            contact.setEmail("davide@gmail.com");
            contact.setPhone("231441241");
            contact.setUser(user);
            contactRepository.save(contact);
        }

        mockMvc.perform(
                get("/api/contacts")
                        .accept(MediaType.APPLICATION_JSON)
                        .queryParam( "phone", "412")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-API-TOKEN", "testtoken")
        ).andExpectAll(
                status().isOk()
        ).andDo(result -> {
            WebResponse<List<ContactResponse>> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });
            assertNull(response.getErrors());

            assertEquals(10, response.getData().size());
            assertEquals(1, response.getPagingResponse().getTotalPages());
            assertEquals(0, response.getPagingResponse().getCurrentPage());
            assertEquals(10, response.getPagingResponse().getSize());
        });
    }

    @Test
    void searchUsingNameEmailPhone() throws Exception{
        User user = userRepository.findById("hdscode").orElse(null);

        for(int i = 0; i < 10; i ++){
            Contact contact = new Contact();
            String id = UUID.randomUUID().toString();
            contact.setId(id);
            contact.setFirstname("hdscode "+i);
            contact.setLastname("vide");
            contact.setEmail("davide@gmail.com");
            contact.setPhone("231441241");
            contact.setUser(user);
            contactRepository.save(contact);
        }

        mockMvc.perform(
                get("/api/contacts")
                        .accept(MediaType.APPLICATION_JSON)
                        .queryParam( "name", "hdscode")
                        .queryParam("email", "davide")
                        .queryParam( "phone", "412")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-API-TOKEN", "testtoken")
        ).andExpectAll(
                status().isOk()
        ).andDo(result -> {
            WebResponse<List<ContactResponse>> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });
            assertNull(response.getErrors());

            assertEquals(10, response.getData().size());
            assertEquals(1, response.getPagingResponse().getTotalPages());
            assertEquals(0, response.getPagingResponse().getCurrentPage());
            assertEquals(10, response.getPagingResponse().getSize());
        });
    }

    @Test
    void searchUsingNameEmailPhonePage() throws Exception{
        User user = userRepository.findById("hdscode").orElse(null);

        for(int i = 0; i < 10; i ++){
            Contact contact = new Contact();
            String id = UUID.randomUUID().toString();
            contact.setId(id);
            contact.setFirstname("hdscode "+i);
            contact.setLastname("vide");
            contact.setEmail("davide@gmail.com");
            contact.setPhone("231441241");
            contact.setUser(user);
            contactRepository.save(contact);
        }

        mockMvc.perform(
                get("/api/contacts")
                        .accept(MediaType.APPLICATION_JSON)
                        .queryParam( "name", "hdscode")
                        .queryParam("email", "davide")
                        .queryParam( "phone", "412")
                        .queryParam("page", "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-API-TOKEN", "testtoken")
        ).andExpectAll(
                status().isOk()
        ).andDo(result -> {
            WebResponse<List<ContactResponse>> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });
            assertNull(response.getErrors());

            assertEquals(0, response.getData().size());
            assertEquals(1, response.getPagingResponse().getTotalPages());
            assertEquals(1, response.getPagingResponse().getCurrentPage());
            assertEquals(10, response.getPagingResponse().getSize());
        });
    }




}