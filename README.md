# This repository contains code for the assignment `Coupons Management API` given by Monk Commerce Company, as a part of their hiring process


### Tech Stack Used:
- SpringBoot (Java)
- H2- In Memory Database
- JUnit
- Maven
- Postman

### How To Deploy Backend:
1) Open Command Prompt and navigate to project root
2) Run `mvn clean install`
3) Run  `java -jar .\target\couponsmgmt-0.0.1-SNAPSHOT.jar`
4) This will deploy backend on `http://localhost:8080`

### Accessing Interactive Web Console for H2 Database-
1) Navigate to `http://localhost:8080/h2-console`
2) Enter JDBC URL: `jdbc:h2:mem:coupondb`
3) Enter User Name: `sa`
4) Enter Password: `password`
5) Press `connect`

### POSTMAN Collection for the APIs implemented -
- Download and import API Collection present in project, to your postman
- `https://github.com/codenostalgia/coupons-mgmt-repo/blob/main/CouponMgmt.postman_collection.json`



## Implemented Cases:


**`APIs`**
  - Implemented all the 7 APIs, along with the necessary functionality mentioned in the assignment

  **`API to Retrieve Best Coupons`**
  - Implemented API `POST /best-coupons` apart from 7 APIs mentioned in the assignment
  - This API filters out and provides the Coupons which has `maximum discount` among all the Applicable coupons for the given cart
  - The Payload and Response structure of this API is exactly same as `POST /applicable-coupons` API
  - You can find the API in the Postman Collection present in project root


**`API for Bulk Coupon Creation:`**
- Implemented API `POST /coupons/bulk` apart from 7 APIs mentioned in the assignment
- The API takes Coupons List payload and response with sames coupons with extra fields populated like id, createdTS, expiryTS, etc


**`PayLoad Validation`**
- The API Payload is validated against the DTOs defined in Backend
- Invalid payload return `PayLoad Structure is Invalid !!` message
- Similary ID in path should be integer, or backend returns `ID must be an Integer !!` message


**`Coupon Types`**
- Implemented 3 types of coupons as mentioned in assignment - `CART WISE` `PRODUCT WISE` `BXGY`
    

**`Type Extensibility:`**
- The `details` of coupon are stored as a `JSON string` in the H2-database. This will help us accomodate any new type of coupon in future without modifying the database schema and DTOs structure.
- The CouponDTO uses a `Details` class, which could implemented by Details of new coupon type in future. This will ensure existing schema and code wont break after adding any new cuopon type


**`Coupon Expiry:`**
- The create coupons api `/coupons` takes an optional payload propery `expires_in_days`. Using this property, backend will compute its expiry timestamp `expiryTS` with reference to the coupon creation timestamp `createdTS`.
```
{
    "type": "cart-wise",
    "expires_in_days": 7, // By Default Expiry Days are 30
    "details": {
        "threshold": 100,
        "discount": 10,
        "maximum_discount": 25
    }
}
```
- If the optional property is not provided, by default backend will set coupon expiry to `30 days`.
- The expiryTS is implemented as an Epoch, because epoch would remain same even in different Timezones
- The coupon will also contain a boolean property `is_expired`, which is dynamically computed everytime by comparing the `expiryTS` with the current timestamp of the system.
- The `is_expired` field is used internally in backend `POST /apply-coupon/{id}` API to check if coupon is still valid, otherwise it will throw `COUPON EXPIRED` exception
- The coupon will also contain property `expiry_date`, which represents `expiryTS` in a readable date format, considering IST Timezone.
- The coupon has a structure like this
```
{
    "id": 1,
    "type": "cart-wise",
    "createdTS": 1742657622180,
    "expiryTS": 1743262422180,
    "is_expired": false,
    "expiry_date": "2025-03-29 21:03:42",
    "details": {
        "threshold": 100,
        "discount": 10,
        "maximum_discount": 25
    }
}
```

**`BxGy Coupon Edge Cases:`**
- The system succesfully handles cases where the "Buy X, Get Y" coupon is applied to multiple products with different quantities


**`CARTWISE Coupon: X% Off upto RsY above an order of RsZ :`**
- The CARTWISE coupon takes an optional proporty `max_discount` in its details, which defines the upper boundary for the discount.
- For example, let say the CARTWISE coupon has `threshold=2000` and `discount=10%` and `max_discount=500`. Then if the coupon is applied on a cart of `total_price=10000`, then it will give `total_discount=500` instead of 1000, as 1000>500. So this will act as `10% Off upto Rs500 above an order of Rs2000`


**`Repetition Limit:`**
- The BxGy coupon details has `repetition_limit` property, which would define how many times this coupon can be applied on the same cart until exhaustion


**`Exception Handling:`**
Implemented Robust Exception Handling to deal with any kind of custom and system generated Exception:

- `NoResourceFoundException:` If invalid URL path entered
- `MethodArgumentTypeMismatchException:` If provided ID is not an Integer in any api request path
- `CouponNotFoundException:` If provided coupon ID doesnt exist
- `CouponExpiredException:` If `POST /apply-coupon/{id}` API called with an expired coupon ID
- `HttpMessageNotReadableException:` If the provided payload structure is invalid
- `NoApplicableCouponsFoundException:` If there are NO Applicable Coupons found for the given cart

**`Testing:`**
- Implemented Unit Tests using JUnit for all the functions present in service layer of the application, which effectively ensure entire application is running as desired


## Unimplemented Cases:


**`User-Specific Coupons:`**
- Coupons may be restricted to specific users or user groups (e.g., first-time users, premium users).
- currently the system does not validate if the user is eligible for the coupon, as we dont have any user data currently

**`Usage Limits:`**
- Coupons may have a maximum usage limit (e.g., can be used only 100 times globally or 1 time per user).
- The system does not track how many times a coupon has been used.

**`Product-Wise Coupon Constraints:`**
- The system does not validate if the product in the cart meets the coupon's conditions like minimum product quantity (which we usually see on Flipkart/Amazon)
- Currently it simply applies X% discount to Product Y


**`Region-Specific Coupons:`**
- Coupons may be restricted to specific regions or countries.
- The system does not validate the user's region before applying the coupon.


**`Dynamic Discounts:`**
- Coupons may have dynamic discounts based on cart value (e.g., 10% off for orders above 100, 20% Off for orders above 200).
- The system does not handle dynamic discount calculations.

**`Coupon Invalidation:`**
- The system does not handle cases where a coupon is invalidated after being applied (e.g., due to a change in coupon conditions).


## Improvements Which Can Be Done in Implemented Cases:

**`expiry_date as User TimeZone:`**
- The DateTime display string  `expiry_date` provided in coupon response, should be as per the TimeZone of the user
- Currently its hardcoded to return in `Asia/kolkata` Timezone

**`BxGy Coupon Implementation Improvement:`**
- The BxGy should be implemented in a way that, the total_discount using that coupon should be maximized
- Currently it simply picks the `first n` matches of buy and get products, and doesn't have for logic for choosing `best n` matches among all possible matches


## Assumptions/Limitations:


**`Coupon Types:`**
- Only three types of coupons are supported: cart-wise, product-wise, and BxGy.
- New coupon types can be added in the future without breaking existing functionality.


**`Cart Structure:`**
- The cart is assumed to be a simple list of products with quantities and prices.
- No support for complex cart structures (e.g., bundles, subscriptions).


**`Coupon Conditions:`**
- Coupon conditions are assumed to be static and predefined in the details itself as provided in assignment

**`Time Zones:`**
- Expiry Date shown in response is hardcoded in IST


**`Database:`**
- The database is assumed to be always available and consistent.
- No handling for database failures 
