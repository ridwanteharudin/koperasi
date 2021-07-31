package com.alami.koperasi.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.alami.koperasi.dto.controller.response.Response;

@RestController
@RequestMapping("/")
public class RootController {
	@RequestMapping(path = "/", method = RequestMethod.GET)
	public Response<String> root() {
		Response<String> response = new Response<String> ("koperasi-alami");
		return response;
	}
}
