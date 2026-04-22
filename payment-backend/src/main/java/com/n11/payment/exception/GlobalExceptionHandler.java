package com.n11.payment.exception;


import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import com.n11.payment.dto.ErrorResponse;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(AuthException.class)
    public ResponseEntity<ErrorResponse> handleAuth(AuthException ex, HttpServletRequest request){
        return build(HttpStatus.UNAUTHORIZED, " Authentication Failed",
                List.of(ex.getMessage()), request);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(ResourceNotFoundException ex, HttpServletRequest request){
        return build(HttpStatus.NOT_FOUND, "Not Found",
                List.of(ex.getMessage()), request);
    }

    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<ErrorResponse> handleConflict(ConflictException ex, HttpServletRequest request){
        return build(HttpStatus.CONFLICT, "Conflict",
                List.of(ex.getMessage()), request);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException ex, HttpServletRequest request){
        List<String> errors = ex.getBindingResult().getFieldErrors().stream()
                .map(fe ->fe.getField() + ": " + fe.getDefaultMessage())
                .toList();
        return build(HttpStatus.BAD_REQUEST, "Validation Failed", errors, request);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArg(IllegalArgumentException ex, HttpServletRequest request){
        return build(HttpStatus.BAD_REQUEST, "Bad Request",
                List.of(ex.getMessage()), request);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleOther(Exception ex, HttpServletRequest request){
        return build(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error",
                List.of(ex.getMessage()), request);
    }

    private ResponseEntity<ErrorResponse> build(HttpStatus status, String error, List<String> messages, HttpServletRequest request){
        ErrorResponse body = new ErrorResponse( status.value(), error, messages, request.getRequestURI());
        return ResponseEntity.status(status).body(body);
    }
}
