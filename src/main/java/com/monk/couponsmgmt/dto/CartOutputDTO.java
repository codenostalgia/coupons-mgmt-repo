package com.monk.couponsmgmt.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.monk.couponsmgmt.pojos.Cart;
import lombok.Data;

@Data
public class CartOutputDTO {
    @JsonProperty("updated_cart")
    Cart cart;
    @JsonProperty("total_price")
    Integer totalPrice;
    @JsonProperty("total_discount")
    Integer totalDiscount = 0;
    @JsonProperty("final_price")
    Integer finalPrice;
}
