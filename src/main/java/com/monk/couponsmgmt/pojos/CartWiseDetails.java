package com.monk.couponsmgmt.pojos;

import lombok.Data;

@Data
public class CartWiseDetails extends Details {
    private Integer threshold;
    private Integer discount;
}
