package com.management.tab.config.exception;

import com.management.tab.domain.repository.TabContentRepository.TabContentNotFoundException;
import com.management.tab.domain.repository.TabGroupRepository.TabGroupNotFoundException;
import com.management.tab.domain.repository.TabRepository.TabNotFoundException;
import com.management.tab.domain.tab.TabTree.TabNodeNotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

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
}
