package com.connected.controllers;

import com.connected.domain.Account;
import com.connected.services.AccountService;
import com.connected.validators.AccountCreationValidationGroup;

import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class SecurityController {

	AccountService accountService;

	public SecurityController(AccountService accountService) {
		this.accountService = accountService;
	}

	@GetMapping("user/registration")
	public String getRegistrationPage(@ModelAttribute("newUser") Account newUser) {
		return "registration";
	}

	@PostMapping("user/registration")
	public String createNewUser(
			@Validated(AccountCreationValidationGroup.class) @ModelAttribute("newUser") Account newUser,
			BindingResult bindingResult) {
		if (bindingResult.hasErrors()) {
			return "registration";
		}
		accountService.saveUser(newUser);
		return "redirect:/home";
	}
}
