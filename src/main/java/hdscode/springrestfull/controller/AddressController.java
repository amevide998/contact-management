package hdscode.springrestfull.controller;

import hdscode.springrestfull.entity.User;
import hdscode.springrestfull.model.AddressResponse;
import hdscode.springrestfull.model.CreateAddressRequest;
import hdscode.springrestfull.model.UpdateAddressRequest;
import hdscode.springrestfull.model.WebResponse;
import hdscode.springrestfull.service.AddressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class AddressController {

    @Autowired
    private AddressService addressService;

    @PostMapping(value = "/api/contacts/{contactId}/addresses",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public WebResponse<AddressResponse> create(User user,
                                               @RequestBody CreateAddressRequest request,
                                               @PathVariable("contactId") String contactId) {
        request.setContactId(contactId);
        AddressResponse addressResponse = addressService.create(user, request);
        return WebResponse.<AddressResponse>builder().data(addressResponse).build();


    }

    @GetMapping(value = "/api/contacts/{contactId}/addresses/{addressId}",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public WebResponse<AddressResponse> get(User user,
                                               @PathVariable("addressId") String addressId,
                                               @PathVariable("contactId") String contactId) {
        AddressResponse addressResponse = addressService.get(user, contactId, addressId);
        return WebResponse.<AddressResponse>builder().data(addressResponse).build();


    }

    @PutMapping(value = "/api/contacts/{contactId}/addresses/{addressId}",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public WebResponse<AddressResponse> update(User user,
                                               @RequestBody UpdateAddressRequest request,
                                               @PathVariable("contactId") String contactId,
                                               @PathVariable("addressId") String addressId) {
        request.setContactId(contactId);
        request.setAddressId(addressId);

        AddressResponse addressResponse = addressService.update(user, request);
        return WebResponse.<AddressResponse>builder().data(addressResponse).build();


    }

    @DeleteMapping(value = "/api/contacts/{contactId}/addresses/{addressId}",
            produces = MediaType.APPLICATION_JSON_VALUE)
    public WebResponse<String> delete(User user,
                                               @PathVariable("contactId") String contactId,
                                               @PathVariable("addressId") String addressId) {

        addressService.remove(user, contactId, addressId);
        return WebResponse.<String>builder().data("Ok").build();
    }

    @GetMapping(value = "/api/contacts/{contactId}/addresses",
            produces = MediaType.APPLICATION_JSON_VALUE)
    public WebResponse<List<AddressResponse>> delete(User user, @PathVariable("contactId") String contactId) {

        List<AddressResponse> addressList = addressService.list(user, contactId);
        return WebResponse.<List<AddressResponse>>builder().data(addressList).build();
    }







}
