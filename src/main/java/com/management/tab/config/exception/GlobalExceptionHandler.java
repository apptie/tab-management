package com.management.tab.config.exception;

import com.management.tab.application.tab.TabService.TabForbiddenException;
import com.management.tab.config.auth.resolver.AuthCurrentUserIdArgumentResolver.UnauthorizedException;
import com.management.tab.domain.repository.TabContentRepository.TabContentNotFoundException;
import com.management.tab.domain.repository.TabGroupRepository.TabGroupNotFoundException;
import com.management.tab.domain.repository.TabRepository.TabNotFoundException;
import com.management.tab.domain.tab.TabTree.TabNodeNotFoundException;
import com.management.tab.infrastructure.jwt.JwtDecoder.ExpiredTokenException;
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

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ExceptionResponse> handleException(Exception ex) {
        log.warn("Exception : ", ex);

        return ResponseEntity.internalServerError()
                             .body(new ExceptionResponse("EXCEPTION", "서버에 문제가 발생했습니다."));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ExceptionResponse> handleIllegalArgumentException(IllegalArgumentException ex) {
        return ResponseEntity.badRequest()
                             .body(new ExceptionResponse("ILLEGAL_ARGUMENT", ex.getMessage()));
    }

    @ExceptionHandler(TabNodeNotFoundException.class)
    public ResponseEntity<ExceptionResponse> handleTabNodeNotFoundException(TabNodeNotFoundException ex) {
        return ResponseEntity.badRequest()
                             .body(new ExceptionResponse("TAB_NOT_NOT_FOUND", ex.getMessage()));
    }

    @ExceptionHandler(TabContentNotFoundException.class)
    public ResponseEntity<ExceptionResponse> handleTabContentNotFoundException(TabContentNotFoundException ex) {
        return ResponseEntity.badRequest()
                             .body(new ExceptionResponse("TAB_CONTENT_NOT_FOUDN", ex.getMessage()));
    }

    @ExceptionHandler(TabGroupNotFoundException.class)
    public ResponseEntity<ExceptionResponse> handleTabGroupNotFoundException(TabGroupNotFoundException ex) {
        return ResponseEntity.badRequest()
                             .body(new ExceptionResponse("TAB_GROUP_NOT_FOUND", ex.getMessage()));
    }

    @ExceptionHandler(TabNotFoundException.class)
    public ResponseEntity<ExceptionResponse> handleTabNotFoundException(TabNotFoundException ex) {
        return ResponseEntity.badRequest()
                             .body(new ExceptionResponse("TAB_NOT_FOUDN", ex.getMessage()));
    }

    @ExceptionHandler(TabForbiddenException.class)
    public ResponseEntity<ExceptionResponse> handleTabForbiddenException(TabForbiddenException ex) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                             .body(new ExceptionResponse("TAB_FORBIDDEN", ex.getMessage()));
    }

    @ExceptionHandler(DataAccessException.class)
    public ResponseEntity<ExceptionResponse> handleDataAccessException(DataAccessException ex) {
        log.warn("DataAccessException : ", ex);

        return ResponseEntity.internalServerError()
                             .body(new ExceptionResponse("EXCEPTION", "서버에 문제가 발생했습니다."));
    }

    @ExceptionHandler(InvalidTokenException.class)
    public ResponseEntity<ExceptionResponse> handleInvalidTokenException(InvalidTokenException ex) {
        return ResponseEntity.badRequest()
                             .body(new ExceptionResponse("INVALID_TOKEN", "유효한 토큰이 아닙니다."));
    }

    @ExceptionHandler(FailedEncodeTokenException.class)
    public ResponseEntity<ExceptionResponse> handleFailedEncodeTokenException(FailedEncodeTokenException ex) {
        log.warn("FailedEncodeTokenException : ", ex);

        return ResponseEntity.internalServerError()
                             .body(new ExceptionResponse("EXCEPTION", "서버에 문제가 발생했습니다."));
    }

    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<ExceptionResponse> handleUnauthorizedException(UnauthorizedException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                             .body(new ExceptionResponse("UNAUTHORIZED", "권한이 없습니다."));
    }

    @ExceptionHandler(ExpiredTokenException.class)
    public ResponseEntity<ExceptionResponse> handleExpiredTokenException(ExpiredTokenException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                             .body(new ExceptionResponse("EXPIRED_TOKEN", "만료된 토큰입니다."));
    }

    public record ExceptionResponse(String code, String message) { }
}
