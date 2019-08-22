/*
 * Copyright (C) 2019 ULTA
 * http://www.ulta.com
 * AmitMish@ulta.com
 * All rights reserved
 */ 
  
package com.ulta.cart.controller;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import java.util.concurrent.CompletableFuture;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
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

	CartController cartController=new CartController();
	@Mock
	CartService cartService;
	
	@Before
	public void init() {
		MockitoAnnotations.initMocks(this);
		cartController.setCartService(cartService);
	}
	
	@Test
	public void addToCartTest() {
		CreateCartRequest requestDto=new CreateCartRequest();
		CompletableFuture<Cart> fetchedCart = new CompletableFuture<Cart>();
		when(cartService.addToCart(requestDto)).thenReturn(fetchedCart);
		ResponseEntity<CompletableFuture<Cart>> result=cartController.addItemToCart(requestDto);
		assertEquals(HttpStatus.OK, result.getStatusCode());		
	}
	
	@Test(expected = CartException.class)
	public void addToCartExceptionTest() {
		CreateCartRequest requestDto=new CreateCartRequest();
		when(cartService.addToCart(requestDto)).thenThrow(new CartException("failure"));
		cartController.addItemToCart(requestDto);
	}
	
	@Test
	public void getAllCartsTest() {
		CompletableFuture<PagedQueryResult<Cart>> carts=new CompletableFuture<PagedQueryResult<Cart>>();
		when(cartService.getAllCarts()).thenReturn(carts);
		ResponseEntity<CompletableFuture<PagedQueryResult<Cart>>> result=cartController.getAllCarts();
		assertEquals(HttpStatus.OK, result.getStatusCode());		
	}
	
	@Test
	public void removeItemFromCartTest() {
		CompletableFuture<Cart> carts=new CompletableFuture<Cart>();
		RemoveLineItemRequest removeLineItemRequest=new RemoveLineItemRequest();
		when(cartService.removeLineItem(removeLineItemRequest)).thenReturn(carts);
		ResponseEntity<CompletableFuture<Cart>> result=cartController.removeItemFromCart(removeLineItemRequest);
		assertEquals(HttpStatus.OK, result.getStatusCode());		
	}

}
