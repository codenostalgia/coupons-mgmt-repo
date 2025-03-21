package com.monk.couponsmgmt.controllers;

import com.monk.couponsmgmt.dto.ApplicableCouponsDTO;
import com.monk.couponsmgmt.dto.CartInputDTO;
import com.monk.couponsmgmt.dto.CartOutputDTO;
import com.monk.couponsmgmt.dto.CouponDTO;
import com.monk.couponsmgmt.services.inf.CouponService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController("/")
public class CouponController {

    @Autowired
    private CouponService couponService;

    @GetMapping("")
    public String hello() {
        return "Hello World!!!";
    }

    @PostMapping("/coupons")
    public ResponseEntity<CouponDTO> createCoupon(@RequestBody CouponDTO cdto) {
        CouponDTO coupon = couponService.createCoupon(cdto);
        return new ResponseEntity<>(coupon, HttpStatus.CREATED);
    }

    @GetMapping("/coupons")
    public ResponseEntity<List<CouponDTO>> getAllCoupons() {
        List<CouponDTO> couponsList = couponService.getAllCoupons();
        return new ResponseEntity<>(couponsList, HttpStatus.OK);
    }

    @GetMapping("/coupons/{id}")
    public ResponseEntity<CouponDTO> getCoupon(@PathVariable Integer id) {
        CouponDTO coupon = couponService.getCoupon(id);
        return new ResponseEntity<>(coupon, HttpStatus.OK);
    }

    @PutMapping("/coupons/{id}")
    public ResponseEntity<CouponDTO> updateCoupon(@PathVariable Integer id, @RequestBody CouponDTO coupon) {
        CouponDTO updatedCoupon = couponService.updateCoupon(id, coupon);
        return new ResponseEntity<>(updatedCoupon, HttpStatus.OK);
    }

    @DeleteMapping("/coupons/{id}")
    public ResponseEntity<Boolean> deleteCoupon(@PathVariable Integer id) {
        boolean isDeleted = couponService.deleteCoupon(id);
        return new ResponseEntity<>(isDeleted, HttpStatus.OK);
    }

    @PostMapping("/applicable-coupons")
    public ResponseEntity<ApplicableCouponsDTO> getApplicableCoupons(@RequestBody CartInputDTO cartInputDTO) {
        ApplicableCouponsDTO applicable = couponService.findApplicableCoupons(cartInputDTO);
        return new ResponseEntity<>(applicable, HttpStatus.OK);
    }

    @PostMapping("/apply-coupon/{id}")
    public ResponseEntity<CartOutputDTO> applyCoupon(@PathVariable Integer id, @RequestBody CartInputDTO cartInputDTO) {
        CartOutputDTO updatedCart = couponService.applyCoupon(cartInputDTO, id);
        return new ResponseEntity<>(updatedCart, HttpStatus.OK);
    }
}
