/*
 * Copyright (C) 2019 ULTA
 * http://www.ulta.com
 * BrijendraK@ulta.com
 * All rights reserved
 */
package com.ulta.cart.exception;

import java.util.Date;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;

@ControllerAdvice
@RestController
public class GlobalExceptionHandler {

	/**
	 * Handling the all exceptions of Exception class.
	 * 
	 * @param ex
	 * @return
	 */
	@ExceptionHandler(value = Exception.class)
	public final ResponseEntity<ErrorDetails> handleException(Exception ex) {
		ErrorDetails errorDetails = new ErrorDetails(new Date(), ex.getMessage(),
				String.valueOf(HttpStatus.INTERNAL_SERVER_ERROR.value()));
		return new ResponseEntity<>(errorDetails, HttpStatus.INTERNAL_SERVER_ERROR);
	}

	/**
	 * Handling the all exceptions of CartExcption class.
	 * 
	 * @param ex
	 * @return
	 */

	@ExceptionHandler(value = CartException.class)
	public final ResponseEntity<ErrorDetails> handleException(CartException ex) {
		ErrorDetails errorDetails = new ErrorDetails(new Date(), ex.getMessage(),
				String.valueOf(HttpStatus.INTERNAL_SERVER_ERROR.value()));
		return new ResponseEntity<>(errorDetails, HttpStatus.INTERNAL_SERVER_ERROR);
	}

}