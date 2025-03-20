package com.monk.couponsmgmt.dto;

import lombok.Data;

@Data
public class ProductWiseCouponDTO extends CouponDTO {
    private Long productId;
    private double discount;
}
