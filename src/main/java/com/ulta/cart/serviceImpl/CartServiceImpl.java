/*
 * Copyright (C) 2019 ULTA
 * http://www.ulta.com
 * BrijendraK@ulta.com
 * All rights reserved
 */
package com.ulta.cart.serviceImpl;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ExecutionException;

import javax.money.CurrencyContext;
import javax.money.CurrencyUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.neovisionaries.i18n.CountryCode;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.ulta.cart.exception.CartException;
import com.ulta.cart.request.CreateCartRequest;
import com.ulta.cart.request.RemoveLineItemRequest;
import com.ulta.cart.resources.HystrixCommandPropertyResource;
import com.ulta.cart.service.CartService;

import io.sphere.sdk.carts.Cart;
import io.sphere.sdk.carts.CartDraft;
import io.sphere.sdk.carts.LineItem;
import io.sphere.sdk.carts.commands.CartCreateCommand;
import io.sphere.sdk.carts.commands.CartUpdateCommand;
import io.sphere.sdk.carts.commands.updateactions.AddLineItem;
import io.sphere.sdk.carts.commands.updateactions.RemoveLineItem;
import io.sphere.sdk.carts.queries.CartByCustomerIdGet;
import io.sphere.sdk.carts.queries.CartByIdGet;
import io.sphere.sdk.carts.queries.CartQuery;
import io.sphere.sdk.carts.queries.CartQueryBuilder;
import io.sphere.sdk.client.SphereClient;
import io.sphere.sdk.queries.PagedQueryResult;

@Service
public class CartServiceImpl implements CartService {

	private static final int MASTER_VARIANT_ID = 1;
	@Autowired
	SphereClient sphereClient;
	@Autowired
	HystrixCommandPropertyResource hystrixCommandProp;
	
