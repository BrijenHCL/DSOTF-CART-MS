/*
 * Copyright (C) 2019 ULTA
 * http://www.ulta.com
 * BrijendraK@ulta.com
 * All rights reserved
 */
package com.ulta.cart.controller;

import static com.ulta.cart.constant.CartConstants.ADDLINEITEM_URI;
import static com.ulta.cart.constant.CartConstants.CART_BASE_URI;
import static com.ulta.cart.constant.CartConstants.GET_ALL_CARTS_URI;
import static com.ulta.cart.constant.CartConstants.REMOVE_LINEITEM_URI;

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
	public ResponseEntity<Cart> addItemToCart(@RequestBody CreateCartRequest requestDto)
			throws CartException {
		log.info("Add Item to Cart Start");
		Cart fetchedCart = null;
		try {
			fetchedCart = cartService.addToCart(requestDto);
			log.info("Line item added successfully to cart for user.");
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
	public ResponseEntity<PagedQueryResult<Cart>> getAllCarts() throws CartException {
		PagedQueryResult<Cart> carts=null;
		try {
			carts = cartService.getAllCarts();
		} catch (Exception e) {
			log.error("exception occured while fetching all carts, details-" + e.getMessage());
			throw new CartException(e.getMessage());
		}
		return ResponseEntity.ok().body(carts);
	}

	/**
	 * 
	 * @param removeLineItemRequest
	 * @return
	 * @throws CartException
	 */

	@RequestMapping(path = REMOVE_LINEITEM_URI, method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Cart> removeItemFromCart(
			@RequestBody RemoveLineItemRequest removeLineItemRequest) throws CartException {
		Cart carts = cartService.removeLineItem(removeLineItemRequest);
		return ResponseEntity.ok().body(carts);
	}

	public void setCartService(CartService cartService) {
		this.cartService = cartService;
	}

}
