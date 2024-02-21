package net.safety.alerts.exceptions;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class CustomExceptionHandler {

    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public Object handleValidationConstraintError(Exception ex, HttpServletRequest request){
        Map<String, Object> errorBody = new HashMap<>();
        errorBody.put("timestamp", new Date());
        errorBody.put("status", HttpStatus.BAD_REQUEST.value());
        errorBody.put("message", ex.getMessage());
        errorBody.put("path", request.getRequestURL()+"?"+request.getQueryString());
        return errorBody;
    }

    @ExceptionHandler({StationNumberNotFoundException.class, AddressNotFoundException.class})
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ResponseBody
    public Object handleNotFoundMessage(Exception ex, HttpServletRequest request){
        Map<String, Object> errorBody = new HashMap<>();
        errorBody.put("timestamp", new Date());
        errorBody.put("status", HttpStatus.NOT_FOUND.value());
        errorBody.put("message", ex.getMessage());
        errorBody.put("path", request.getRequestURL()+"?"+request.getQueryString());
        return errorBody;
    }

}
