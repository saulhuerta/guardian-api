package com.example.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.annotation.RateLimited;

@RestController
@RequestMapping("/api/v1")
public class GuardianController {

	@GetMapping("/hello")
	@RateLimited(requests = 3, period = 10)
	public String SayHello() {
		return "Hello :)";
	}
}
