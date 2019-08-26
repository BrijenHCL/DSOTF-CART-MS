package com.ulta.cart.serviceImpl;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;

import com.ulta.cart.exception.CartException;
import com.ulta.cart.request.CreateCartRequest;
import com.ulta.cart.request.RemoveLineItemRequest;

import io.netty.util.concurrent.CompleteFuture;
import io.sphere.sdk.carts.Cart;
import io.sphere.sdk.carts.LineItem;
import io.sphere.sdk.carts.commands.CartCreateCommand;
import io.sphere.sdk.carts.queries.CartByCustomerIdGet;
import io.sphere.sdk.carts.queries.CartByIdGet;
import io.sphere.sdk.carts.queries.CartQuery;
import io.sphere.sdk.client.SphereClient;
import io.sphere.sdk.products.ProductProjection;
import io.sphere.sdk.products.queries.ProductProjectionQuery;
import io.sphere.sdk.queries.PagedQueryResult;

@SpringBootTest
public class CartServiceImplTest {

	CartServiceImpl cartService = new CartServiceImpl();
	
	@Mock
	SphereClient sphereClient;
	
	@Before
	public void init() {
		MockitoAnnotations.initMocks(this);
		cartService.setSphereClient(sphereClient);
		
	}
	
	@Test(expected=NullPointerException.class)
	public void testAddtocart() throws CartException, InterruptedException, ExecutionException {
		CreateCartRequest requestDto= new CreateCartRequest();
		requestDto.setAnonymousUser(true);
		CompletionStage<Cart> cart = Mockito.mock(CompletionStage.class);
		CartCreateCommand cartCreateCommand = Mockito.mock(CartCreateCommand.class);
		CreateCartRequest customer= new CreateCartRequest();
		CompletableFuture<Cart> value = new CompletableFuture<>();
		Cart cart1 = Mockito.mock(Cart.class);
		value.complete(cart1 );
		//when(cartService.(customer)).thenReturn(value);
		//when(client.execute(cartCreateCommand)).thenReturn(cart);
		//AddLineItem action = Mockito.mock(AddLineItem.class);
		when(sphereClient.execute(cartCreateCommand)).thenReturn(cart);
		cartService.addToCart(requestDto);
	}
	
	@Test(expected=CartException.class)
	public void testCreateCartForAnonymousUser() throws CartException, InterruptedException, ExecutionException{
		CreateCartRequest requestDto= new CreateCartRequest();
		requestDto.setAnonymousUser(false);
		CompletionStage<Cart> cart = Mockito.mock(CompletionStage.class);
		CartCreateCommand cartCreateCommand = Mockito.mock(CartCreateCommand.class);
		CreateCartRequest customer= new CreateCartRequest();
		CompletableFuture<Cart> value = new CompletableFuture<>();
		Cart cart1 = Mockito.mock(Cart.class);
		value.complete(cart1 );
		//when(cartService.(customer)).thenReturn(value);
		//when(client.execute(cartCreateCommand)).thenReturn(cart);
		//AddLineItem action = Mockito.mock(AddLineItem.class);
		when(sphereClient.execute(cartCreateCommand)).thenReturn(cart);
		cartService.addToCart(requestDto);
	}
	
	@Test(expected=CartException.class)
	public void testRemoveLineItemWhenLineItemIsEmpty() {
		/*Cart value= Mockito.mock(Cart.class);
		CompletableFuture<Cart> completeFuture = new CompletableFuture<Cart>();
		completeFuture.complete(value);
		CompletionStage<Cart> c= 
		c.whenComplete((BiConsumer<? super Cart, ? super Throwable>) completeFuture);*/
		CompletableFuture<Cart> completeFuture = new CompletableFuture<Cart>();
		Cart value= Mockito.mock(Cart.class);
		LineItem e= Mockito.mock(LineItem.class);
		value.getLineItems().add(e);
		completeFuture.complete(value);
		CompletionStage<Cart> fetchedCart = Mockito.mock(CompletionStage.class);
		RemoveLineItemRequest removeLineItemRequest= new RemoveLineItemRequest();
		removeLineItemRequest.setCartId("Customer1");
		when(sphereClient.execute(CartByIdGet.of(removeLineItemRequest.getCartId()))).thenReturn(fetchedCart);
		when(cartService.getCart(removeLineItemRequest)).thenReturn(completeFuture);
		cartService.removeLineItem(removeLineItemRequest);
	}
	
	@Test(expected=CartException.class)
	public void testgetAllCart() throws CartException, InterruptedException, ExecutionException {
		CartQuery query= Mockito.mock(CartQuery.class);
		CompletionStage<PagedQueryResult<Cart>> value= Mockito.mock(CompletableFuture.class);
		when(sphereClient.execute(query)).thenReturn(value);
		cartService.getAllCarts();
	}
	
	
	@Test
	public void testisCartAvailbale(){
		String customerId= "CUSTO1";
		CompletionStage<Cart> fetchedCart = Mockito.mock(CompletionStage.class);
		CartByCustomerIdGet request = CartByCustomerIdGet.of(customerId);
		when(sphereClient.execute(request)).thenReturn(fetchedCart);
	boolean value=	cartService.isCartAvailable(customerId);
	assertEquals(false, value);
		
	}
	
	@Test(expected=CartException.class)
	public void testisCartAvailbaleforException(){
		String customerId= "CUSTO1";
		CompletionStage<Cart> fetchedCart = Mockito.mock(CompletionStage.class);
		CartByCustomerIdGet request = CartByCustomerIdGet.of(customerId);
		when(sphereClient.execute(request)).thenThrow(new CartException("Failure"));
	boolean value=	cartService.isCartAvailable(customerId);
		
	}
	
	
	@Test(expected=CartException.class)
	public void testaddItemToCartFallback() throws CartException, InterruptedException, ExecutionException {
		CreateCartRequest createCartRequest= new CreateCartRequest();
		cartService.addItemToCartFallback(createCartRequest);
	}
	
	@Test(expected=CartException.class)
	public void testgetAllCartsFallback() throws CartException, InterruptedException, ExecutionException {
		cartService.getAllCartsFallback();
	}
	@Test(expected=CartException.class)
	public void testremoveLineItemFallback() throws CartException, InterruptedException, ExecutionException {
		RemoveLineItemRequest request= new RemoveLineItemRequest();
		cartService.removeLineItemFallback(request);
	}
	
	@Test
	public void  testGetCurrency() {
		cartService.getCurrency("US").getContext();
		cartService.getCurrency("US").getCurrencyCode();
		cartService.getCurrency("US").getNumericCode();
		cartService.getCurrency("US").getDefaultFractionDigits();
		
		
		
		
	}
}
