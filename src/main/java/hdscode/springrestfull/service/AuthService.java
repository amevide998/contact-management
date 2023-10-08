package hdscode.springrestfull.service;

import hdscode.springrestfull.entity.User;
import hdscode.springrestfull.model.LoginUserRequest;
import hdscode.springrestfull.model.TokenResponse;
import hdscode.springrestfull.repository.UserRepository;
import hdscode.springrestfull.security.BCrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ValidationService validationService;


    @Transactional
    public TokenResponse login(LoginUserRequest request){
        validationService.validate(request);

        User user = userRepository.findById(request.getUsername())
                .orElseThrow(()-> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "username and password doesn't match"));

        if(BCrypt.checkpw(request.getPassword(), user.getPassword())){
            user.setToken(UUID.randomUUID().toString());
            user.setTokenExpiredAt(next30Days());
            userRepository.save(user);

            return TokenResponse.builder().token(user.getToken()).ExpiredAt(user.getTokenExpiredAt()).build();
        }else{
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "username and password doesn't match");
        }

    }

    private Long next30Days(){
        return System.currentTimeMillis() + (1000 * 60 * 24 * 30);
    }
}
