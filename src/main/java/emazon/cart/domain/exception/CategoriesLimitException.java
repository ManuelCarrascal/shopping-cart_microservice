package emazon.cart.domain.exception;

public class CategoriesLimitException  extends RuntimeException {
    public CategoriesLimitException(String message) {
        super(message);
    }
}
