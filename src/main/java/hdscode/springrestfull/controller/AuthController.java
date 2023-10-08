package hdscode.springrestfull.controller;

import hdscode.springrestfull.model.LoginUserRequest;
import hdscode.springrestfull.model.TokenResponse;
import hdscode.springrestfull.model.WebResponse;
import hdscode.springrestfull.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping(path = "/api/auth/login", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public WebResponse<TokenResponse> login(@RequestBody LoginUserRequest request){
        TokenResponse token = authService.login(request);
        return WebResponse.<TokenResponse>builder().data(token).build();

    }
}
