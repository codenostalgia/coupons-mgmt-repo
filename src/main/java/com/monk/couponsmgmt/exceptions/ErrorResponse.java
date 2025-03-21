package com.monk.couponsmgmt.exceptions;

import lombok.Data;

@Data
public class ErrorResponse {

    private String code;
    private String message;

    public ErrorResponse(String errorCode, String errorMessage) {
        this.code = errorCode;
        this.message = errorMessage;
    }
}
