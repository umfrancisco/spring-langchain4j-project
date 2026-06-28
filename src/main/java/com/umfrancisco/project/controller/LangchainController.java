package com.umfrancisco.project.controller;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import com.umfrancisco.project.service.LangchainService;

@RestController
@CrossOrigin(origins="http://localhost:3000")
public class LangchainController {

	private LangchainService service;
	
	public LangchainController(LangchainService service) {
		this.service = service;
	}
	
	@GetMapping("/ai/{prompt}")
	public String hello(@PathVariable String prompt) {
		return service.run(prompt);
	}
}
