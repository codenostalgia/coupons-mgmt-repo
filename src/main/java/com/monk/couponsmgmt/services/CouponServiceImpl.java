package com.monk.couponsmgmt.services;

import com.monk.couponsmgmt.db.CouponsDAO;
import com.monk.couponsmgmt.db.H2DatabaseConnection;
import com.monk.couponsmgmt.dto.ApplicableCouponsDTO;
import com.monk.couponsmgmt.dto.CartInputDTO;
import com.monk.couponsmgmt.dto.CartOutputDTO;
import com.monk.couponsmgmt.dto.CouponDTO;
import com.monk.couponsmgmt.pojos.Cart;
import com.monk.couponsmgmt.pojos.Item;
import com.monk.couponsmgmt.services.inf.CouponService;
import org.springframework.stereotype.Service;

import java.sql.Connection;
import java.sql.SQLException;
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
        try {
            Integer id = couponsDAO.insertCoupon(coupon, connection);
            if (id == -1) return null;
            coupon.setId(id);
            return coupon;
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return null;
    }

    @Override
    public CouponDTO getCoupon(Integer id) {
        try {
            CouponDTO coupon = couponsDAO.getCoupon(id, connection);
            return coupon;
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return null;
    }

    @Override
    public List<CouponDTO> getAllCoupons() {
        try {
            List<CouponDTO> coupons = couponsDAO.getAllCoupons(connection);
            return coupons;
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return null;
    }

    @Override
    public boolean deleteCoupon(Integer id) {
        try {
            return couponsDAO.deleteCoupon(id, connection);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return false;
    }

    @Override
    public CouponDTO updateCoupon(Integer id, CouponDTO coupon) {
        try {
            return couponsDAO.updateCoupon(id, coupon, connection);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return null;
    }

    @Override
    public ApplicableCouponsDTO findApplicableCoupons(CartInputDTO cartInputDTO) {
        try {
            List<CouponDTO> coupons = couponsDAO.getAllCoupons(connection);
            ApplicableCouponsDTO applicableCoupons = new ApplicableCouponsDTO(cartInputDTO, coupons);
            return applicableCoupons;
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return null;
    }

    @Override
    public CartOutputDTO applyCoupon(CartInputDTO cartInputDTO, Integer id) {
        try {
            CouponDTO coupon = couponsDAO.getCoupon(id, connection);
            if (coupon == null) {
                return null;
            }
            String type = coupon.getType();
            CouponDTO.Details details = coupon.getDetails();
            switch (type) {
                case TYPE_CARTWISE:
                    return applyCartWiseCoupon(details, cartInputDTO);
                case TYPE_PRODUCTWISE:
                    return applyProdctWiseCoupon(details, cartInputDTO);
                case TYPE_BXGY:
                    return applyBxGyCoupon(details, cartInputDTO);
                default:
                    return null;
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return null;
    }

    private CartOutputDTO applyBxGyCoupon(CouponDTO.Details rawDetails, CartInputDTO cartInputDTO) {
        CartOutputDTO updatedCart = getUpdatedCart(cartInputDTO);
        Integer totalPrice = updatedCart.getTotalPrice();

        CouponDTO.BxGyDetails details = (CouponDTO.BxGyDetails) rawDetails;

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

    private CartOutputDTO applyProdctWiseCoupon(CouponDTO.Details rawDetails, CartInputDTO cartInputDTO) {

        CartOutputDTO updatedCart = getUpdatedCart(cartInputDTO);
        Integer totalPrice = updatedCart.getTotalPrice();

        CouponDTO.ProductWiseDetails details = (CouponDTO.ProductWiseDetails) rawDetails;

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

    private CartOutputDTO applyCartWiseCoupon(CouponDTO.Details rawDetails, CartInputDTO cartInputDTO) {

        CartOutputDTO updatedCart = getUpdatedCart(cartInputDTO);
        Integer totalPrice = updatedCart.getTotalPrice();

        CouponDTO.CartWiseDetails details = (CouponDTO.CartWiseDetails) rawDetails;

        Integer totalDiscount = 0;
        if (totalPrice > details.getThreshold()) {
            totalDiscount = (int) Math.floor((details.getDiscount() * totalPrice) / 100.);
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

