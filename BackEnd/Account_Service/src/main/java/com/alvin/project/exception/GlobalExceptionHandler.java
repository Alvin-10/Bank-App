package com.alvin.project.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

@ControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(AccountNotFoundException.class)
	public ResponseEntity<?> handleAccountNotFoundException(AccountNotFoundException ex, WebRequest request) {
		return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_FOUND);// 404
	}

	@ExceptionHandler(InsufficientBalanceException.class)
	public ResponseEntity<?> handleInsufficientBalanceException(InsufficientBalanceException ex, WebRequest request) {
		return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);// 500
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<?> handleGlobalException(Exception ex, WebRequest request) {
		return new ResponseEntity<>(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);// 500
	}
}