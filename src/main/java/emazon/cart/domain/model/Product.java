package emazon.cart.domain.model;

public class Product {
    private final Long productId;
    private final String productName;
    private final String productDescription;
    private final Integer productQuantity;
    private final Double productPrice;
    private final Integer cartQuantity;
    private final String nextSupplyDate;
    private final Double subtotal;

    private Product(Builder builder) {
        this.productId = builder.productId;
        this.productName = builder.productName;
        this.productDescription = builder.productDescription;
        this.productQuantity = builder.productQuantity;
        this.productPrice = builder.productPrice;
        this.cartQuantity = builder.cartQuantity;
        this.nextSupplyDate = builder.nextSupplyDate;
        this.subtotal = builder.subtotal;
    }

    public static class Builder {
        private Long productId;
        private String productName;
        private String productDescription;
        private Integer productQuantity;
        private Double productPrice;
        private Integer cartQuantity;
        private String nextSupplyDate;
        private Double subtotal;

        public Builder productId(Long productId) {
            this.productId = productId;
            return this;
        }

        public Builder productName(String productName) {
            this.productName = productName;
            return this;
        }

        public Builder productDescription(String productDescription) {
            this.productDescription = productDescription;
            return this;
        }

        public Builder productQuantity(Integer productQuantity) {
            this.productQuantity = productQuantity;
            return this;
        }

        public Builder productPrice(Double productPrice) {
            this.productPrice = productPrice;
            return this;
        }

        public Builder cartQuantity(Integer cartQuantity) {
            this.cartQuantity = cartQuantity;
            return this;
        }

        public Builder nextSupplyDate(String nextSupplyDate) {
            this.nextSupplyDate = nextSupplyDate;
            return this;
        }

        public Builder subtotal(Double subtotal) {
            this.subtotal = subtotal;
            return this;
        }

        public Product build() {
            return new Product(this);
        }
    }

    public Long getProductId() {
        return productId;
    }

    public String getProductName() {
        return productName;
    }

    public String getProductDescription() {
        return productDescription;
    }

    public Integer getProductQuantity() {
        return productQuantity;
    }

    public Double getProductPrice() {
        return productPrice;
    }

    public Integer getCartQuantity() {
        return cartQuantity;
    }

    public String getNextSupplyDate() {
        return nextSupplyDate;
    }

    public Double getSubtotal() {
        return subtotal;
    }
}