	Cart cart = null;
	CompletionStage<Cart> cartStage = null;
	static Logger log = LoggerFactory.getLogger(CartServiceImpl.class);

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ulta.cart.service.CartService#addToCart(com.ulta.cart.request.
	 * CreateCartRequest)
	 */
	@SuppressWarnings("deprecation")
	@Override
	@HystrixCommand(fallbackMethod = "addItemToCartFallback", ignoreExceptions = {
			CartException.class }, commandKey = "CARTADDITEMCommand", threadPoolKey = "CARTThreadPool")
	public Cart addToCart(CreateCartRequest requestDto) throws CartException, InterruptedException, ExecutionException {
		if (requestDto.isAnonymousUser()) {
			try {
				cart = createCartForAnonymousUser(requestDto).get();
			} catch (Exception e) {
				log.error("Exception during creating cart for anonymous user, details-" + e.getMessage());
				throw new CartException(e.getMessage());
			}
			log.info("Cart created Successfully for anonymous customer" + cart.getId());
		} else {
			boolean isCartAvailable = isCartAvailable(requestDto.getCustomerId());
			if (!isCartAvailable) {
				try {
					CompletableFuture<Cart> cartResponse= createCart(requestDto);
					if (null!=cartResponse){
						cart = cartResponse.get();	
						log.info("Cart created Successfully for existing customer");
					}
					else{
						throw new CartException("Unable to create Cart for this user");
					}
					
				} catch (Exception e) {
					log.error("Exception during creating cart for existing user, details-" + e.getMessage());
					throw new CartException(e.getMessage());
				}
				
			} else {
				log.info("Cart already available for provided user.");
			}
		}

		log.info("Adding line item to cart for user.");
		final AddLineItem action = AddLineItem.of(requestDto.getProductId(), MASTER_VARIANT_ID,
				requestDto.getQuantity());
		final CompletionStage<Cart> updatedCart = sphereClient.execute(CartUpdateCommand.of(cart, action));
		CompletableFuture<Cart> futureCart = null;
		
		if(null!=updatedCart) {
			futureCart=updatedCart.toCompletableFuture();
		}
		
		return futureCart.get();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ulta.cart.service.CartService#getAllCarts()
	 */
	@HystrixCommand(fallbackMethod = "getAllCartsFallback", ignoreExceptions = {
			CartException.class }, commandKey = "CARTGETALLCommand", threadPoolKey = "CARTThreadPool")
	@Override
	public PagedQueryResult<Cart> getAllCarts() throws CartException, InterruptedException, ExecutionException {
		CartQueryBuilder cartQueryBuilder = CartQueryBuilder.of().fetchTotal(true);
		CartQuery query = cartQueryBuilder.build();
		CompletionStage<PagedQueryResult<Cart>> cartList = sphereClient.execute(query);
		CompletableFuture<PagedQueryResult<Cart>> cart;
		if (null != cartList) {
			cart = cartList.toCompletableFuture();
		} else {
			throw new CartException("Cart list is empty");
		}
		return cart.get();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ulta.cart.service.CartService#removeLineItem(com.ulta.cart.request.
	 * RemoveLineItemRequest)
	 */
	@HystrixCommand(fallbackMethod = "removeLineItemFallback", ignoreExceptions = {
			CartException.class }, commandKey = "CARTREMOVELINEITEMCommand", threadPoolKey = "CARTThreadPool")
	@Override
	public Cart removeLineItem(RemoveLineItemRequest removeLineItemRequest) throws CartException {
		log.info("Removing line item from cart start");
		CompletableFuture<Cart> futureCart= getCart(removeLineItemRequest);
		
		try {
			
			Cart flattenedCart = futureCart.get();
			List<LineItem> lineItems = flattenedCart.getLineItems();
			lineItems.forEach(lineItem -> {
				if (lineItem.getProductId().equals(removeLineItemRequest.getProductId().trim())) {
					if (lineItem.getQuantity() == removeLineItemRequest.getQuantity()) {
						cartStage = sphereClient
								.execute(CartUpdateCommand.of(flattenedCart, RemoveLineItem.of(lineItem)));
					}
					else {
							cartStage = sphereClient.execute(CartUpdateCommand.of(flattenedCart,
									RemoveLineItem.of(lineItem, removeLineItemRequest.getQuantity())));
					}
				}
			});
			if (null != cartStage) {
				futureCart = cartStage.toCompletableFuture();
			} else {
				throw new CartException("Cart list is empty");
			}
			cart= futureCart.get();
			return cart;
		} catch (Exception e) {
			log.error("Exception during removing line item from cart, details-" + e.getMessage());
			throw new CartException(e.getMessage());
		}
	}

	public CompletableFuture<Cart> getCart(RemoveLineItemRequest removeLineItemRequest) {
		CompletionStage<Cart> fetchedCart = sphereClient.execute(CartByIdGet.of(removeLineItemRequest.getCartId()));
		CompletableFuture<Cart> futureCart = fetchedCart.toCompletableFuture();
		return futureCart;
	}


	/**
	 * 
	 * @param customerId
	 * @return
	 */

	public boolean isCartAvailable(String customerId) {
		final CartByCustomerIdGet request = CartByCustomerIdGet.of(customerId);
		final CompletionStage<Cart> fetchedCart = sphereClient.execute(request);
		CompletableFuture<Cart> futureCart = null;
		try {
			if (null!= fetchedCart) {
				futureCart=fetchedCart.toCompletableFuture();
				if (null!= futureCart&& null != futureCart.get())
					return true;
			}
		} catch (Exception e) {
			log.error("Exception during checking available cart for user, details-" + e.getMessage());
			throw new CartException(e.getMessage());
		}
		return false;
	}

	/**
	 * 
	 * @param requestDto
	 * @return
	 */

	private CompletableFuture<Cart> createCart(CreateCartRequest requestDto) {
		log.info("Creating cart for existing customer");
		final CartDraft cartDraft = CartDraft.of(getCurrency("EUR")).withCountry(CountryCode.DE)
				.withCustomerId(requestDto.getCustomerId()).withCustomerEmail(requestDto.getCustomerEmail())
				.withShippingAddress(requestDto.getCustomerAddress())
				.withBillingAddress(requestDto.getCustomerAddress());

		final CartCreateCommand cartCreateCommand = CartCreateCommand.of(cartDraft);
		final CompletionStage<Cart> cart = sphereClient.execute(cartCreateCommand);
		if(null!=cart) {
			return cart.toCompletableFuture();	
		}
		else return null;
	}

	/**
	 * 
	 * @param customer
	 * @return
	 */

	private CompletableFuture<Cart> createCartForAnonymousUser(CreateCartRequest customer) {
		log.info("Creating cart for anonymous customer");
		final CartDraft cartDraft = CartDraft.of(getCurrency("EUR")).withCountry(CountryCode.DE);
		final CartCreateCommand cartCreateCommand = CartCreateCommand.of(cartDraft);
		final CompletionStage<Cart> cart = sphereClient.execute(cartCreateCommand);
		if (null != cart) {
			return cart.toCompletableFuture();
		}
		return null;
	}

	public Cart addItemToCartFallback(CreateCartRequest createCartRequest)
			throws CartException, InterruptedException, ExecutionException {
		log.error("Critical -  CommerceTool UnAvailability error");
		throw new CartException("Failure while adding line item to cart");
	}
	
	public PagedQueryResult<Cart> getAllCartsFallback() throws CartException{
		log.error("Critical -  CommerceTool UnAvailability error");
		throw new CartException("Failure while fetching all carts");
	}
	
	public Cart removeLineItemFallback(RemoveLineItemRequest request) throws CartException {
		log.error("Critical -  CommerceTool UnAvailability error");
		throw new CartException("Failure while removing line item");
	}

	public CurrencyUnit getCurrency(String code) {
		CurrencyUnit cu = new CurrencyUnit() {

			@Override
			public int compareTo(CurrencyUnit o) {
				
				return 0;
			}

			@Override
			public int getNumericCode() {
				
				return 0;
			}

			@Override
			public int getDefaultFractionDigits() {
				
				return 0;
			}

			@Override
			public String getCurrencyCode() {
				// TODO Auto-generated method stub
				return code.trim();
			}

			@Override
			public CurrencyContext getContext() {
				// TODO Auto-generated method stub
				return null;
			}
		};
		return cu;
	}



	/**
	 * 
	 * @param sphereClient
	 */

	public void setSphereClient(SphereClient sphereClient) {
		this.sphereClient = sphereClient;
	}

}
