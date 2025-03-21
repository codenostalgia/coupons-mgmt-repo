package com.monk.couponsmgmt.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.monk.couponsmgmt.pojos.Cart;
import lombok.Data;

@Data
public class CartInputDTO {
    @JsonProperty("cart")
    Cart cart;
}

