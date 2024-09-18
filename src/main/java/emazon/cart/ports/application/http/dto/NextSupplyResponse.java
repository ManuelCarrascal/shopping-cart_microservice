package emazon.cart.ports.application.http.dto;

import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NextSupplyResponse {
    @Temporal(TemporalType.DATE)
    private String nextSupplyDate;

}