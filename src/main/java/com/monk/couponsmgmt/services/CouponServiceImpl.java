package com.monk.couponsmgmt.services;

import com.monk.couponsmgmt.db.CouponsDAO;
import com.monk.couponsmgmt.db.H2DatabaseConnection;
import com.monk.couponsmgmt.dto.ApplicableCouponsDTO;
import com.monk.couponsmgmt.dto.CartInputDTO;
import com.monk.couponsmgmt.dto.CartOutputDTO;
import com.monk.couponsmgmt.dto.CouponDTO;
import com.monk.couponsmgmt.exceptions.GlobalExceptionHandler.CouponExpiredException;
import com.monk.couponsmgmt.exceptions.GlobalExceptionHandler.InvalidCouponTypeException;
import com.monk.couponsmgmt.exceptions.GlobalExceptionHandler.NoCouponsFoundException;
import com.monk.couponsmgmt.pojos.*;
import com.monk.couponsmgmt.services.inf.CouponService;
import org.springframework.stereotype.Service;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.monk.couponsmgmt.consts.UniversalConstants.*;

@Service
public class CouponServiceImpl implements CouponService {

    CouponsDAO couponsDAO;
    Connection connection;

    public CouponServiceImpl() throws SQLException {
        this.couponsDAO = new CouponsDAO();
        this.connection = H2DatabaseConnection.getConnection();
        couponsDAO.init(connection);
    }

    @Override
    public CouponDTO createCoupon(CouponDTO coupon) {
        return couponsDAO.insertCoupon(coupon, connection);
    }

    @Override
    public CouponDTO getCoupon(Integer id) {
        return couponsDAO.getCoupon(id, connection);
    }

    @Override
    public List<CouponDTO> getAllCoupons() {
        return couponsDAO.getAllCoupons(connection);
    }

    @Override
    public boolean deleteCoupon(Integer id) {
        return couponsDAO.deleteCoupon(id, connection);
    }

    @Override
    public CouponDTO updateCoupon(Integer id, CouponDTO coupon) {
        return couponsDAO.updateCoupon(id, coupon, connection);
    }

    @Override
    public ApplicableCouponsDTO findApplicableCoupons(CartInputDTO cartInputDTO) {
        List<CouponDTO> coupons = new ArrayList<>();
        try {
            coupons = couponsDAO.getAllCoupons(connection);
        } catch (NoCouponsFoundException e) {
        }
        // Filtering Out Expired Coupons
        coupons = coupons.stream().filter(coupon -> !coupon.getIsExpired()).toList();
        ApplicableCouponsDTO applicableCoupons = new ApplicableCouponsDTO(cartInputDTO, coupons);
        return applicableCoupons;
    }

    @Override
    public CartOutputDTO applyCoupon(CartInputDTO cartInputDTO, Integer id) {

        CouponDTO coupon = couponsDAO.getCoupon(id, connection);

        if (coupon.getIsExpired()) {
            throw new CouponExpiredException();
        }

        String type = coupon.getType();
        Details details = coupon.getDetails();
        switch (type) {
            case TYPE_CARTWISE:
                return applyCartWiseCoupon(details, cartInputDTO);
            case TYPE_PRODUCTWISE:
                return applyProdctWiseCoupon(details, cartInputDTO);
            case TYPE_BXGY:
                return applyBxGyCoupon(details, cartInputDTO);
            default:
                throw new InvalidCouponTypeException("Invalid Coupon Type!");
        }
    }

