package hdscode.springrestfull.service;

import hdscode.springrestfull.entity.Contact;
import hdscode.springrestfull.entity.User;
import hdscode.springrestfull.model.ContactResponse;
import hdscode.springrestfull.model.CreateContactRequest;
import hdscode.springrestfull.model.SearchContactRequest;
import hdscode.springrestfull.model.UpdateContactRequest;
import hdscode.springrestfull.repository.ContactRepository;
import hdscode.springrestfull.repository.UserRepository;
import jakarta.persistence.criteria.Predicate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ContactService {
    @Autowired
    private UserRepository userRepository;

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

    @Transactional(readOnly = true)
    public ContactResponse get(User user, String id){
        log.info("contact service - get : id = {}", id);
        log.info("contact service - get : user = {}", user.getUsername());

        // cek to db


        Contact contact = contactRepository.findFirstByUserAndId(user, id).orElseThrow(
                ()-> new ResponseStatusException(HttpStatus.NOT_FOUND, "contact not found")
        );
        return toContactResponse(contact);
    }

    private ContactResponse toContactResponse(Contact contact){
        return ContactResponse.builder()
                .id(contact.getId())
                .firstName(contact.getFirstname())
                .lastName(contact.getLastname())
                .email(contact.getEmail())
                .phone(contact.getPhone())
                .build();
    }

    @Transactional
    public ContactResponse update(User user, UpdateContactRequest request){
        validationService.validate(request);
        Contact contact = contactRepository.findFirstByUserAndId(user, request.getId()).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "contact not found")
        );

        if(Objects.nonNull(request.getFirstName())){
            contact.setFirstname(request.getFirstName());
        }
        if(Objects.nonNull(request.getLastName())){
            contact.setLastname(request.getLastName());
        }
        if(Objects.nonNull(request.getPhone())){
            contact.setPhone(request.getPhone());
        }
        if(Objects.nonNull(request.getEmail())){
            contact.setEmail(request.getEmail());
        }

        contactRepository.save(contact);
        return toContactResponse(contact);
    }

    @Transactional()
    public void delete(User user, String id){
        Contact contact = contactRepository.findFirstByUserAndId(user, id).orElseThrow(
                ()-> new ResponseStatusException(HttpStatus.NOT_FOUND, "contact not found")
        );

        contactRepository.deleteById(contact.getId());
    }

    @Transactional(readOnly = true)
    public Page<ContactResponse> search(User user, SearchContactRequest request){
        Specification<Contact> specification = (root, query, builder) -> {
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(builder.equal(root.get("user"), user));
            if(Objects.nonNull(request.getName())){
                predicates.add(builder.or(
                        builder.like(root.get("firstname"), "%" + request.getName() + "%"),
                        builder.like(root.get("lastname"), "%" + request.getName() + "%")
                ));
            }

            if(Objects.nonNull(request.getEmail())){
                predicates.add(builder.like(root.get("email"),"%" + request.getEmail() + "%"));
            }

            if(Objects.nonNull(request.getPhone())){
                predicates.add(builder.like(root.get("phone"), "%" + request.getPhone() + "%"));
            }

             return query.where(predicates.toArray( new Predicate[]{})).getRestriction();
        };

        Pageable pageable = PageRequest.of(request.getPage(), request.getSize());
        Page<Contact> contacts = contactRepository.findAll(specification, pageable);

        List<ContactResponse> contactResponses = contacts
                .getContent()
                .stream()
                .map(this::toContactResponse)
                .toList();

        return new PageImpl<>(contactResponses, pageable, contacts.getTotalElements());
    }

}
