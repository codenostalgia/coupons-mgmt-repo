package com.monk.couponsmgmt.repos;


import com.monk.couponsmgmt.entities.Coupon;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CouponRepository extends JpaRepository<Coupon, Long> {
}