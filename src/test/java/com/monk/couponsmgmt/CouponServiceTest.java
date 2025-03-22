package com.monk.couponsmgmt;

import com.monk.couponsmgmt.dto.ApplicableCouponsDTO;
import com.monk.couponsmgmt.dto.CartInputDTO;
import com.monk.couponsmgmt.dto.CartOutputDTO;
import com.monk.couponsmgmt.dto.CouponDTO;
import com.monk.couponsmgmt.pojos.*;
import com.monk.couponsmgmt.services.CouponServiceImpl;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestPropertySource(properties = {
        "spring.datasource.url=jdbc:h2:mem:testdb",
        "spring.datasource.driverClassName=org.h2.Driver",
        "spring.datasource.username=sa",
        "spring.datasource.password=password"
})
public class CouponServiceTest {

    @Autowired
    private CouponServiceImpl couponService;

    @BeforeAll
    public void setUp() throws SQLException {
        couponService.init();
        System.out.println(couponService);
    }

    public CouponDTO getCoupon(String type) {

        CouponDTO coupon = new CouponDTO();
        coupon.setType(type);
        coupon.setCreatedTS(System.currentTimeMillis());
        coupon.setExpiresInDays(7);

        switch (type) {
            case "cart-wise": {
                CartWiseDetails details = new CartWiseDetails();
                details.setDiscount(10);
                details.setThreshold(100);
                details.setMaximumDiscount(25);
                coupon.setDetails(details);
                break;
            }
            case "product-wise": {
                ProductWiseDetails details = new ProductWiseDetails();
                details.setDiscount(10);
                details.setProductId(1);
                coupon.setDetails(details);
                break;
            }
            case "bxgy": {
                BuyProduct bp1 = new BuyProduct();
                bp1.setProductId(1);
                bp1.setQuantity(2);
                BuyProduct bp2 = new BuyProduct();
                bp2.setProductId(2);
                bp2.setQuantity(2);

                List<BuyProduct> buyProds = new ArrayList<>();
                buyProds.add(bp1);
                buyProds.add(bp2);

                GetProduct gp1 = new GetProduct();
                gp1.setProductId(3);
                gp1.setQuantity(1);

                List<GetProduct> getProds = new ArrayList<>();
                getProds.add(gp1);

                BxGyDetails details = new BxGyDetails();
                details.setBuyProducts(buyProds);
                details.setGetProducts(getProds);
                details.setRepetitionLimit(2);
                coupon.setDetails(details);
                break;
            }
        }
        return coupon;
    }

    public CartInputDTO getCart() {

        CartInputDTO cartInputDTO = new CartInputDTO();

        Item item1 = new Item();
        item1.setProductId(1);
        item1.setQuantity(6);
        item1.setPrice(50);

        Item item2 = new Item();
        item2.setProductId(2);
        item2.setQuantity(3);
        item2.setPrice(30);

        Item item3 = new Item();
        item3.setProductId(3);
        item3.setQuantity(2);
        item3.setPrice(25);

        List<Item> items = new ArrayList<>();
        items.add(item1);
        items.add(item2);
        items.add(item3);

        Cart cart = new Cart(items);
        cartInputDTO.setCart(cart);

        return cartInputDTO;
    }

    @Test
    @Order(1)
    public void testCreateCoupon() {
        CouponDTO pCoupon = getCoupon("product-wise");
        CouponDTO result1 = couponService.createCoupon(pCoupon);

        assertNotNull(result1);
        assertEquals(1, result1.getId());

        CouponDTO cCoupon = getCoupon("cart-wise");
        CouponDTO result2 = couponService.createCoupon(cCoupon);

        assertNotNull(result2);
        assertEquals(2, result2.getId());

        CouponDTO bgCoupon = getCoupon("bxgy");
        CouponDTO result3 = couponService.createCoupon(bgCoupon);

        assertNotNull(result3);
        assertEquals(3, result3.getId());
    }

    @Test
    @Order(2)
    public void testGetCoupon() {
        CouponDTO result1 = couponService.getCoupon(1);

        assertNotNull(result1);
        assertEquals(1, result1.getId());
        assertEquals("product-wise", result1.getType());

        CouponDTO result2 = couponService.getCoupon(2);

        assertNotNull(result2);
        assertEquals(2, result2.getId());
        assertEquals("cart-wise", result2.getType());

        CouponDTO result3 = couponService.getCoupon(3);

        assertNotNull(result3);
        assertEquals(3, result3.getId());
        assertEquals("bxgy", result3.getType());
    }

    @Test
    @Order(3)
    public void testGetAllCoupons() {
        List<CouponDTO> result = couponService.getAllCoupons();

        assertNotNull(result);
        assertEquals(3, result.size());
    }

    @Test
    @Order(4)
    public void testDeleteCoupon() {
        Boolean deleted = couponService.deleteCoupon(1);

        assertNotNull(deleted);
        assertEquals(true, deleted);

        List<CouponDTO> result = couponService.getAllCoupons();
        assertNotNull(result);
        assertEquals(2, result.size());
    }

    @Test
    @Order(5)
    public void testUpdateCoupon() {
        CouponDTO coupon = getCoupon("cart-wise");
        CartWiseDetails details = (CartWiseDetails) coupon.getDetails();
        details.setMaximumDiscount(100);
        coupon.setDetails(details);

        CouponDTO dbCoupon = couponService.getCoupon(2);
        assertNotNull(dbCoupon);
        assertEquals(25, ((CartWiseDetails) dbCoupon.getDetails()).getMaximumDiscount());

        CouponDTO updatedCoupon = couponService.updateCoupon(2, coupon);
        assertNotNull(updatedCoupon);
        assertEquals(100, ((CartWiseDetails) updatedCoupon.getDetails()).getMaximumDiscount());
    }

    @Test
    @Order(6)
    public void testFindApplicableCoupons() {
        CartInputDTO cart = getCart();
        ApplicableCouponsDTO coupons = couponService.findApplicableCoupons(cart);

        System.out.println(coupons);

        assertNotNull(coupons);
        assertEquals(2, coupons.getCoupons().size());
        assertEquals(2, coupons.getCoupons().get(0).getId());
        assertEquals(3, coupons.getCoupons().get(1).getId());
    }

    @Test
    @Order(6)
    public void testApplyCoupon() {
        CartInputDTO cart = getCart();
        CartOutputDTO cartUpdated = couponService.applyCoupon(cart, 2);
        System.out.println(cartUpdated);

        assertNotNull(cartUpdated);
        assertEquals(396, cartUpdated.getFinalPrice());
    }
}
