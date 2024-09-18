package emazon.cart.ports.persistence.mysql.entity;

import emazon.cart.ports.persistence.mysql.util.CartEntityConstants;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Table(name = CartEntityConstants.TABLE_NAME, indexes = {
        @Index(name = CartEntityConstants.INDEX_USER_ID, columnList = CartEntityConstants.COLUMN_LIST_USER_ID)
})
public class CartEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = CartEntityConstants.COLUMN_CART_ID)
    private Long cartId;

    @Column(name = CartEntityConstants.COLUMN_PRODUCT_ID, nullable = false)
    private Long productId;

    @Column(name = CartEntityConstants.COLUMN_USER_ID, nullable = false)
    private Long userId;

    @Column(name = CartEntityConstants.COLUMN_QUANTITY, nullable = false)
    private Integer quantity;

    @Column(name= CartEntityConstants.COLUMN_CREATED_AT, nullable = false)
    private Date createdAt;

    @Column(name= CartEntityConstants.COLUMN_UPDATED_AT, nullable = false)
    private Date updatedAt;
}
