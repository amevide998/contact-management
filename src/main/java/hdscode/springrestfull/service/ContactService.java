package hdscode.springrestfull.service;

import hdscode.springrestfull.entity.Contact;
import hdscode.springrestfull.entity.User;
import hdscode.springrestfull.model.ContactResponse;
import hdscode.springrestfull.model.CreateContactRequest;
import hdscode.springrestfull.repository.ContactRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Slf4j
public class ContactService {

    @Autowired
    private ContactRepository contactRepository;

    @Autowired
    private ValidationService validationService;

    @Transactional
    public ContactResponse create(User user, CreateContactRequest request){
        log.info("contact service - create : request = {}", request);
        validationService.validate(request);

        Contact contact = new Contact();
        contact.setId(UUID.randomUUID().toString());
        contact.setFirstname(request.getFirstName());
        contact.setLastname(request.getLastName());
        contact.setPhone(request.getPhone());
        contact.setEmail(request.getEmail());
        contact.setUser(user);

        contactRepository.save(contact);

        return ContactResponse.builder()
                .id(contact.getId())
                .firstName(contact.getFirstname())
                .lastName(contact.getLastname())
                .email(contact.getEmail())
                .phone(contact.getPhone())
                .build();

    }
}
