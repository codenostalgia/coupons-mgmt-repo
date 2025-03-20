package com.monk.couponsmgmt.entities;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@DiscriminatorValue("product-wise")
public class ProductWiseCoupon extends Coupon {
    private Long productId;
    private double discount;
}
