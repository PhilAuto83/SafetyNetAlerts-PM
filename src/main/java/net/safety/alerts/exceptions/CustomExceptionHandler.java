package net.safety.alerts.exceptions;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.hibernate.validator.internal.IgnoreForbiddenApisErrors;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.servlet.function.ServerRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.*;
import java.util.stream.Collectors;

@RestControllerAdvice
public class CustomExceptionHandler{

    @ExceptionHandler({ConstraintViolationException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Object handleValidationConstraintError(ConstraintViolationException ex, HttpServletRequest request){
            String messagesWithOutPropertyPath = (String)ex.getConstraintViolations().stream()
                    .map(constraintViolation -> {
                        return constraintViolation == null ? "null" : constraintViolation.getMessage();
                    }).collect(Collectors.joining(", "));
            Map<String, Object> errorBody = new HashMap<>();
            errorBody.put("timestamp", new Date());
            errorBody.put("status", HttpStatus.BAD_REQUEST.value());
            errorBody.put("message", messagesWithOutPropertyPath);
            errorBody.put("path", request.getRequestURL() + "?" + request.getQueryString());
            return errorBody;
    }

    @ExceptionHandler({MethodArgumentNotValidException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Object handleMethodArgumentNotValidException(MethodArgumentNotValidException ex, HttpServletRequest request){

        Map<String, Object> errorBody = new HashMap<>();
        errorBody.put("timestamp", new Date());
        errorBody.put("status", HttpStatus.BAD_REQUEST.value());
        errorBody.put("message", Objects.requireNonNull(ex.getDetailMessageArguments())[1]);
        errorBody.put("path", request.getRequestURL() + "?" + request.getQueryString());
        return errorBody;
    }


    @ExceptionHandler({StationNumberNotFoundException.class, AddressNotFoundException.class, PersonNotFoundException.class,
            PhoneNotFoundException.class, CityNotFoundException.class})
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Object handleNotFoundMessage(Exception ex, HttpServletRequest request){
        Map<String, Object> errorBody = new HashMap<>();
        errorBody.put("timestamp", new Date());
        errorBody.put("status", HttpStatus.NOT_FOUND.value());
        errorBody.put("message", ex.getMessage());
        errorBody.put("path", request.getRequestURL()+"?"+request.getQueryString());
        return errorBody;
    }
}
