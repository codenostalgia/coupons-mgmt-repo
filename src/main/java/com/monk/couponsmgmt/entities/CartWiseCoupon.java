package com.monk.couponsmgmt.entities;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@DiscriminatorValue("cart-wise")
public class CartWiseCoupon extends Coupon {

    private double threshold;
    private double discount;
}
