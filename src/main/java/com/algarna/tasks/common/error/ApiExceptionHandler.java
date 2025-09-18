package com.algarna.tasks.common.error;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.List;

@RestControllerAdvice
public class ApiExceptionHandler {

    //404
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiError> handleIllegalArgument(IllegalArgumentException ex,
                                                          HttpServletRequest request) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiError.of(HttpStatus.NOT_FOUND, request.getRequestURI(), ex.getMessage()));
    }

    // 400 – Body validation (@Valid @RequestBody)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
                                                                 HttpServletRequest req) {
        List<ApiError.FieldViolation> violations = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(this::toViolation)
                .toList();

        ApiError body = ApiError.of(HttpStatus.BAD_REQUEST, req.getRequestURI(),
                "Validation failed", violations);

        return ResponseEntity.badRequest().body(body);
    }

    private ApiError.FieldViolation toViolation(FieldError fe) {
        return new ApiError.FieldViolation(fe.getField(), fe.getDefaultMessage());
    }

    // 400 – Query/path validations (e.g., @Min on @RequestParam or @PathVariable)
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiError> handleConstraintViolation(ConstraintViolationException ex,
                                                              HttpServletRequest req) {
        List<ApiError.FieldViolation> violations = ex.getConstraintViolations().stream()
                .map(v -> new ApiError.FieldViolation(v.getPropertyPath().toString(), v.getMessage()))
                .toList();

        ApiError body = ApiError.of(HttpStatus.BAD_REQUEST, req.getRequestURI(),
                "Constraint violation", violations);

        return ResponseEntity.badRequest().body(body);
    }

    // 400 – Malformed JSON / type mismatches
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiError> handleNotReadable(HttpMessageNotReadableException ex,
                                                      HttpServletRequest req) {
        ApiError body = ApiError.of(HttpStatus.BAD_REQUEST, req.getRequestURI(),
                "Malformed JSON request");
        return ResponseEntity.badRequest().body(body);
    }

    public record ApiError(
            Instant timestamp,
            int status,
            String error,
            String path,
            String message,
            List<FieldViolation> violations
    ) {
        public static ApiError of(HttpStatus status, String path, String message) {
            return new ApiError(Instant.now(), status.value(), status.getReasonPhrase(), path, message, List.of());
        }
        public static ApiError of(HttpStatus status, String path, String message, List<FieldViolation> violations) {
            return new ApiError(Instant.now(), status.value(), status.getReasonPhrase(), path, message, violations);
        }
        public record FieldViolation(String field, String message) {}
    }

}
