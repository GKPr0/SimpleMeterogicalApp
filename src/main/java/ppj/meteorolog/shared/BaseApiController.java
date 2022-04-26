package ppj.meteorolog.shared;

import org.springframework.http.ResponseEntity;

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
}
