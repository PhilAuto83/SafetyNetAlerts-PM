package net.safety.alerts.exceptions;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.DispatcherServlet;

import java.io.IOException;
import java.time.DateTimeException;
import java.time.format.DateTimeParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@RestControllerAdvice
public class CustomExceptionHandler{


    @ExceptionHandler({ConstraintViolationException.class,MethodArgumentNotValidException.class,
            MedicationOrAllergyFormatException.class, DateTimeException.class, DateTimeParseException.class})
    @ResponseBody
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Object handleBadRequestResponse(Exception exception, HttpServletRequest request) throws IOException {
        Map<String, Object> errorBody = new HashMap<>();
        errorBody.put("timestamp", new Date());
        errorBody.put("status", HttpStatus.BAD_REQUEST.value());
        errorBody.put("method", request.getMethod());
        if(request.getMethod().equals("GET")){
            errorBody.put("params",  request.getParameterMap());
        }
        errorBody.put("path", request.getRequestURL());
        switch (exception) {
            case ConstraintViolationException constraintViolationException -> {
                String messagesWithOutPropertyPath = (String) constraintViolationException.getConstraintViolations().stream()
                        .map(constraintViolation -> {
                            return constraintViolation == null ? "null" : constraintViolation.getMessage();
                        }).collect(Collectors.joining(", "));
                errorBody.put("message", messagesWithOutPropertyPath);
            }
            case MethodArgumentNotValidException invalidArgumentException -> errorBody.put("message", Objects.requireNonNull(invalidArgumentException.getDetailMessageArguments())[1]);
            default -> errorBody.put("message", exception.getMessage());
        }
        return errorBody;
    }


    @ExceptionHandler({StationNumberNotFoundException.class, AddressNotFoundException.class, PersonNotFoundException.class,
            PhoneNotFoundException.class, CityNotFoundException.class, MedicalRecordNotFoundException.class})
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
