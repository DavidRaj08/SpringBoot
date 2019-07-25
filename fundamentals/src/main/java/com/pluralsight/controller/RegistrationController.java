package com.pluralsight.controller;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.pluralsight.entities.User;
import com.pluralsight.service.RegistrationService;

@Controller
public class RegistrationController {

	@Autowired
	RegistrationService registration;

	public RegistrationService getRegistration() {
		return registration;
	}

	@Autowired
	public void setRegistration(RegistrationService registration) {
		this.registration = registration;
	}

	@GetMapping("/register")
	public String registerUser(Model model, User user) {
		String[] expList = { "0-1", "1-6", "6-<10", "10+" };
		String[] noticeList = { "0-15", "15-60", "60-90" };
		user.setExperience(Arrays.asList(expList));
		user.setNoticePeriod(Arrays.asList(noticeList));
		model.addAttribute("register", new User());

		return "register";
	}

	@PostMapping("/register")
	public String welcomeUser(@ModelAttribute User user, @RequestParam("file") MultipartFile file) {
		System.out.println("experience: " + user.getExperience());
		System.out.println("Notice: " + user.getNoticePeriod());
		if (file.isEmpty()) {
			return "error";
		} else {
			return "welcome";
		}

	}

	@RequestMapping("/error")
	public String handleError() {
		return "error";
	}

}
