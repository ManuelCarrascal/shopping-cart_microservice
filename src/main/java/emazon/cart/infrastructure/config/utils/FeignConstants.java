package emazon.cart.infrastructure.config.utils;

public class FeignConstants {
    public static final String STOCK_SERVICE_NAME = "stock";
    public static final String STOCK_SERVICE_URL = "http://localhost:9091";
    public static final String TRANSACTION_SERVICE_NAME = "transaction";
    public static final String TRANSACTION_SERVICE_URL = "http://localhost:9094";
    private FeignConstants() {
    }
}
