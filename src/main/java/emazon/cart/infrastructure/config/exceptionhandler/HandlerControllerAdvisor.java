package emazon.cart.infrastructure.config.exceptionhandler;


import emazon.cart.domain.exception.CategoriesLimitException;
import emazon.cart.domain.exception.InsufficientStockException;
import emazon.cart.domain.exception.NotFoundException;
import emazon.cart.infrastructure.config.utils.HandlerControllerAdvisorConstants;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
public class HandlerControllerAdvisor {

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<String> handleNotFoundException(NotFoundException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Map<String, String>> handleAccessDeniedException() {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(Map.of(HandlerControllerAdvisorConstants.MESSAGE_KEY, HandlerControllerAdvisorConstants.ACCESS_DENIED));
    }

    @ExceptionHandler(InsufficientStockException.class)
    public ResponseEntity<Map<String, Object>> handleInsufficientStockException(InsufficientStockException ex) {
        Map<String, Object> response = Map.of(
                HandlerControllerAdvisorConstants.MESSAGE_KEY, ex.getMessage(),
                HandlerControllerAdvisorConstants.NEXT_SUPPLY_DATE_KEY, ex.getNextSupplyDate()
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(CategoriesLimitException.class)
    public ResponseEntity<Map<String, String>> handleCategoriesLimitException(CategoriesLimitException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of(HandlerControllerAdvisorConstants.MESSAGE_KEY, ex.getMessage()));
    }

}
