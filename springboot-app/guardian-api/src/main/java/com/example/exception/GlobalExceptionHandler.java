package com.example.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.example.dto.ErrorResponseDto;

@RestControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(RuntimeException.class)
	public ResponseEntity<ErrorResponseDto> handleRateLimit(RuntimeException ex) {

		ErrorResponseDto error = new ErrorResponseDto(HttpStatus.TOO_MANY_REQUESTS.value(), ex.getMessage(),
				System.currentTimeMillis());

		// Return 429 (Too Many Requests) instead of 500
		return new ResponseEntity<>(error, HttpStatus.TOO_MANY_REQUESTS);
	}

}
