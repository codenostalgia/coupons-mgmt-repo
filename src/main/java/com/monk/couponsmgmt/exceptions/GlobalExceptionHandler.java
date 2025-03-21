package com.monk.couponsmgmt.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<Object> handleNoResourceFoundException(NoResourceFoundException ex) {
        ErrorResponse errorResponse = new ErrorResponse("NOT_FOUND", "Invalid URI Path !!");
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<Object> handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException ex) {
        ErrorResponse errorResponse = new ErrorResponse("NOT_ACCEPTABLE", "ID must be an Integer !!");
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_ACCEPTABLE);
    }

    @ExceptionHandler(CouponNotFoundException.class)
    public ResponseEntity<Object> handleCouponNotFoundException(CouponNotFoundException ex) {
        ErrorResponse errorResponse = new ErrorResponse("NOT_FOUND", "Coupon ID does Not Exist !!");
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(CouponStructureInvalidException.class)
    public ResponseEntity<Object> handleInvalidCouponException(CouponStructureInvalidException ex) {
        ErrorResponse errorResponse = new ErrorResponse("NOT_ACCEPTABLE", ex.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_ACCEPTABLE);
    }

    @ExceptionHandler(NoCouponsFoundException.class)
    public ResponseEntity<Object> handleNoCouponsFoundException(NoCouponsFoundException ex) {
        ErrorResponse errorResponse = new ErrorResponse("NOT_FOUND", ex.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(InvalidCouponTypeException.class)
    public ResponseEntity<Object> handleInvalidTypeException(InvalidCouponTypeException ex) {
        ErrorResponse errorResponse = new ErrorResponse("NOT_ACCEPTABLE", ex.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_ACCEPTABLE);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Object> handleRequestBodyException(HttpMessageNotReadableException ex) {
        ErrorResponse errorResponse = new ErrorResponse("NOT_ACCEPTABLE", "PayLoad Structure is Invalid !!");
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_ACCEPTABLE);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Object> handleIllegalArgumentException(IllegalArgumentException ex) {
        ErrorResponse errorResponse = new ErrorResponse("BAD_REQUEST", ex.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleGenericException(Exception ex) {
        System.out.println(ex.getMessage());
        ErrorResponse errorResponse = new ErrorResponse("INTERNAL_SERVER_ERROR", "Something went wrong!");
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    public static class CouponNotFoundException extends RuntimeException {
        public CouponNotFoundException() {
            super();
        }
    }

    public static class CouponStructureInvalidException extends RuntimeException {
        public CouponStructureInvalidException(String message) {
            super(message);
        }
    }

    public static class NoCouponsFoundException extends RuntimeException {
        public NoCouponsFoundException(String message) {
            super(message);
        }
    }

    public static class InvalidCouponTypeException extends RuntimeException {
        public InvalidCouponTypeException(String message) {
            super(message);
        }
    }

}

