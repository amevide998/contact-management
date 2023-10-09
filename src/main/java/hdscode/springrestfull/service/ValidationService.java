package hdscode.springrestfull.service;

import hdscode.springrestfull.model.RegisterUserRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Set;

@Service
public class ValidationService {

    @Autowired
    private Validator validator;

    public void validate(Object request){
        Set<ConstraintViolation<Object>> constraintViolations = validator.validate(request);
        if(constraintViolations.size() != 0){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, constraintViolations.iterator().next().getPropertyPath().toString() + ": " + constraintViolations.iterator().next().getMessage());
        }
    }
}
