package emazon.cart.ports.persistence.mysql.adapter;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import emazon.cart.domain.spi.ISupplyConnectionPersistencePort;
import emazon.cart.domain.util.CartUseCaseConstants;
import emazon.cart.infrastructure.config.feign.ISupplyFeignClient;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class SupplyConnectionAdapter  implements ISupplyConnectionPersistencePort {
    private final ISupplyFeignClient supplyFeignClient;
    private final ObjectMapper objectMapper;

    @Override
    public String getNextSupplyDate(Long productId) {
        try {
            String nextSupplyDateJson = supplyFeignClient.getNextSupplyDate(productId);
            JsonNode jsonNode = objectMapper.readTree(nextSupplyDateJson);
            return jsonNode.get(CartUseCaseConstants.NEXT_SUPPLY_DATE_KEY).asText();
        } catch (Exception e) {
            return null;
        }
    }
}
