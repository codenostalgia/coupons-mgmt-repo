package com.monk.couponsmgmt.pojos;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class ProductWiseDetails extends Details {
    @JsonProperty("product_id")
    private Integer productId;
    private Integer discount;
}