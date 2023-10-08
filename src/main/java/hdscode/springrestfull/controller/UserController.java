package hdscode.springrestfull.controller;

import hdscode.springrestfull.entity.User;
import hdscode.springrestfull.model.RegisterUserRequest;
import hdscode.springrestfull.model.UpdateUserRequest;
import hdscode.springrestfull.model.UserResponse;
import hdscode.springrestfull.model.WebResponse;
import hdscode.springrestfull.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.validator.internal.util.logging.Log_$logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping(
            path = "/api/users",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<String> register(@RequestBody RegisterUserRequest request){
        userService.register(request);
        return WebResponse.<String>builder().data("Ok").build();
    }

    @GetMapping(path = "/api/users/current", produces = MediaType.APPLICATION_JSON_VALUE)
    public WebResponse<UserResponse> get(User user){
        UserResponse userResponse = userService.get(user);
        return WebResponse.<UserResponse>builder().data(userResponse).build();
    }

    @PatchMapping(path = "/api/users/current", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public WebResponse<UserResponse> update(User user, @RequestBody UpdateUserRequest request){
        log.info("update user controller - requests : {}", request);
        log.info("update user controller - user : {}", user);
        UserResponse response = userService.update(user, request);
        return WebResponse.<UserResponse>builder().data(response).build();
    }


}
