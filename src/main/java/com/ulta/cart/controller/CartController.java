/*
 * Copyright (C) 2019 ULTA
 * http://www.ulta.com
 * BrijendraK@ulta.com
 * All rights reserved
 */
package com.ulta.cart.controller;

import static com.ulta.cart.constant.CartConstants.*;

import java.util.concurrent.CompletableFuture;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.ulta.cart.exception.CartException;
import com.ulta.cart.request.CreateCartRequest;
import com.ulta.cart.request.RemoveLineItemRequest;
import com.ulta.cart.service.CartService;

import io.sphere.sdk.carts.Cart;
import io.sphere.sdk.queries.PagedQueryResult;

@RequestMapping(CART_BASE_URI)
@RestController
public class CartController {

	@Autowired
	private CartService cartService;

	static Logger log = LoggerFactory.getLogger(CartController.class);

	/**
	 * 
	 * @param requestDto
	 * @return
	 * @throws CartException
	 */
	@RequestMapping(path = ADDLINEITEM_URI, method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<CompletableFuture<Cart>> addItemToCart(@RequestBody CreateCartRequest requestDto) throws CartException {
		log.info("Add Item to Cart Start");
		CompletableFuture<Cart> fetchedCart = null;
		try {
			fetchedCart = cartService.addToCart(requestDto);

		} catch (Exception e) {
			log.error("exception during adding item to cart, details-" + e.getMessage());
			throw new CartException(e.getMessage());
		}
		log.info("Add Item to Cart End");
		return ResponseEntity.ok().body(fetchedCart);
	}

	/**
	 * 
	 * @return
	 * @throws CartException
	 */

	@RequestMapping(path = GET_ALL_CARTS_URI, method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<CompletableFuture<PagedQueryResult<Cart>>> getAllCarts() throws CartException {
		CompletableFuture<PagedQueryResult<Cart>> carts=cartService.getAllCarts();
		return ResponseEntity.ok().body(carts);
	}
	
	@RequestMapping(path = REMOVE_LINEITEM_URI, method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<CompletableFuture<Cart>> removeItemFromCart(@RequestBody RemoveLineItemRequest removeLineItemRequest) throws CartException {
		CompletableFuture<Cart> carts=cartService.removeLineItem(removeLineItemRequest);
		return ResponseEntity.ok().body(carts);
	}

	public CartService getCartService() {
		return cartService;
	}

	public void setCartService(CartService cartService) {
		this.cartService = cartService;
	}

}
