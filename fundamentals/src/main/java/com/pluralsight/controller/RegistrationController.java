package com.pluralsight.controller;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import com.pluralsight.config.Constants;
import com.pluralsight.entities.Login;
import com.pluralsight.entities.User;
import com.pluralsight.exception.RegistrationException;
import com.pluralsight.repositorities.RegistrationRepository;
import com.pluralsight.service.RegistrationService;

@Controller
public class RegistrationController {

	private static String UPLOADED_FOLDER = "C:\\Users\\dsamband\\Documents\\ProjectFiles";

	@Autowired
	RegistrationService service;

	@Autowired
	RegistrationRepository registration;

	private String password;

	@RequestMapping(value = "/{[path:[^\\.]*}")
	public String redirect() {
		return "forward:/home";
	}

	@GetMapping("/home")
	public String home() {
		return Constants.HOME;
	}

	@GetMapping("/register")
	public String registerUser(Model model, User user) {
		model.addAttribute(Constants.REGISTER, new User());
		return Constants.REGISTER;
	}

	@PostMapping("/register")
	public ModelAndView welcomeUser(@Valid @ModelAttribute(value = "user") User user,
			@RequestParam("file") MultipartFile file, BindingResult result, ModelAndView model)
			throws RegistrationException {

		/*
		 * To check if username or email already exists
		 */
		User userExists = service.findByEmail(user.getEmail());
		User usernameCheck = registration.findByUsername(user.getUsername());

		if (userExists != null) {
			result.rejectValue("email", "error.user", Constants.EMAILID_EXISTS);
			model.addObject(user);
			model.setViewName(Constants.REGISTER);
		}
		if (usernameCheck != null) {
			result.rejectValue("username", "error.user", Constants.USER_EXISTS);
		}
		if (!file.isEmpty()) {
			try {

				byte[] bytes = file.getBytes();
				Path path = Paths.get(UPLOADED_FOLDER + file.getOriginalFilename());
				Files.write(path, bytes);
			} catch (Exception e) {
				user.setMessage(Constants.FILE_UPLOAD_FAILED);
				model.setViewName(Constants.REGISTER);
			}
		} else {
			user.setMessage(Constants.NO_FILE_UPLOADED);
			model.addObject(user);
			model.setViewName(Constants.REGISTER);
		}

		if (result.hasErrors()) {
			model.setViewName(Constants.REGISTER);
		} else {
			user.setFileLocation(UPLOADED_FOLDER + "\\" + file.getOriginalFilename());
			registration.save(user);
			user.setMessage(Constants.USER_CREATED);
			model.setViewName(Constants.WELCOME);
		}

		return model;
	}

	@GetMapping("/login")
	public String loginPage(Model model, Login login) {
		model.addAttribute(Constants.LOGIN, new Login());
		return Constants.LOGIN;
	}

	@PostMapping("/login")
	public String login(@ModelAttribute(value = "login") Login login, Model model, BindingResult result) {
		User user = registration.findByUsername(login.getUsername());
		if ((user == null) || !(login.getPassword().equals(user.getPassword()))) {
			result.rejectValue("username", "error.user", Constants.INVALID_USERNAME);
			model.addAttribute(Constants.LOGIN);
			return Constants.LOGIN;
		}
		user.setMessage(Constants.LOGIN_SUCCESSFULL);
		model.addAttribute(user);
		return Constants.WELCOME;
	}

	@GetMapping("/edit/{userId}")
	public String saveUser(@PathVariable("userId") long userId, Model model, @ModelAttribute User user) {
		try {
			user = registration.findById(userId).get();
		} catch (Exception e) {
			Login login = new Login();
			login.setMessage(Constants.INVALID_USERNAME);
			model.addAttribute(Constants.LOGIN, login);
			return Constants.LOGIN;
		}
		password = user.getPassword();
		model.addAttribute(user);
		return Constants.EDIT;
	}

	@PostMapping("/edit/{userId}")
	public String updateUser(@PathVariable("userId") long userId, @Valid @ModelAttribute User user,
			BindingResult result, Model model) {
		if (result.hasErrors()) {
			result.rejectValue("username", "error.user", Constants.INVALID_USERNAME);
			user.setUserId(userId);
			return Constants.EDIT;
		}
		user.setPassword(password);
		user.setMessage(Constants.USER_UPDATED);
		registration.save(user);
		return Constants.WELCOME;
	}

	@RequestMapping("/welcome/{userId}")
	public String welcome(@PathVariable long userId, @ModelAttribute User user, Model model) {
		user = registration.findById(userId).get();
		model.addAttribute(user);
		user.setMessage(Constants.NO_DETAILS_UPDATED);
		return Constants.WELCOME;
	}

	@GetMapping(value = "/showResume")
	public ResponseEntity<byte[]> downloadFile() {
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.parseMediaType("application/pdf"));
		String filename = UPLOADED_FOLDER + "\\Training_OOPS.pdf";
		headers.add("content-disposition", "inline;filename=" + filename);
		filename = filename.substring(filename.lastIndexOf("\\") + 1);
		headers.setContentDispositionFormData(filename, filename);
		headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");
		ResponseEntity<byte[]> response = new ResponseEntity<byte[]>(headers, HttpStatus.OK);
		return response;
	}

	@GetMapping("/resetPassword")
	public String resetPassword(Model model, @ModelAttribute Login login) {
		model.addAttribute(login);
		return Constants.PASSWORD_RESET;
	}

	@PostMapping("/resetPassword")
	public String updatePassword(@RequestParam("username") String username,
			@ModelAttribute(value = "login") Login login, BindingResult result, Model model) {
		User user = registration.findByUsername(username);
		if (user == null) {
			result.rejectValue("username", "error.user", Constants.INVALID_USERNAME);
			return Constants.PASSWORD_RESET;
		} else if (!(login.getPassword().equals(login.getReEnterPassword()))) {
			result.rejectValue("password", "error.user", Constants.PASSWORD_MISMATCH);
			return Constants.PASSWORD_RESET;
		} else {
			user.setPassword(login.getPassword());
		}
		registration.save(user);
		login.setMessage(Constants.PASSWORD_UPDATED);
		return Constants.HOME;
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
