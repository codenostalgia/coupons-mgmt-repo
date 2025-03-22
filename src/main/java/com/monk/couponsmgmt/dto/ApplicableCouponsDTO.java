package com.monk.couponsmgmt.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.monk.couponsmgmt.exceptions.GlobalExceptionHandler;
import com.monk.couponsmgmt.pojos.BxGyDetails;
import com.monk.couponsmgmt.pojos.Cart;
import com.monk.couponsmgmt.pojos.CartWiseDetails;
import com.monk.couponsmgmt.pojos.ProductWiseDetails;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.monk.couponsmgmt.consts.UniversalConstants.*;

@Data
public class ApplicableCouponsDTO {

    @JsonProperty("applicable_coupons")
    List<CouponSimplifiedDTO> coupons;

    public ApplicableCouponsDTO(CartInputDTO cartInputDTO, List<CouponDTO> coupons) {
        this.coupons = new ArrayList<>();

        Map<String, List<CouponDTO>> typeToCouponsMap = coupons.stream()
                .collect(Collectors.groupingBy(CouponDTO::getType));

        if (coupons != null) {

            Cart cart = cartInputDTO.getCart();
            Map<Integer, Integer> prod2Price = cart.getProd2Price();
            Map<Integer, Integer> prod2Quant = cart.getProd2Quant();

            for (Map.Entry<String, List<CouponDTO>> entry : typeToCouponsMap.entrySet()) {
                String type = entry.getKey();
                List<CouponDTO> candidateCoupons = entry.getValue();
                if (type.equalsIgnoreCase(TYPE_CARTWISE)) {
                    for (CouponDTO coupon : candidateCoupons) {
                        CartWiseDetails details = (CartWiseDetails) coupon.getDetails();
                        if (cart.totalCartValue() > details.getThreshold()) {
                            int discount = (int) ((cart.totalCartValue() * details.getDiscount()) / 100.);
                            if (discount > 0) {
                                CouponSimplifiedDTO csDTO = new CouponSimplifiedDTO(coupon.getId(), entry.getKey(), discount);
                                this.coupons.add(csDTO);
                            }
                        }
                    }
                }
                if (type.equalsIgnoreCase(TYPE_PRODUCTWISE)) {
                    for (CouponDTO coupon : candidateCoupons) {
                        ProductWiseDetails details = (ProductWiseDetails) coupon.getDetails();
                        if (prod2Price.containsKey(details.getProductId())) {
                            int discount = (int) ((prod2Price.get(details.getProductId()) * details.getDiscount()) / 100.);
                            if (discount > 0) {
                                CouponSimplifiedDTO csDTO = new CouponSimplifiedDTO(coupon.getId(), type, discount);
                                this.coupons.add(csDTO);
                            }
                        }
                    }
                }
                if (type.equalsIgnoreCase(TYPE_BXGY)) {
                    // ASSUMPTION : In one Coupon, Same product cant be present in both buy and get category
                    for (CouponDTO coupon : candidateCoupons) {
                        BxGyDetails details = (BxGyDetails) coupon.getDetails();
                        Map<Integer, Integer> buyProds = details.getBuyProd2Quant();
                        Map<Integer, Integer> getProds = details.getGetProd2Quant();
                        System.out.println("buyMap: " + buyProds);
                        System.out.println("getMap: " + getProds);

                        List<Integer> prods = new ArrayList<>(prod2Quant.keySet());
                        int repititionLimit = details.getRepetitionLimit();
                        int totalDiscount = 0;
                        while (repititionLimit > 0) {
                            for (Integer paidProd : prods) {
                                if (buyProds.containsKey(paidProd) && prod2Quant.get(paidProd) >= buyProds.get(paidProd)) {

                                    Boolean bFound = false;
                                    List<Integer> candidateFreeProds = prods.stream().filter(p -> p != paidProd).toList();
                                    // ASSUMPTION: Just picking discount by first match
                                    // IMPROVEMENT: We can pick best discount rather than first
                                    for (Integer freProd : candidateFreeProds) {
                                        if (getProds.containsKey(freProd)) {
                                            Integer discount = Math.min(getProds.get(freProd), prod2Quant.get(freProd))
                                                    * prod2Price.get(freProd);
                                            if (discount > 0) {
                                                bFound = true;
                                                totalDiscount += discount;

                                                // Updating Cart
                                                Integer prodCartQuantFree = Math.min(getProds.get(freProd), prod2Quant.get(freProd));
                                                prod2Quant.put(paidProd, prod2Quant.get(paidProd) - buyProds.get(paidProd));
                                                prod2Quant.put(freProd, prod2Quant.get(freProd) - prodCartQuantFree);
                                                break;
                                            }
                                        }
                                    }
                                    if (bFound) break;
                                }
                            }
                            repititionLimit--;
                        }
                        if (totalDiscount > 0) {
                            CouponSimplifiedDTO csDTO = new CouponSimplifiedDTO(coupon.getId(), type, totalDiscount);
                            this.coupons.add(csDTO);
                        }
                    }
                }
            }
        }

        if (this.coupons.size() == 0) {
            throw new GlobalExceptionHandler.NoApplicableCouponsFoundException();
        }
    }
}
