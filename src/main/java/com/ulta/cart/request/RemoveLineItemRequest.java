/*
 * Copyright (C) 2019 ULTA
 * http://www.ulta.com
 * AmitMish@ulta.com
 * All rights reserved
 */ 
  
package com.ulta.cart.request;

/**
 * @author AmitMish
 *
 */
public class RemoveLineItemRequest {

	private String cartId;
	
	private String productId;
	
	private long quantity;

	public String getCartId() {
		return cartId;
	}

	public void setCartId(String cartId) {
		this.cartId = cartId;
	}

	public String getProductId() {
		return productId;
	}

	public void setProductId(String productId) {
		this.productId = productId;
	}

	public long getQuantity() {
		return quantity;
	}

	public void setQuantity(long quantity) {
		this.quantity = quantity;
	}
}
