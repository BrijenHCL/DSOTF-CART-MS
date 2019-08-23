/*
 * Copyright (C) 2019 ULTA
 * http://www.ulta.com
 * BrijendraK@ulta.com
 * All rights reserved
 */
package com.ulta.cart.service;

import java.util.concurrent.ExecutionException;

import org.springframework.stereotype.Service;

import com.ulta.cart.exception.CartException;
import com.ulta.cart.request.CreateCartRequest;
import com.ulta.cart.request.RemoveLineItemRequest;

import io.sphere.sdk.carts.Cart;
import io.sphere.sdk.queries.PagedQueryResult;

@Service
public interface CartService {

	public Cart addToCart(CreateCartRequest requestDto) throws CartException, InterruptedException, ExecutionException;

	public PagedQueryResult<Cart> getAllCarts() throws CartException, InterruptedException, ExecutionException;

	public Cart removeLineItem(RemoveLineItemRequest removeLineItemRequest) throws CartException;
}
