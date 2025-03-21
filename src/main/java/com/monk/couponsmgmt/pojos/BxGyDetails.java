package com.monk.couponsmgmt.pojos;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
public class BxGyDetails extends Details {
    @JsonProperty("buy_products")
    private List<BuyProduct> buyProducts;
    @JsonProperty("get_products")
    private List<GetProduct> getProducts;
    @JsonProperty("repetition_limit")
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
}