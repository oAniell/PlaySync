package com.playsync.demo.dtoresponse;

import java.time.LocalDateTime;

public class ErroResponse {

	private LocalDateTime timestamp;
	private int status;
	private String error;
	private String mensagem;
	private String path;

	public ErroResponse(int status, String error, String mensagem, String path) {
		this.timestamp = LocalDateTime.now();
		this.status = status;
		this.error = error;
		this.mensagem = mensagem;
		this.path = path;
	}

	public LocalDateTime getTimestamp() {
		return timestamp;
	}

	public int getStatus() {
		return status;
	}

	public String getError() {
		return error;
	}

	public String getMensagem() {
		return mensagem;
	}

	public String getPath() {
		return path;
	}
}