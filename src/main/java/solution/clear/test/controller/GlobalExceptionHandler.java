package solution.clear.test.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.fge.jsonpatch.JsonPatchException;
import jakarta.validation.ConstraintViolationException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import solution.clear.test.exception.AgeNotValidException;
import solution.clear.test.exception.UserNotFoundException;

@RestControllerAdvice
public class GlobalExceptionHandler {
    
    private static final String REQUEST_EXCEPTION = "Request exception";
    

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleUserNotFound(RuntimeException ex) {
        Map<String, String> result = new HashMap<>();
        result.put(REQUEST_EXCEPTION, ex.getMessage());
        return new ResponseEntity<>(result, HttpStatus.NOT_FOUND);
    }
    
    
    @ExceptionHandler( {AgeNotValidException.class, JsonPatchException.class, 
        JsonProcessingException.class} )
    public ResponseEntity<Map<String, String>> handleRuntime(RuntimeException ex) {
        Map<String, String> result = new HashMap<>();
        result.put(REQUEST_EXCEPTION, ex.getMessage());
        return new ResponseEntity<>(result, HttpStatus.BAD_REQUEST);
    }
    
    
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, List<String>>> 
            handleParameterValidationErrors(MethodArgumentNotValidException ex) {
        Map<String, List<String>> result = new HashMap<>();
        BindingResult br = ex.getBindingResult(); 
        for (FieldError fieldError : br.getFieldErrors()) {
            String fieldName = fieldError.getField();
            List<String> errors = br.getFieldErrors(fieldName)
                                    .stream()
                                    .map(err -> err.getRejectedValue() + " : " + err.getDefaultMessage())
                                    .toList();
            result.put(fieldName, errors);
        }
        return new ResponseEntity<>(result, HttpStatus.BAD_REQUEST);
    }

    
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Map<String, List<String>>> handleValidationErrors(ConstraintViolationException ex) {
        Map<String, List<String>> result = new HashMap<>();
        List<String> errors = ex.getConstraintViolations()
                                    .stream()
                                    .map(cv -> cv == null ? "null" : cv.getPropertyPath() + " " 
                                            + cv.getInvalidValue() + " : " + cv.getMessage())
                                    .toList();
        result.put("Constrain voilations: ", errors);
        return new ResponseEntity<>(result, HttpStatus.BAD_REQUEST);
    }
    
    
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<Map<String, String>> handleMissingParameter(MissingServletRequestParameterException ex) {
        Map<String, String> result = new HashMap<>();
        result.put(REQUEST_EXCEPTION, ex.getParameterType() + ' ' + ex.getParameterName() + " : "
                + (ex.isMissingAfterConversion() ? "present but converted to null" : "not present"));
        return new ResponseEntity<>(result, HttpStatus.BAD_REQUEST);
    }
    
}
