package com.monk.couponsmgmt.entities;

import lombok.Data;

import jakarta.persistence.*;

import java.util.List;

@Data
@Entity
@DiscriminatorValue("bxgy")
public class BxgyCoupon extends Coupon {

    @ElementCollection
    @CollectionTable(name = "buy_product", joinColumns = @JoinColumn(name = "coupon_id"))
    private List<BuyProduct> buyProducts;

    @ElementCollection
    @CollectionTable(name = "get_product", joinColumns = @JoinColumn(name = "coupon_id"))
    private List<GetProduct> getProducts;

    private int repetitionLimit;

    @Data
    @Embeddable
    public static class BuyProduct {
        private Long productId;
        private int quantity;
    }

    @Data
    @Embeddable
    public static class GetProduct {
        private Long productId;
        private int quantity;
    }
}
