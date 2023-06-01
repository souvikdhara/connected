package com.connected.controllers;

import com.connected.domain.Account;
import com.connected.services.AccountService;
import com.connected.validators.ProfileEditValidationGroup;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class EditProfileController {

	private AccountService accountService;

	public EditProfileController(AccountService accountService) {
		this.accountService = accountService;
	}

	@GetMapping("/{profileString}/profile")
	public String editProfilePage(@ModelAttribute("editedProfile") Account editedProfile,
			@PathVariable String profileString, Model model) {
		Account currentUser = accountService.getCurrentUserAccountIfAuthenticated();
		if (!currentUser.getProfileString().equals(profileString)) {
			return "redirect:/home";
		}
		model.addAttribute("currentUser", currentUser);
		return "editProfile";
	}

	@PostMapping("/{profileString}/profile")
	public String updateUserProfile(
			@Validated(ProfileEditValidationGroup.class) @ModelAttribute("editedProfile") Account editedProfile,
			BindingResult bindingResult, Model model) {
		if (bindingResult.hasErrors()) {
			model.addAttribute("currentUser", accountService.getCurrentUserAccountIfAuthenticated());
			return "editProfile";
		}
		accountService.updateUserProfile(editedProfile.getUserName(), editedProfile.getProfileString());
		return "redirect:/" + editedProfile.getProfileString();
	}
}
