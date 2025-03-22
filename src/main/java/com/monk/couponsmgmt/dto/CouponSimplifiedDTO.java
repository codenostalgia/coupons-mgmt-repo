package com.monk.couponsmgmt.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class CouponSimplifiedDTO {
    @JsonProperty("coupon_id")
    int id;
    String type;
    int discount;

    public CouponSimplifiedDTO(int id, String type, int discount) {
        this.id = id;
        this.type = type;
        this.discount = discount;
    }
}
