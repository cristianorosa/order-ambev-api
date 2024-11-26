package com.ambev.enums;

public enum StatusEnum {
	
	AWAITING_PAYMENT("AWAITING_PAYMENT"),
	AUTHORIZED_ORDER("AUTHORIZED_ORDER"),
	PAYMENT_APPROVED("PAYMENT APPROVED"),
	PAYMENT_REJECTED("PAYMENT_REJECTED"),
	PAYMENT_CANCELED("PAYMENT_CANCELED"),
	ORDER_SEPARATION("ORDER_SEPARATION"),
	ORDER_INVOICED("ORDER_INVOICED"),
	READY_TO_SHIP("READY_TO_SHIP"),
	DELIVERY_EXCEPTION("DELIVERY_EXCEPTION"),
	IN_TRANSPORT("IN_TRANSPORT"),
	DELIVERED("IN_TRANSPORT");
	
	private final String value;
	
	StatusEnum(String value) {
		this.value = value;
		
	}
	
	public String getValue() {
		return this.value;
	}
	
}
