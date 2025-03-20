package com.monk.couponsmgmt.controllers;

import com.monk.couponsmgmt.dto.CouponDTO;
import com.monk.couponsmgmt.entities.Coupon;
import com.monk.couponsmgmt.services.inf.CouponService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController("/")
public class CouponController {

    @Autowired
    private CouponService couponService;

    @GetMapping("")
    public String hello(){
        return "Hello World!!!";
    }

    @PostMapping("/coupons")
    public ResponseEntity<CouponDTO> createCoupon(@RequestBody CouponDTO coupon){
        CouponDTO createdCoupon = couponService.createCoupon(coupon);
        return new ResponseEntity<>(createdCoupon, HttpStatus.CREATED);
    }

    @GetMapping("/coupons")
    public String getAllCoupons(){
        return "All coupons!!!";
    }

    @GetMapping("/coupons/{id}")
    public String getCoupon(@PathVariable Integer id){
        return "Single Coupon!!!";
    }

    @PutMapping("/coupons/{id}")
    public String updateCoupon(@PathVariable Integer id){
        return "Updated Coupon!!!";
    }

    @DeleteMapping("/coupons/{id}")
    public String deleteCoupon(@PathVariable Integer id){
        return "Delete Coupon!!!";
    }

    @PostMapping("/applicable-coupons")
    public String getApplicableCoupons(){
        return "Get applicable-coupons!!!";
    }
}
