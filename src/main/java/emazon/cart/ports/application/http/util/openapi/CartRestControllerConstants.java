package emazon.cart.ports.application.http.util.openapi;

public class CartRestControllerConstants {
    public static final String TAG_NAME = "Cart";
    public static final String TAG_DESCRIPTION = "Cart API";

    public static final String ADD_PRODUCT_SUMMARY = "Add a product to the cart";
    public static final String ADD_PRODUCT_DESCRIPTION = "Adds a product to the cart and returns the updated cart";
    public static final String ADD_PRODUCT_RESPONSE_201_DESCRIPTION = "Product added to cart successfully";
    public static final String ADD_PRODUCT_RESPONSE_400_DESCRIPTION = "Invalid input";
    public static final String CART_REQUEST_DESCRIPTION = "Cart request object";
    public static final String REMOVE_PRODUCT_SUMMARY = "Remove a product from the cart";
    public static final String REMOVE_PRODUCT_DESCRIPTION = "Removes a product from the user's cart based on user ID and product ID";
    public static final String REMOVE_PRODUCT_RESPONSE_200_DESCRIPTION = "Product successfully removed from cart";
    public static final String REMOVE_PRODUCT_RESPONSE_BODY = "Product removed from cart";

    public static final String RESPONSE_CODE_201 = "201";
    public static final String RESPONSE_CODE_400 = "400";
    public static final String RESPONSE_CODE_200 = "200";

    public static final String DEFAULT_PAGE = "0";
    public static final String DEFAULT_SIZE = "1";
    public static final String DEFAULT_IS_ASCENDING = "true";


    private CartRestControllerConstants() {
    }
}
