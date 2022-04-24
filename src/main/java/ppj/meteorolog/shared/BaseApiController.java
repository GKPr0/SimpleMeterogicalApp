package ppj.meteorolog.shared;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.HashMap;
import java.util.Map;

public class BaseApiController {

    protected <T> ResponseEntity<?> HandleResult(Result<T> result)
    {
        if(result == null)
            return ResponseEntity.notFound().build();

        if(result.getIsSuccess() && result.getValue() != null)
            return ResponseEntity.ok(result.getValue());

        if(result.getIsSuccess() && result.getValue() == null)
            return ResponseEntity.notFound().build();

        return ResponseEntity.badRequest().body(result.getError());
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    protected Map<String, String> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        return errors;
    }
}
