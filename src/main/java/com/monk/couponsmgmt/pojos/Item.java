package com.monk.couponsmgmt.pojos;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class Item {
    @JsonProperty("product_id")
    Integer productId;
    Integer quantity;
    Integer price;
    @JsonProperty("total_discount")
    Integer totalDiscount = 0;
}
