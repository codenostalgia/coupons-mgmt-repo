package com.monk.couponsmgmt.dto;

import lombok.Data;

@Data
public class CartWiseCouponDTO extends CouponDTO {
    private double threshold;
    private double discount;
}
