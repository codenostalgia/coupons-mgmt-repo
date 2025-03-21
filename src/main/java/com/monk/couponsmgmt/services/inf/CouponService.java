package com.monk.couponsmgmt.services.inf;

import com.monk.couponsmgmt.dto.ApplicableCouponsDTO;
import com.monk.couponsmgmt.dto.CartInputDTO;
import com.monk.couponsmgmt.dto.CartOutputDTO;
import com.monk.couponsmgmt.dto.CouponDTO;

import java.util.List;

public interface CouponService {

    CouponDTO createCoupon(CouponDTO coupon);

    CouponDTO getCoupon(Integer id);

    List<CouponDTO> getAllCoupons();

    boolean deleteCoupon(Integer id);

    CouponDTO updateCoupon(Integer id, CouponDTO coupon);

    ApplicableCouponsDTO findApplicableCoupons(CartInputDTO cartInputDTO);

    CartOutputDTO applyCoupon(CartInputDTO cartInputDTO, Integer id);
}
