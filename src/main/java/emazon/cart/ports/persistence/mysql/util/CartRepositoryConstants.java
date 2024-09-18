package emazon.cart.ports.persistence.mysql.util;

public class CartRepositoryConstants {
    public static final String FIND_LAST_MODIFIED_BY_USER_ID_QUERY = "SELECT MAX(c.updatedAt) FROM CartEntity c WHERE c.userId = :userId";
    public static final String USER_ID_PARAM = "userId";

    private CartRepositoryConstants() {
    }
}
