package com.monk.couponsmgmt.services.inf;

import com.monk.couponsmgmt.dto.CouponDTO;
import com.monk.couponsmgmt.entities.Coupon;

public interface CouponService {

    CouponDTO createCoupon(CouponDTO coupon);

    Coupon getCoupon(Long id);
}
