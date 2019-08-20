/*
 * Copyright (C) 2019 ULTA
 * http://www.ulta.com
 * BrijendraK@ulta.com
 * All rights reserved
 */
package com.ulta.cart.request;

import com.fasterxml.jackson.annotation.JsonInclude;

import io.sphere.sdk.models.Address;

@JsonInclude(JsonInclude.Include.NON_NULL) 
public class CreateCartRequest {

	private String customerId;
	private String customerEmail;
	private Address customerAddress;
	private String productId;
	private boolean isAnonymousUser;
	private long quantity;
	
	public long getQuantity() {
		return quantity;
	}
	public void setQuantity(long quantity) {
		this.quantity = quantity;
	}
	public boolean isAnonymousUser() {
		return isAnonymousUser;
	}
	public void setAnonymousUser(boolean isAnonymousUser) {
		this.isAnonymousUser = isAnonymousUser;
	}
	public String getProductId() {
		return productId;
	}
	public void setProductId(String productId) {
		this.productId = productId;
	}
	public String getCustomerId() {
		return customerId;
	}
	public void setCustomerId(String customerId) {
		this.customerId = customerId;
	}
	public String getCustomerEmail() {
		return customerEmail;
	}
	public void setCustomerEmail(String customerEmail) {
		this.customerEmail = customerEmail;
	}
	public Address getCustomerAddress() {
		return customerAddress;
	}
	public void setCustomerAddress(Address customerAddress) {
		this.customerAddress = customerAddress;
	}
	
}
