package com.realestate.realestate.exception;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import com.realestate.realestate.dto.error.ErrorResponse;
import com.realestate.realestate.exception.appointment.AppointmentConflictException;
import com.realestate.realestate.exception.appointment.InvalidAppointmentStatusException;
import com.realestate.realestate.exception.auth.AlreadyVerifiedException;
import com.realestate.realestate.exception.auth.EmailNotVerifiedException;
import com.realestate.realestate.exception.auth.TokenExpiredException;
import com.realestate.realestate.exception.common.BadRequestException;
import com.realestate.realestate.exception.common.ConflictException;
import com.realestate.realestate.exception.common.DuplicateResourceException;
import com.realestate.realestate.exception.common.ForbiddenException;
import com.realestate.realestate.exception.common.ResourceNotFoundException;
import com.realestate.realestate.exception.estate.InvalidEstateStatusException;
import com.realestate.realestate.exception.estate.InvalidEstateTypeException;
import com.realestate.realestate.exception.seller.InvalidSellerStatusException;
import com.realestate.realestate.exception.seller.SellerAlreadyExistsException;

import lombok.extern.slf4j.Slf4j;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

        @ExceptionHandler(EmailNotVerifiedException.class)
        public ResponseEntity<ErrorResponse> handleEmailNotVerified(
                        EmailNotVerifiedException ex,
                        WebRequest request) {

                log.warn("Email not verified: {}", ex.getMessage());

                ErrorResponse error = new ErrorResponse(
                                HttpStatus.FORBIDDEN.value(),
                                "Email Not Verified",
                                ex.getMessage(),
                                getPath(request));

                return new ResponseEntity<>(error, HttpStatus.FORBIDDEN);
        }

        @ExceptionHandler(ResourceNotFoundException.class)
        public ResponseEntity<ErrorResponse> handleResourceNotFound(
                        ResourceNotFoundException ex,
                        WebRequest request) {

                log.warn("Resource not found: {}", ex.getMessage());

                ErrorResponse error = new ErrorResponse(
                                HttpStatus.NOT_FOUND.value(),
                                "Not Found",
                                ex.getMessage(),
                                getPath(request));

                return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
        }

        @ExceptionHandler(TokenExpiredException.class)
        public ResponseEntity<ErrorResponse> handleTokenExpired(
                        TokenExpiredException ex,
                        WebRequest request) {

                log.warn("Token expired: {}", ex.getMessage());

                ErrorResponse error = new ErrorResponse(
                                HttpStatus.GONE.value(),
                                "Token Expired",
                                ex.getMessage(),
                                getPath(request));

                return new ResponseEntity<>(error, HttpStatus.GONE);
        }

        @ExceptionHandler(DuplicateResourceException.class)
        public ResponseEntity<ErrorResponse> handleDuplicateResource(
                        DuplicateResourceException ex,
                        WebRequest request) {

                log.warn("Duplicate resource: {}", ex.getMessage());

                ErrorResponse error = new ErrorResponse(
                                HttpStatus.CONFLICT.value(),
                                "Duplicate Resource",
                                ex.getMessage(),
                                getPath(request));

                return new ResponseEntity<>(error, HttpStatus.CONFLICT);
        }

        @ExceptionHandler(AlreadyVerifiedException.class)
        public ResponseEntity<ErrorResponse> handleAlreadyVerified(
                        AlreadyVerifiedException ex,
                        WebRequest request) {

                log.info("Email already verified: {}", ex.getMessage());

                ErrorResponse error = new ErrorResponse(
                                HttpStatus.OK.value(),
                                "Already Verified",
                                ex.getMessage(),
                                getPath(request));

                return new ResponseEntity<>(error, HttpStatus.OK);
        }

        @ExceptionHandler(SellerAlreadyExistsException.class)
        public ResponseEntity<ErrorResponse> handleSellerAlreadyExists(
                        SellerAlreadyExistsException ex,
                        WebRequest request) {

                log.warn("Seller already exists: {}", ex.getMessage());

                ErrorResponse error = new ErrorResponse(
                                HttpStatus.CONFLICT.value(),
                                "Seller Already Exists",
                                ex.getMessage(),
                                getPath(request));

                return new ResponseEntity<>(error, HttpStatus.CONFLICT);
        }

        @ExceptionHandler(InvalidSellerStatusException.class)
        public ResponseEntity<ErrorResponse> handleInvalidSellerStatus(
                        InvalidSellerStatusException ex,
                        WebRequest request) {

                log.warn("Invalid seller status: {}", ex.getMessage());

                ErrorResponse error = new ErrorResponse(
                                HttpStatus.BAD_REQUEST.value(),
                                "Invalid Status",
                                ex.getMessage(),
                                getPath(request));

                return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
        }

        @ExceptionHandler(BadCredentialsException.class)
        public ResponseEntity<ErrorResponse> handleBadCredentials(
                        BadCredentialsException ex,
                        WebRequest request) {

                log.warn("Bad credentials attempt");

                ErrorResponse error = new ErrorResponse(
                                HttpStatus.UNAUTHORIZED.value(),
                                "Unauthorized",
                                "Invalid email or password",
                                getPath(request));

                return new ResponseEntity<>(error, HttpStatus.UNAUTHORIZED);
        }

        @ExceptionHandler(AuthenticationException.class)
        public ResponseEntity<ErrorResponse> handleAuthenticationException(
                        AuthenticationException ex,
                        WebRequest request) {

                log.warn("Authentication failed: {}", ex.getMessage());

                ErrorResponse error = new ErrorResponse(
                                HttpStatus.UNAUTHORIZED.value(),
                                "Unauthorized",
                                "Authentication failed: " + ex.getMessage(),
                                getPath(request));

                return new ResponseEntity<>(error, HttpStatus.UNAUTHORIZED);
        }

        @ExceptionHandler(MethodArgumentNotValidException.class)
        public ResponseEntity<ErrorResponse> handleValidationErrors(
                        MethodArgumentNotValidException ex,
                        WebRequest request) {

                Map<String, String> validationErrors = new HashMap<>();
                ex.getBindingResult().getAllErrors().forEach(error -> {
                        String fieldName = ((FieldError) error).getField();
                        String errorMessage = error.getDefaultMessage();
                        validationErrors.put(fieldName, errorMessage);
                });

                ErrorResponse error = ErrorResponse.builder()
                                .timestamp(java.time.LocalDateTime.now())
                                .status(HttpStatus.BAD_REQUEST.value())
                                .error("Validation Failed")
                                .message("Invalid input data")
                                .path(getPath(request))
                                .validationErrors(validationErrors)
                                .build();

                return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
        }

        @ExceptionHandler(IllegalArgumentException.class)
        public ResponseEntity<ErrorResponse> handleIllegalArgument(
                        IllegalArgumentException ex,
                        WebRequest request) {

                log.warn("Illegal argument: {}", ex.getMessage());

                ErrorResponse error = new ErrorResponse(
                                HttpStatus.BAD_REQUEST.value(),
                                "Bad Request",
                                ex.getMessage(),
                                getPath(request));

                return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
        }

        @ExceptionHandler(RuntimeException.class)
        public ResponseEntity<ErrorResponse> handleRuntimeException(
                        RuntimeException ex,
                        WebRequest request) {

                log.error("Runtime exception: {}", ex.getMessage(), ex);

                ErrorResponse error = new ErrorResponse(
                                HttpStatus.BAD_REQUEST.value(),
                                "Bad Request",
                                ex.getMessage(),
                                getPath(request));

                return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
        }

        @ExceptionHandler(Exception.class)
        public ResponseEntity<ErrorResponse> handleGlobalException(
                        Exception ex,
                        WebRequest request) {

                log.error("Unexpected error: {}", ex.getMessage(), ex);

                ErrorResponse error = new ErrorResponse(
                                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                                "Internal Server Error",
                                "An unexpected error occurred. Please try again later.",
                                getPath(request));

                return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        @ExceptionHandler(InvalidEstateStatusException.class)
        public ResponseEntity<ErrorResponse> handleInvalidEstateStatus(
                        InvalidEstateStatusException ex,
                        WebRequest request) {
                ErrorResponse error = ErrorResponse.builder()
                                .timestamp(LocalDateTime.now())
                                .status(HttpStatus.BAD_REQUEST.value())
                                .error(HttpStatus.BAD_REQUEST.getReasonPhrase())
                                .message(ex.getMessage())
                                .path(request.getDescription(false).replace("uri=", ""))
                                .build();
                return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
        }

        @ExceptionHandler(InvalidEstateTypeException.class)
        public ResponseEntity<ErrorResponse> handleInvalidEstateType(
                        InvalidEstateTypeException ex,
                        WebRequest request) {
                ErrorResponse error = ErrorResponse.builder()
                                .timestamp(LocalDateTime.now())
                                .status(HttpStatus.BAD_REQUEST.value())
                                .error(HttpStatus.BAD_REQUEST.getReasonPhrase())
                                .message(ex.getMessage())
                                .path(request.getDescription(false).replace("uri=", ""))
                                .build();
                return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
        }

        @ExceptionHandler(BadRequestException.class)
        public ResponseEntity<ErrorResponse> handleBadRequest(
                        BadRequestException ex,
                        WebRequest request) {
                ErrorResponse error = ErrorResponse.builder()
                                .timestamp(LocalDateTime.now())
                                .status(HttpStatus.BAD_REQUEST.value())
                                .error(HttpStatus.BAD_REQUEST.getReasonPhrase())
                                .message(ex.getMessage())
                                .path(request.getDescription(false).replace("uri=", ""))
                                .build();
                return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
        }

        @ExceptionHandler(ForbiddenException.class)
        public ResponseEntity<ErrorResponse> handleForbidden(
                        ForbiddenException ex,
                        WebRequest request) {
                ErrorResponse error = ErrorResponse.builder()
                                .timestamp(LocalDateTime.now())
                                .status(HttpStatus.FORBIDDEN.value())
                                .error(HttpStatus.FORBIDDEN.getReasonPhrase())
                                .message(ex.getMessage())
                                .path(request.getDescription(false).replace("uri=", ""))
                                .build();
                return new ResponseEntity<>(error, HttpStatus.FORBIDDEN);
        }

        @ExceptionHandler(ConflictException.class)
        public ResponseEntity<ErrorResponse> handleConflict(
                        ConflictException ex,
                        WebRequest request) {
                ErrorResponse error = ErrorResponse.builder()
                                .timestamp(LocalDateTime.now())
                                .status(HttpStatus.CONFLICT.value())
                                .error(HttpStatus.CONFLICT.getReasonPhrase())
                                .message(ex.getMessage())
                                .path(request.getDescription(false).replace("uri=", ""))
                                .build();
                return new ResponseEntity<>(error, HttpStatus.CONFLICT);
        }

        @ExceptionHandler(InvalidAppointmentStatusException.class)
        public ResponseEntity<ErrorResponse> handleInvalidAppointmentStatus(
                        InvalidAppointmentStatusException ex,
                        WebRequest request) {
                ErrorResponse error = ErrorResponse.builder()
                                .timestamp(LocalDateTime.now())
                                .status(HttpStatus.BAD_REQUEST.value())
                                .error(HttpStatus.BAD_REQUEST.getReasonPhrase())
                                .message(ex.getMessage())
                                .path(request.getDescription(false).replace("uri=", ""))
                                .build();
                return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
        }

        @ExceptionHandler(AppointmentConflictException.class)
        public ResponseEntity<ErrorResponse> handleAppointmentConflict(
                        AppointmentConflictException ex,
                        WebRequest request) {
                ErrorResponse error = ErrorResponse.builder()
                                .timestamp(LocalDateTime.now())
                                .status(HttpStatus.CONFLICT.value())
                                .error(HttpStatus.CONFLICT.getReasonPhrase())
                                .message(ex.getMessage())
                                .path(request.getDescription(false).replace("uri=", ""))
                                .build();
                return new ResponseEntity<>(error, HttpStatus.CONFLICT);
        }

        private String getPath(WebRequest request) {
                return request.getDescription(false).replace("uri=", "");
        }
}
