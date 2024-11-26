package com.ambev.exception;

public class BusinessException extends Exception {
	private static final long serialVersionUID = -2107184658843282904L;

    public BusinessException(String message) {
       super(message);
    }
}