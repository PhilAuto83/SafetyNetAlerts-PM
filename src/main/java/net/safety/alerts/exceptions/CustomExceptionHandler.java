package net.safety.alerts.exceptions;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@RestControllerAdvice
public class CustomExceptionHandler{




    @ExceptionHandler({ConstraintViolationException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Object handleValidationConstraintError(ConstraintViolationException ex, HttpServletRequest request) throws IOException {
            String messagesWithOutPropertyPath = (String)ex.getConstraintViolations().stream()
                    .map(constraintViolation -> {
                        return constraintViolation == null ? "null" : constraintViolation.getMessage();
                    }).collect(Collectors.joining(", "));
            Map<String, Object> errorBody = new HashMap<>();
            errorBody.put("timestamp", new Date());
            errorBody.put("status", HttpStatus.BAD_REQUEST.value());
            errorBody.put("message", messagesWithOutPropertyPath);
            errorBody.put("method", request.getMethod());
            errorBody.put("path", request.getRequestURL());
            if(request.getMethod().equals("GET")) {
                errorBody.put("params", request.getParameterMap());
            }
        return errorBody;
    }

    @ExceptionHandler({MethodArgumentNotValidException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Object handleMethodArgumentNotValidException(MethodArgumentNotValidException ex, HttpServletRequest request) throws IOException {

        Map<String, Object> errorBody = new HashMap<>();
        errorBody.put("method", request.getMethod());
        errorBody.put("path", request.getRequestURL());
        errorBody.put("timestamp", new Date());
        errorBody.put("status", HttpStatus.BAD_REQUEST.value());
        errorBody.put("message", Objects.requireNonNull(ex.getDetailMessageArguments())[1]);
        return errorBody;
    }

    @ExceptionHandler({MedicationOrAllergyFormatException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Object handleMedicationOrAllergyMessage(Exception ex, HttpServletRequest request) throws IOException {
        Map<String, Object> errorBody = new HashMap<>();
        errorBody.put("timestamp", new Date());
        errorBody.put("status", HttpStatus.BAD_REQUEST.value());
        errorBody.put("message", ex.getMessage());
        errorBody.put("method", request.getMethod());
        errorBody.put("path", request.getRequestURL());
        if(request.getMethod().equals("GET")){
            errorBody.put("params",  request.getParameterMap());
        }
        return errorBody;
    }


    @ExceptionHandler({StationNumberNotFoundException.class, AddressNotFoundException.class, PersonNotFoundException.class,
            PhoneNotFoundException.class, CityNotFoundException.class})
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Object handleNotFoundMessage(Exception ex, HttpServletRequest request) throws IOException {
        Map<String, Object> errorBody = new HashMap<>();
        errorBody.put("timestamp", new Date());
        errorBody.put("status", HttpStatus.NOT_FOUND.value());
        errorBody.put("message", ex.getMessage());
        errorBody.put("method", request.getMethod());
        errorBody.put("path", request.getRequestURL());
        if(request.getMethod().equals("GET")){
            errorBody.put("params",  request.getParameterMap());
        }
        return errorBody;
    }
}
