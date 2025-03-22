package com.monk.couponsmgmt.dto;

import com.fasterxml.jackson.annotation.*;
import com.monk.couponsmgmt.pojos.BxGyDetails;
import com.monk.couponsmgmt.pojos.CartWiseDetails;
import com.monk.couponsmgmt.pojos.Details;
import com.monk.couponsmgmt.pojos.ProductWiseDetails;
import lombok.Data;

import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

import static com.monk.couponsmgmt.consts.UniversalConstants.*;

@Data
@JsonPropertyOrder({"id", "type", "createdTS", "expiryTS", "is_expired", "expiry_date", "details"})
public class CouponDTO {
    Integer id;
    String type;
    Integer expiresInDays = 30; // default 30 days expiry
    Long createdTS = System.currentTimeMillis();

    @JsonProperty("expiryTS")
    public Long getExpiryTS() {
        return createdTS + Duration.of(expiresInDays, ChronoUnit.DAYS).toMillis();
    }

    @JsonProperty("is_expired")
    public Boolean getIsExpired() {
        long currentTS = System.currentTimeMillis();
        return currentTS >= getExpiryTS();
    }

    @JsonProperty("expiry_date")
    public String getExpiryDateTime() {
        Instant instant = Instant.ofEpochMilli(getExpiryTS());
        ZonedDateTime istTime = instant.atZone(ZoneId.of("Asia/Kolkata"));
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String readableDateTime = istTime.format(formatter);
        return readableDateTime;
    }

    @JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXTERNAL_PROPERTY, property = FIELD_TYPE)
    @JsonSubTypes({
            @JsonSubTypes.Type(value = CartWiseDetails.class, name = TYPE_CARTWISE),
            @JsonSubTypes.Type(value = ProductWiseDetails.class, name = TYPE_PRODUCTWISE),
            @JsonSubTypes.Type(value = BxGyDetails.class, name = TYPE_BXGY)
    })
    Details details;

    @JsonIgnore
    @JsonProperty("expires_in_days")
    public Integer getExpiresInDays() {
        return expiresInDays;
    }

    @JsonProperty("expires_in_days")
    public void setExpiresInDays(Integer expiresInDays) {
        this.expiresInDays = expiresInDays;
    }
}
