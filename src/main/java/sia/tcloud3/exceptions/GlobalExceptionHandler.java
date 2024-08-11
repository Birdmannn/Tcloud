package sia.tcloud3.exceptions;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.security.SignatureException;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AccountStatusException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import sia.tcloud3.service.DesignService.DesignTacoException;
import sia.tcloud3.service.OrderService.OrderServiceException;
import sia.tcloud3.service.checkout.PaystackServiceImpl.PaymentException;

import java.util.Arrays;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ProblemDetail handleSecurityException(Exception exception) {
        ProblemDetail errorDetail = null;

        // TODO send this stack trace to an observability tool
        exception.printStackTrace();

        String title = "description";
        String subject = null;
        int code = 403;

        if (exception instanceof BadCredentialsException) {
            subject = "The username or password is incorrect";
            code = 401;

        } else if (exception instanceof AccountStatusException) {
            subject = "The account is locked.";

        } else if (exception instanceof AccessDeniedException) {
            subject = "You are not authorized to access this resource";

        } else if (exception instanceof SignatureException) {
            subject = "The JWT signature is invalid";

        } else if (exception instanceof ExpiredJwtException) {
            subject = "The JWT Token has expired";

        } else if (exception instanceof DesignTacoException) {
            subject = exception.getMessage();
            code = 401;
        } else {
            subject = "Unknown internal server error.";
            code = 500;
        }

        errorDetail = ProblemDetail.forStatusAndDetail(HttpStatusCode.valueOf(code), exception.getMessage());
        errorDetail.setProperty(title, subject);
        return errorDetail;
    }

    @ExceptionHandler(DesignTacoException.class)
    public ResponseEntity<String> handleDesignException(DesignTacoException e) {
        return ResponseEntity.badRequest().body(e.getMessage() + "\n" + Arrays.toString(e.getStackTrace()));
    }

    @ExceptionHandler(OrderServiceException.class)
    public ResponseEntity<String> handleOrderCreationException(OrderServiceException e) {
        // Cross-check
        return new ResponseEntity<>(e.getMessage() + "\n" + Arrays.toString(e.getStackTrace()), HttpStatus.valueOf(600));
    }
}
