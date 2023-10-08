package hdscode.springrestfull.service;

import hdscode.springrestfull.entity.Address;
import hdscode.springrestfull.entity.Contact;
import hdscode.springrestfull.entity.User;
import hdscode.springrestfull.model.AddressResponse;
import hdscode.springrestfull.model.CreateAddressRequest;
import hdscode.springrestfull.repository.AddressRepository;
import hdscode.springrestfull.repository.ContactRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;

@Service
public class AddressService {

    @Autowired
    ContactRepository contactRepository;

    @Autowired
    AddressRepository addressRepository;

    @Autowired
    ValidationService validationService;


    @Transactional
    public AddressResponse create(User user, CreateAddressRequest request){
        validationService.validate(request);

        Contact contact = contactRepository.findFirstByUserAndId(user, request.getContactId())
                .orElseThrow(()-> new ResponseStatusException(HttpStatus.NOT_FOUND, "contact not found"));

        Address address = new Address();
        address.setContact(contact);
        address.setId(UUID.randomUUID().toString());
        address.setCity(request.getCity());
        address.setStreet(request.getStreet());
        address.setCountry(request.getCountry());
        address.setPostalCode(request.getPostalCode());
        address.setProvince(request.getProvince());

        addressRepository.save(address);

        return mapToAddressResponse(address);

    }

    private AddressResponse mapToAddressResponse(Address address) {
        return AddressResponse.builder()
                .id(address.getId())
                .city(address.getCity())
                .country(address.getCountry())
                .postalCode(address.getPostalCode())
                .province(address.getProvince())
                .street(address.getStreet())
                .build();
    }

    @Transactional(readOnly = true)
    public AddressResponse get(User user, String contactId, String addressId){
        Contact contact = contactRepository.findFirstByUserAndId(user, contactId)
                .orElseThrow(()-> new ResponseStatusException(HttpStatus.NOT_FOUND, "contact not found"));

        Address address = addressRepository.findFirstByContactAndId(contact, addressId)
                .orElseThrow(()-> new ResponseStatusException(HttpStatus.NOT_FOUND, "address not found"));

        return mapToAddressResponse(address);
    }
}
