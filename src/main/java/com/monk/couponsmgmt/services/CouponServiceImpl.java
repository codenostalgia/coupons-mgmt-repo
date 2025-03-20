package com.monk.couponsmgmt.services;

import com.monk.couponsmgmt.dto.CouponDTO;
import com.monk.couponsmgmt.entities.Coupon;
import com.monk.couponsmgmt.mapper.ObjectMapperUtil;
import com.monk.couponsmgmt.repos.CouponRepository;
import com.monk.couponsmgmt.services.inf.CouponService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CouponServiceImpl implements CouponService {

    @Autowired
    private CouponRepository couponRepository;

    public CouponDTO createCoupon(CouponDTO coupon) {
        try{
            Coupon c = ObjectMapperUtil.dtoToEntity(coupon, Coupon.class);
            return ObjectMapperUtil.entityToDto(couponRepository.save(coupon),CouponDTO.class);
        }catch (Exception e){
            System.out.println(e.getMessage());
        }
        return null;
    }

    public Coupon getCoupon(Long id) {
        return null;
    }
}

