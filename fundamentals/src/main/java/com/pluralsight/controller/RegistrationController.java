package com.pluralsight.controller;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.pluralsight.entities.User;
import com.pluralsight.repositorities.RegistrationRepository;

@Controller
public class RegistrationController {

	// private static String UPLOADED_FOLDER =
	// "C:\\Users\\dsamband\\Documents\\ProjectFiles";

	@Autowired
	RegistrationRepository registration;

	/*@Autowired
	private RegistrationService service;*/

	@GetMapping("/register")
	public String registerUser(Model model, User user) {
		System.out.println("--------------Registration Controller GET--------------");
		model.addAttribute("register", new User());
		return "register";
	}

	@PostMapping("/register")
	public String welcomeUser(@ModelAttribute User user, @RequestParam("file") MultipartFile file,
			BindingResult result, RedirectAttributes redirectAttributes) {
		System.out.println("--------------Registration Controller POST--------------");
		if (file.isEmpty()) {
			redirectAttributes.addFlashAttribute("message", "Please select a file to upload");
			return "redirect:uploadStatus";
		}
		try {

			// Get the file and save it somewhere
			byte[] bytes = file.getBytes();
			Path path = Paths.get(file.getOriginalFilename());
			Files.write(path, bytes);
			// String fileName = path.getFileName().toString();
			// System.out.println(f.getAbsolutePath().substring(f.getAbsolutePath().lastIndexOf("\\")+1));
			redirectAttributes.addFlashAttribute("message",
					"You successfully uploaded '" + file.getOriginalFilename() + "'");

		} catch (IOException e) {
			e.printStackTrace();
		}

		if(result.hasErrors()) {
			return "register";
		}
		registration.save(user);
		return "welcome";
	}

	@GetMapping("/edit/{userId}")
	public String saveUser(@PathVariable("userId") long userId, Model model, @ModelAttribute User user) {
		//user = service.get(userId);
		user = registration.findById(userId).get();
		model.addAttribute(user);
		return "edit";
	}

	@PostMapping("/edit/{userId}")
	public String updateUser(@PathVariable("userId") long userId, @Valid @ModelAttribute User user,
			BindingResult result, Model model) {
		if (result.hasErrors()) {
			user.setUserId(userId);
			return "edit";
		}

		registration.save(user);
		return "welcome";
	}

	@ModelAttribute("expDropdownValues")
	public String[] getexpDropdownValues() {
		return new String[] { "0-1", "1-5", "5-10", "10>" };
	}

	@ModelAttribute("noticedropdownValues")
	public String[] getnoticedropdownValues() {
		return new String[] { "0-15", "15-30", "30-60", "90" };
	}

}
