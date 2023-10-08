package hdscode.springrestfull.service;

import hdscode.springrestfull.entity.User;
import hdscode.springrestfull.model.RegisterUserRequest;
import hdscode.springrestfull.model.UpdateUserRequest;
import hdscode.springrestfull.model.UserResponse;
import hdscode.springrestfull.repository.UserRepository;
import hdscode.springrestfull.security.BCrypt;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.Objects;
import java.util.Set;

@Service
@Slf4j
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ValidationService validationService;

    @Transactional
    public void register(RegisterUserRequest request){
        validationService.validate(request);

        if(userRepository.existsById(request.getUsername())){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"username already registered");
        }
        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(BCrypt.hashpw(request.getPassword(), BCrypt.gensalt()));
        user.setName(request.getName());


        userRepository.save(user);
    }

    public UserResponse get(User user){
        return UserResponse.builder().username(user.getUsername()).name(user.getName()).build();
    }

    @Transactional
    public UserResponse update(User user, UpdateUserRequest request){

        log.info("update user - update user request : {} ", request);
        if(Objects.nonNull(request.getPassword())){
            user.setPassword(BCrypt.hashpw(request.getPassword(), BCrypt.gensalt()));
        }
        if(Objects.nonNull(request.getName())){
            user.setName(request.getName());
        }

        userRepository.save(user);

        UserResponse userResponse = UserResponse.builder().name(user.getName()).username(user.getUsername()).build();

        log.info("update user - user response : {} - ",userResponse);

        return userResponse;

    }

}