    private CartOutputDTO applyBxGyCoupon(Details rawDetails, CartInputDTO cartInputDTO) {
        CartOutputDTO updatedCart = getUpdatedCart(cartInputDTO);
        Integer totalPrice = updatedCart.getTotalPrice();

        BxGyDetails details = (BxGyDetails) rawDetails;

        Map<Integer, Integer> buyProds = details.getBuyProd2Quant();
        Map<Integer, Integer> getProds = details.getGetProd2Quant();

        Cart cart = updatedCart.getCart();
        Map<Integer, Integer> prod2Price = cart.getProd2Price();
        Map<Integer, Integer> prod2Quant = cart.getProd2Quant();

        List<Item> items = updatedCart.getCart().getItems();
        int repititionLimit = details.getRepetitionLimit();
        int totalDiscount = 0;

        while (repititionLimit > 0) {
            for (Item paidProd : items) {
                if (buyProds.containsKey(paidProd.getProductId()) && prod2Quant.get(paidProd.getProductId()) >= buyProds.get(paidProd.getProductId())) {

                    Boolean bFound = false;
                    List<Item> candidateFreeProds = items.stream().filter(p -> p.getProductId() != paidProd.getProductId()).toList();
                    // ASSUMPTION: Just picking discount by first match
                    // IMPROVEMENT: We can pick best discount rather than first
                    for (Item freeProd : candidateFreeProds) {
                        if (getProds.containsKey(freeProd.getProductId())) {
                            Integer discount = Math.min(getProds.get(freeProd.getProductId()), prod2Quant.get(freeProd.getProductId()))
                                    * prod2Price.get(freeProd.getProductId());
                            if (discount > 0) {

                                bFound = true;
                                freeProd.setTotalDiscount(freeProd.getTotalDiscount() + discount);
                                totalDiscount += discount;

                                // Updating Cart
                                Integer prodCartQuantFree = Math.min(getProds.get(freeProd.getProductId()), prod2Quant.get(freeProd.getProductId()));
                                prod2Quant.put(paidProd.getProductId(), prod2Quant.get(paidProd.getProductId()) - buyProds.get(paidProd.getProductId()));
                                prod2Quant.put(freeProd.getProductId(), prod2Quant.get(freeProd.getProductId()) - prodCartQuantFree);
                                break;
                            }
                        }
                    }
                    if (bFound) break;
                }
            }
            repititionLimit--;
        }
        updatedCart.setTotalDiscount(totalDiscount);
        updatedCart.setFinalPrice(totalPrice - totalDiscount);

        return updatedCart;

    }

    private CartOutputDTO applyProdctWiseCoupon(Details rawDetails, CartInputDTO cartInputDTO) {

        CartOutputDTO updatedCart = getUpdatedCart(cartInputDTO);
        Integer totalPrice = updatedCart.getTotalPrice();

        ProductWiseDetails details = (ProductWiseDetails) rawDetails;

        Integer totalDiscount = 0;
        for (Item item : updatedCart.getCart().getItems()) {
            if (item.getProductId() == details.getProductId() && item.getQuantity() > 0) {
                Integer discount = (int) ((details.getDiscount() * item.getPrice()) / 100.);
                item.setTotalDiscount(discount);
                totalDiscount += discount;
            }
        }

        updatedCart.setTotalDiscount(totalDiscount);
        updatedCart.setFinalPrice(totalPrice - totalDiscount);

        return updatedCart;
    }

    private CartOutputDTO applyCartWiseCoupon(Details rawDetails, CartInputDTO cartInputDTO) {

        CartOutputDTO updatedCart = getUpdatedCart(cartInputDTO);
        Integer totalPrice = updatedCart.getTotalPrice();

        CartWiseDetails details = (CartWiseDetails) rawDetails;

        Integer totalDiscount = 0;
        if (totalPrice > details.getThreshold()) {
            totalDiscount = (int) Math.floor((details.getDiscount() * totalPrice) / 100.);

            // Give Discount Only Upto Max X Amount ---> 20% Off UPTO Rs. 100
            totalDiscount = Math.min(totalDiscount, details.getMaximumDiscount());
            updatedCart.setTotalDiscount(totalDiscount);
        } else {
            updatedCart.setTotalDiscount(totalDiscount);
        }
        updatedCart.setFinalPrice(totalPrice - totalDiscount);

        return updatedCart;
    }

    private CartOutputDTO getUpdatedCart(CartInputDTO cartInputDTO) {

        CartOutputDTO updatedCart = new CartOutputDTO();
        updatedCart.setCart(cartInputDTO.getCart());

        Integer totalPrice = cartInputDTO.getCart().totalCartValue();
        updatedCart.setTotalPrice(totalPrice);

        return updatedCart;
    }
}

