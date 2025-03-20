package com.monk.couponsmgmt.dto;

import lombok.Data;

import java.util.List;

@Data
public class BxgyCouponDTO extends CouponDTO {

    private List<BuyProductDTO> buyProducts;
    private List<GetProductDTO> getProducts;
    private int repetitionLimit;

    @Data
    public static class BuyProductDTO {
        private Long productId;
        private int quantity;
    }

    @Data
    public static class GetProductDTO {
        private Long productId;
        private int quantity;
    }
}
