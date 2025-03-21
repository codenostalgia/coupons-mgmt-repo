package com.monk.couponsmgmt.dto;

import com.fasterxml.jackson.annotation.*;
import lombok.Data;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.monk.couponsmgmt.consts.UniversalConstants.*;

@Data
public class CouponDTO {
    Integer id;
    String type;

    @JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXTERNAL_PROPERTY, property = "type")
    @JsonSubTypes({
            @JsonSubTypes.Type(value = CartWiseDetails.class, name = TYPE_CARTWISE),
            @JsonSubTypes.Type(value = ProductWiseDetails.class, name = TYPE_PRODUCTWISE),
            @JsonSubTypes.Type(value = BxGyDetails.class, name = TYPE_BXGY)
    })
    Details details;

    @Data
    public static class Details {
    }

    @Data
    public static class CartWiseDetails extends Details {
        private Integer threshold;
        private Integer discount;
    }

    @Data
    public static class ProductWiseDetails extends Details {
        @JsonProperty("product_id")
        private Integer productId;
        private Integer discount;
    }

    @Data
    public static class BxGyDetails extends Details {
        @JsonProperty("buy_products")
        private List<BuyProduct> buyProducts;
        @JsonProperty("get_products")
        private List<GetProduct> getProducts;
        @JsonProperty("repition_limit")
        private Integer repetitionLimit;

        @JsonIgnore
        Map<Integer, Integer> buyProd2Quant;
        @JsonIgnore
        Map<Integer, Integer> getProd2Quant;

        public BxGyDetails() {

        }

        @JsonCreator
        public BxGyDetails(@JsonProperty("buy_products") List<BuyProduct> buyProducts,
                           @JsonProperty("get_products") List<GetProduct> getProducts,
                           @JsonProperty("repition_limit") Integer repetitionLimit) {
            System.out.println("parameterized constructor called...");
            this.buyProducts = buyProducts;
            this.getProducts = getProducts;
            this.repetitionLimit = repetitionLimit;

            buyProd2Quant = new HashMap<>();
            getProd2Quant = new HashMap<>();

            for (BuyProduct buyProduct : buyProducts) {
                if (buyProduct.getQuantity() >= 0) {
                    buyProd2Quant.put(buyProduct.getProductId(), buyProduct.getQuantity());
                }
            }
            for (GetProduct getProduct : getProducts) {
                if (getProduct.getQuantity() >= 0) {
                    getProd2Quant.put(getProduct.getProductId(), getProduct.getQuantity());
                }
            }
            System.out.println("Object: " + this);
        }

        @Data
        public static class BuyProduct {
            @JsonProperty("product_id")
            private Integer productId;
            private Integer quantity;
        }

        @Data
        public static class GetProduct {
            @JsonProperty("product_id")
            private Integer productId;
            private Integer quantity;
        }
    }

}
