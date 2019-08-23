/*
 * Copyright (C) 2019 ULTA
 * http://www.ulta.com
 * AmitMish@ulta.com
 * All rights reserved
 */

package com.ulta.cart.controller;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import java.util.concurrent.ExecutionException;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.ulta.cart.exception.CartException;
import com.ulta.cart.request.CreateCartRequest;
import com.ulta.cart.request.RemoveLineItemRequest;
import com.ulta.cart.service.CartService;

import io.sphere.sdk.carts.Cart;
import io.sphere.sdk.queries.PagedQueryResult;

/**
 * @author AmitMish
 *
 */
@SpringBootTest
public class CartControllerTest {

	CartController cartController = new CartController();
	@Mock
	CartService cartService;

	@Before
	public void init() {
		MockitoAnnotations.initMocks(this);
		cartController.setCartService(cartService);
	}

	@Test
	public void addToCartTest() throws CartException, InterruptedException, ExecutionException {
		CreateCartRequest requestDto = new CreateCartRequest();
		Cart fetchedCart = Mockito.mock(Cart.class);
		when(cartService.addToCart(requestDto)).thenReturn(fetchedCart);
		ResponseEntity<Cart> result = cartController.addItemToCart(requestDto);
		assertEquals(HttpStatus.OK, result.getStatusCode());
	}

	@Test(expected = CartException.class)
	public void addToCartExceptionTest() throws CartException, InterruptedException, ExecutionException {
		CreateCartRequest requestDto = new CreateCartRequest();
		when(cartService.addToCart(requestDto)).thenThrow(new CartException("failure"));
		cartController.addItemToCart(requestDto);
	}

	@Test
	public void getAllCartsTest() throws CartException, InterruptedException, ExecutionException {
		PagedQueryResult<Cart> carts = (PagedQueryResult<Cart>) Mockito.mock(PagedQueryResult.class);
		when(cartService.getAllCarts()).thenReturn(carts);
		ResponseEntity<PagedQueryResult<Cart>> result = cartController.getAllCarts();
		assertEquals(HttpStatus.OK, result.getStatusCode());
	}

	@Test(expected = CartException.class)
	public void getAllCartsExceptionTest() throws CartException, InterruptedException, ExecutionException {
		PagedQueryResult<Cart> carts = (PagedQueryResult<Cart>) Mockito.mock(PagedQueryResult.class);
		when(cartService.getAllCarts()).thenThrow(new CartException("failure"));
		cartController.getAllCarts();
	}

	@Test
	public void removeItemFromCartTest() {
		Cart carts = Mockito.mock(Cart.class);
		RemoveLineItemRequest removeLineItemRequest = new RemoveLineItemRequest();
		when(cartService.removeLineItem(removeLineItemRequest)).thenReturn(carts);
		ResponseEntity<Cart> result = cartController.removeItemFromCart(removeLineItemRequest);
		assertEquals(HttpStatus.OK, result.getStatusCode());
	}

}
