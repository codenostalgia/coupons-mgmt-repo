package com.monk.couponsmgmt.pojos;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class CartWiseDetails extends Details {
    private Integer threshold;
    private Integer discount;
    @JsonProperty("maximum_discount")
    private Integer maximumDiscount = Integer.MAX_VALUE;
}
