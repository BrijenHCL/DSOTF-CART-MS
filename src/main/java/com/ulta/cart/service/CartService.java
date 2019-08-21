/*
 * Copyright (C) 2019 ULTA
 * http://www.ulta.com
 * BrijendraK@ulta.com
 * All rights reserved
 */
package com.ulta.cart.service;

import java.util.concurrent.CompletableFuture;

import org.springframework.stereotype.Service;

import com.ulta.cart.exception.CartException;
import com.ulta.cart.request.CreateCartRequest;
import com.ulta.cart.request.RemoveLineItemRequest;

import io.sphere.sdk.carts.Cart;
import io.sphere.sdk.queries.PagedQueryResult;

@Service
public interface CartService {

	public CompletableFuture<Cart> addToCart(CreateCartRequest requestDto) throws CartException;

	public CompletableFuture<PagedQueryResult<Cart>> getAllCarts() throws CartException;
	
	public CompletableFuture<Cart> removeLineItem(RemoveLineItemRequest removeLineItemRequest) throws CartException;
}
