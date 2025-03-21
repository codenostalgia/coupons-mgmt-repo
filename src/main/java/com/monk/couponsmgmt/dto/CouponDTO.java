package com.monk.couponsmgmt.dto;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.monk.couponsmgmt.pojos.BxGyDetails;
import com.monk.couponsmgmt.pojos.CartWiseDetails;
import com.monk.couponsmgmt.pojos.Details;
import com.monk.couponsmgmt.pojos.ProductWiseDetails;
import lombok.Data;

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
}
