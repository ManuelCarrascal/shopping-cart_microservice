package emazon.cart.ports.application.http.handler;

import emazon.cart.domain.api.ICartServicePort;
import emazon.cart.domain.model.Pagination;
import emazon.cart.domain.model.dto.ProductDetailsCart;
import emazon.cart.ports.application.http.dto.ProductResponse;
import emazon.cart.ports.application.http.mapper.ICartResponseMapper;
import lombok.RequiredArgsConstructor;


@RequiredArgsConstructor
public class CartRestHandler implements ICartRestHandler {

    private final ICartServicePort cartServicePort;
    private final ICartResponseMapper cartMapper;

    @Override
    public Pagination<ProductResponse> findProductIdsByUserId(int page, int size, boolean isAscending, String categoryName, String brandName) {
        Pagination<ProductDetailsCart> productDetailsCartPagination = cartServicePort.findProductIdsByUserId(page, size, isAscending, categoryName, brandName);

        return new Pagination<>(
                productDetailsCartPagination.isAscending(),
                productDetailsCartPagination.getCurrentPage(),
                productDetailsCartPagination.getTotalPages(),
                productDetailsCartPagination.getTotalElements(),
                cartMapper.productDetailsCartListToProductResponseList(productDetailsCartPagination.getContent()),
                productDetailsCartPagination.getTotal()
        );
    }
}