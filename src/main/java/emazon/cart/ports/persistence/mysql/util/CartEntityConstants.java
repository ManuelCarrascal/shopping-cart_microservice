package emazon.cart.ports.persistence.mysql.util;

public class CartEntityConstants {
    public static final String TABLE_NAME = "cart";
    public static final String INDEX_USER_ID = "idx_user_id";
    public static final String COLUMN_LIST_USER_ID = "user_id";
    public static final String COLUMN_CART_ID = "cart_id";
    public static final String COLUMN_PRODUCT_ID = "product_id";
    public static final String COLUMN_USER_ID = "user_id";
    public static final String COLUMN_QUANTITY = "cart_quantity";
    public static final String COLUMN_CREATED_AT = "created_at";
    public static final String COLUMN_UPDATED_AT = "updated_at";

    private CartEntityConstants() {
    }
}
