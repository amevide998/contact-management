package hdscode.springrestfull.controller;

import hdscode.springrestfull.entity.User;
import hdscode.springrestfull.model.*;
import hdscode.springrestfull.service.ContactService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class ContactController {

    @Autowired
    private ContactService contactService;

    @PostMapping(path = "/api/contacts", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public WebResponse<ContactResponse> create(User user,@RequestBody CreateContactRequest request){
        ContactResponse response = contactService.create(user, request);
        return WebResponse.<ContactResponse>builder().data(response).build();
    }

    @GetMapping(path = "/api/contacts/{id}", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public WebResponse<ContactResponse> get(User user, @PathVariable String id){
        ContactResponse response = contactService.get(user, id);
        return WebResponse.<ContactResponse>builder().data(response).build();
    }

    @PutMapping(path = "/api/contacts/{id}",produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public WebResponse<ContactResponse> update(User user, @PathVariable String id, @RequestBody UpdateContactRequest request){
        request.setId(id);
        ContactResponse response = contactService.update(user, request);
        return WebResponse.<ContactResponse>builder().data(response).build();

    }

    @DeleteMapping(path = "/api/contacts/{id}", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public WebResponse<String> delete(User user, @PathVariable String id){
        contactService.delete(user, id);
        return WebResponse.<String>builder().data("Ok").build();
    }

    @GetMapping(path = "/api/contacts",
            produces = MediaType.APPLICATION_JSON_VALUE)
    public WebResponse<List<ContactResponse>> search(User user,
                                                     @RequestParam(value = "name", required = false) String name,
                                                     @RequestParam(value = "email", required = false) String email,
                                                     @RequestParam(value = "phone", required = false) String phone,
                                                     @RequestParam(value = "page", required = false, defaultValue = "0") Integer page,
                                                     @RequestParam(value = "size", required = false, defaultValue = "10") Integer size
    ){
        SearchContactRequest searchContactRequest = SearchContactRequest.builder()
                .name(name)
                .email(email)
                .phone(phone)
                .page(page)
                .size(size)
                .build();

        Page<ContactResponse> contactResponse = contactService.search(user, searchContactRequest);
        return WebResponse.<List<ContactResponse>>builder()
                .data(contactResponse.getContent())
                .pagingResponse(
                        PagingResponse.builder()
                                .currentPage(contactResponse.getNumber())
                                .totalPages(contactResponse.getTotalPages())
                                .size(contactResponse.getSize())
                                .build())
                .build();

    }

}
