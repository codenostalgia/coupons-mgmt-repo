package com.monk.couponsmgmt.pojos;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
public class Cart {
    List<Item> items;
    @JsonIgnore
    Map<Integer, Integer> prod2Price;
    @JsonIgnore
    Map<Integer, Integer> prod2Quant;

    public Cart(List<Item> items) {
        this.items = items;
        prod2Price = new HashMap<>();
        prod2Quant = new HashMap<>();
        for (Item item : items) {
            if (item.getPrice() > 0 && item.getQuantity() > 0 && item.getPrice() > 0) {
                prod2Price.put(item.getProductId(), item.getPrice());
                prod2Quant.put(item.getProductId(), item.getQuantity());
            }
        }
    }

    @JsonIgnore
    public Map<Integer, Integer> getProduct2DiscountMap() {
        if (items != null && items.size() > 0) {
            Map<Integer, Integer> prod2Discount = new HashMap<>();

            for (Item item : items) {
                if (item.getTotalDiscount() > 0) {
                    prod2Discount.put(item.getProductId(), item.getTotalDiscount());
                }
            }
            return prod2Discount;
        }
        return new HashMap<>();
    }

    public Integer totalCartValue() {
        Integer total = 0;
        if (items != null && items.size() > 0) {
            total = items.stream().filter(item -> ((item.getPrice() * item.getQuantity()) > 0))
                    .mapToInt(item -> item.getPrice() * item.getQuantity())
                    .sum();
        }
        return total;
    }

}
