package com.monk.couponsmgmt.pojos;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class CouponSimplified {
    @JsonProperty("coupon_id")
    int id;
    String type;
    int discount;

    public CouponSimplified(int id, String type, int discount) {
        this.id = id;
        this.type = type;
        this.discount = discount;
    }
}
