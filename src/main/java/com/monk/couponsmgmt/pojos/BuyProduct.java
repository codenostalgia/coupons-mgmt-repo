package com.monk.couponsmgmt.pojos;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class BuyProduct {
    @JsonProperty("product_id")
    private Integer productId;
    private Integer quantity;
}