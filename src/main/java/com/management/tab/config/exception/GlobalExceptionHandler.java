package com.management.tab.config.exception;

import com.management.tab.application.tab.TabService.TabForbiddenException;
import com.management.tab.config.auth.resolver.AuthCurrentUserIdArgumentResolver.UnauthorizedException;
import com.management.tab.domain.repository.TabContentRepository.TabContentNotFoundException;
import com.management.tab.domain.repository.TabGroupRepository.TabGroupNotFoundException;
import com.management.tab.domain.repository.TabRepository.TabNotFoundException;
import com.management.tab.domain.tab.TabTree.TabNodeNotFoundException;
import com.management.tab.infrastructure.jwt.JwtDecoder.InvalidTokenException;
import com.management.tab.infrastructure.jwt.JwtEncoder.FailedEncodeTokenException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleIllegalArgumentException(IllegalArgumentException ex) {
        return ResponseEntity.badRequest()
                             .body(ex.getMessage());
    }

    @ExceptionHandler(TabNodeNotFoundException.class)
    public ResponseEntity<String> handleTabNodeNotFoundException(TabNodeNotFoundException ex) {
        return ResponseEntity.badRequest()
                             .body(ex.getMessage());
    }

    @ExceptionHandler(TabContentNotFoundException.class)
    public ResponseEntity<String> handleTabContentNotFoundException(TabContentNotFoundException ex) {
        return ResponseEntity.badRequest()
                             .body(ex.getMessage());
    }

    @ExceptionHandler(TabGroupNotFoundException.class)
    public ResponseEntity<String> handleTabGroupNotFoundException(TabGroupNotFoundException ex) {
        return ResponseEntity.badRequest()
                             .body(ex.getMessage());
    }

    @ExceptionHandler(TabNotFoundException.class)
    public ResponseEntity<String> handleTabNotFoundException(TabNotFoundException ex) {
        return ResponseEntity.badRequest()
                             .body(ex.getMessage());
    }

    @ExceptionHandler(TabForbiddenException.class)
    public ResponseEntity<String> handleTabForbiddenException(TabForbiddenException ex) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                             .body(ex.getMessage());
    }

    @ExceptionHandler(DataAccessException.class)
    public ResponseEntity<String> handleDataAccessException(DataAccessException ex) {
        log.warn("DataAccessException : ", ex);

        return ResponseEntity.internalServerError()
                             .body("서버에 문제가 발생했습니다.");
    }

    @ExceptionHandler(InvalidTokenException.class)
    public ResponseEntity<String> handleInvalidTokenException(InvalidTokenException ex) {
        log.info("InvalidTokenException : ", ex);

        return ResponseEntity.badRequest()
                             .body("유효한 토큰이 아닙니다.");
    }

    @ExceptionHandler(FailedEncodeTokenException.class)
    public ResponseEntity<String> handleFailedEncodeTokenException(FailedEncodeTokenException ex) {
        return ResponseEntity.internalServerError()
                             .body(ex.getMessage());
    }

    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<String> handleUnauthorizedException(UnauthorizedException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                             .body(ex.getMessage());
    }
}
