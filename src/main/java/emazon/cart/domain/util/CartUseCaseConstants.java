package emazon.cart.domain.util;

public class CartUseCaseConstants {
    public static final String PRODUCT_NOT_FOUND = "Product not found";
    public static final String INSUFFICIENT_STOCK = "Insufficient stock";
    public static final String ERROR_PARSING_NEXT_SUPPLY_DATE = "Error parsing next supply date";
    public static final String CATEGORIES_LIMIT_EXCEEDED = "Categories limit exceeded for category: ";
    public static final int MAX_CATEGORY_LIMIT = 3;
    public static final String NEXT_SUPPLY_DATE_KEY = "nextSupplyDate";
    public static final int DEFAULT_CATEGORY_COUNT = 0;
    public static final int INCREMENT_CATEGORY_COUNT = 1;

    private CartUseCaseConstants() {
    }
}
