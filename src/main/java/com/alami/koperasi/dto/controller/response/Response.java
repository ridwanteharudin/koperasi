package com.alami.koperasi.dto.controller.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Response<T> {
	private static final String SUCCESS = "success";
	private static final String ERROR = "error";
	
	private String status;
	private T data;	
	private InfoResponse info;
	
	public Response(){}
	public Response(T object)
	{
		this.status = Response.SUCCESS;
		this.data = object;
	}
	
	
	public void setError(InfoResponse info) {
		this.status = Response.ERROR;
		this.info = info;
	}
	
	public void setError(String id, String message) {
		this.status = Response.ERROR;
		
		InfoResponse error = new InfoResponse();
		error.setId(id);
		error.setMessage(message);

		this.info = error;
	}
	
	public void setError(String id, String message, String field, String redirect) {
		this.status = Response.ERROR;
		
		InfoResponse error = new InfoResponse();
		error.setId(id);
		error.setMessage(message);
		error.setField(field);
		error.setRedirect(redirect);

		this.info = error;
	}
	
	public void setSuccess(T object){
		this.status = Response.SUCCESS;
		this.data = object;
	}
}
