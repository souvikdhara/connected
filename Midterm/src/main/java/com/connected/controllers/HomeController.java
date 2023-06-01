package com.connected.controllers;

import com.connected.domain.Account;
import com.connected.services.AccountService;
import com.connected.services.WallPostService;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class HomeController {

	private final WallPostService wallPostService;
	private final AccountService accountService;

	public HomeController(WallPostService wallPostService, AccountService accountService) {
		this.wallPostService = wallPostService;
		this.accountService = accountService;
	}

	@GetMapping("/home")
	public String climbookHome(Model model) {
		Account currentUser = accountService.getCurrentUserAccountIfAuthenticated();
		model.addAttribute("currentUser", currentUser);
		model.addAttribute("users", accountService.getAllUsers());
		model.addAttribute("wallPosts", wallPostService.getHomePageWallPosts(currentUser));
		model.addAttribute("followers", accountService.getFollowers(currentUser));
		model.addAttribute("followed", accountService.getFollowing(currentUser));

		return "home";
	}

}
