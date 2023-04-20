package com.dineshb.projects.usermgmt.portal.advice;

import com.auth0.jwt.exceptions.TokenExpiredException;
import com.dineshb.projects.usermgmt.portal.constants.AdviceConstants;
import com.dineshb.projects.usermgmt.portal.exception.EmailExistException;
import com.dineshb.projects.usermgmt.portal.exception.EmailNotFoundException;
import com.dineshb.projects.usermgmt.portal.exception.UserExistException;
import com.dineshb.projects.usermgmt.portal.exception.UserNotFoundException;
import com.dineshb.projects.usermgmt.portal.model.response.HttpResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.persistence.NoResultException;
import java.io.IOException;
import java.util.Date;
import java.util.Objects;

import static com.dineshb.projects.usermgmt.portal.constants.AdviceConstants.*;
import static org.springframework.http.HttpStatus.*;

@Slf4j
@ControllerAdvice
public class UserControllerAdvice {

    @ExceptionHandler(value = DisabledException.class)
    private ResponseEntity<HttpResponse> accountDisabledExceptionHandler() {
        return buildResponseEntityWith(BAD_REQUEST, ACCOUNT_DISABLED);
    }

    @ExceptionHandler(value = BadCredentialsException.class)
    private ResponseEntity<HttpResponse> badCredentialsExceptionHandler() {
        return buildResponseEntityWith(BAD_REQUEST, INCORRECT_CREDENTIALS);
    }

    @ExceptionHandler(value = AccessDeniedException.class)
    private ResponseEntity<HttpResponse> accessDeniedExceptionHandler() {
        return buildResponseEntityWith(FORBIDDEN, NOT_ENOUGH_PERMISSION);
    }

    @ExceptionHandler(value = LockedException.class)
    private ResponseEntity<HttpResponse> accountLockedExceptionHandler() {
        return buildResponseEntityWith(UNAUTHORIZED, ACCOUNT_LOCKED);
    }

    @ExceptionHandler(value = TokenExpiredException.class)
    private ResponseEntity<HttpResponse> tokenExpiredExceptionHandler(final TokenExpiredException tokenExpiredException) {
        return buildResponseEntityWith(UNAUTHORIZED, tokenExpiredException.getMessage().toUpperCase());
    }

    @ExceptionHandler(value = EmailExistException.class)
    private ResponseEntity<HttpResponse> emailExistExceptionHandler(final EmailExistException emailExistException) {
        return buildResponseEntityWith(BAD_REQUEST, emailExistException.getMessage().toUpperCase());
    }

    @ExceptionHandler(value = UserExistException.class)
    private ResponseEntity<HttpResponse> userExistExceptionHandler(final UserExistException userExistException) {
        return buildResponseEntityWith(BAD_REQUEST, userExistException.getMessage().toUpperCase());
    }

    @ExceptionHandler(value = EmailNotFoundException.class)
    private ResponseEntity<HttpResponse> emailNotFoundExceptionHandler(final EmailNotFoundException emailNotFoundException) {
        return buildResponseEntityWith(BAD_REQUEST, emailNotFoundException.getMessage().toUpperCase());
    }

    @ExceptionHandler(value = UserNotFoundException.class)
    private ResponseEntity<HttpResponse> userNotFoundExceptionHandler(final UserNotFoundException userNotFoundException) {
        return buildResponseEntityWith(BAD_REQUEST, userNotFoundException.getMessage().toUpperCase());
    }

    @ExceptionHandler(value = HttpRequestMethodNotSupportedException.class)
    private ResponseEntity<HttpResponse> methodNotSupportedException(final HttpRequestMethodNotSupportedException httpRequestMethodNotSupportedException) {
        HttpMethod supportedMethod = Objects.requireNonNull(httpRequestMethodNotSupportedException.getSupportedHttpMethods().iterator().next());
        return buildResponseEntityWith(HttpStatus.METHOD_NOT_ALLOWED, String.format(AdviceConstants.METHOD_NOT_ALLOWED, supportedMethod));
    }

    @ExceptionHandler(value = NoResultException.class)
    private ResponseEntity<HttpResponse> notFoundException(final NoResultException noResultException) {
        log.error("MSG='Expected Result Not Found !!', message={}", noResultException.getMessage());
        return buildResponseEntityWith(NOT_FOUND, noResultException.getMessage());
    }

    @ExceptionHandler(value = IOException.class)
    private ResponseEntity<HttpResponse> ioException(final IOException ioException) {
        log.error("MSG='IOException Occurred', message={}", ioException.getMessage());
        return buildResponseEntityWith(INTERNAL_SERVER_ERROR, ERROR_PROCESSING_FILE);
    }

    @ExceptionHandler(value = Exception.class)
    private ResponseEntity<HttpResponse> internalServerErrorExceptionHandler(final Exception exception) {
        log.error("MSG='Exception Occurred', message={}", exception.getMessage().toUpperCase());
        return buildResponseEntityWith(INTERNAL_SERVER_ERROR, INTERNAL_SERVER_ERROR_MSG);
    }

    private ResponseEntity<HttpResponse> buildResponseEntityWith(HttpStatus httpStatus, String message) {
        HttpResponse httpResponse = new HttpResponse(httpStatus.value(), httpStatus.getReasonPhrase().toUpperCase(),
                message.toUpperCase(), httpStatus, new Date());
        return new ResponseEntity(httpResponse, httpStatus);
    }
}
