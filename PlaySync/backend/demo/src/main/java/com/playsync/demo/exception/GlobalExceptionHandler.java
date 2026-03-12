package com.playsync.demo.exception;

import org.springframework.http.HttpStatus;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.server.ResponseStatusException;

import com.playsync.demo.dtoresponse.ErroResponse;

import jakarta.servlet.http.HttpServletRequest;

@ControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(ResponseStatusException.class)
	public ResponseEntity<ErroResponse> handleResponseStatusException(ResponseStatusException ex,
			HttpServletRequest request) {

		ErroResponse erro = new ErroResponse(ex.getStatusCode().value(),
				((HttpStatus) ex.getStatusCode()).getReasonPhrase(), ex.getReason(), request.getRequestURI());

		return ResponseEntity.status(ex.getStatusCode()).body(erro);
	}

	// Tratamento genérico para qualquer outra exceção
	@ExceptionHandler(Exception.class)
	public ResponseEntity<ErroResponse> handleGenericException(Exception ex, HttpServletRequest request) {
		ex.printStackTrace(); // Para debug no console
		ErroResponse erro = new ErroResponse(
				HttpStatus.INTERNAL_SERVER_ERROR.value(),
				"Erro interno do servidor",
				ex.getMessage(),
				request.getRequestURI());

		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(erro);
	}
}