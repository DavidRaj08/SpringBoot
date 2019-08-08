package com.pluralsight.controller;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

/**
 * @author dsamband
 *
 */
@Controller
public class RegistrationController {

	private static final Logger logger = LoggerFactory.getLogger(RegistrationController.class);

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

	/**
	 * @param model
	 * @param user
	 * @return
	 */
	@GetMapping("/register")
	public String registerUser(Model model, User user) {
		model.addAttribute(Constants.REGISTER, new User());
		return Constants.REGISTER;
	}

	/**
	 * @param user
	 * @param file
	 * @param result
	 * @param model
	 * @return
	 * @throws RegistrationException
	 */
	@PostMapping("/register")
	public ModelAndView welcomeUser(@Valid @ModelAttribute(value = "user") User user,
			@RequestParam("file") MultipartFile file, BindingResult result, ModelAndView model)
			throws RegistrationException {
		logger.info(Constants.ENTRY  + getMethodName());

		/*
		 * To check if username or email already exists
		 */
		logger.info(Constants.VERIFYING_EMAIL_ID + getMethodName());
		User userExists = service.findByEmail(user.getEmail());
		if (userExists != null) {
			logger.error(Constants.EMAIL_ID_EXISTS + " - "  + getMethodName());
			result.rejectValue("email", "error.user", Constants.EMAIL_ID_EXISTS);
			model.addObject(user);
			model.setViewName(Constants.REGISTER);
		}

		logger.info(Constants.VERIFYING_USERNAME  + getMethodName());
		User usernameCheck = registration.findByUsername(user.getUsername());
		if (usernameCheck != null) {
			logger.error(Constants.USER_EXISTS + " - "  + getMethodName());
			result.rejectValue("username", "error.user", Constants.USER_EXISTS);
		}
		if (!file.isEmpty()) {
			try {
				logger.info(Constants.IS_FILE_EMPTY  + getMethodName());
				byte[] bytes = file.getBytes();
				Path path = Paths.get(UPLOADED_FOLDER + file.getOriginalFilename());
				Files.write(path, bytes);
			} catch (Exception e) {
				logger.error(Constants.FILE_UPLOAD_FAILED + " - "  + getMethodName());
				user.setMessage(Constants.FILE_UPLOAD_FAILED);
				model.setViewName(Constants.REGISTER);
			}
		} else {
			logger.error(Constants.NO_FILE_UPLOADED + " - "  + getMethodName());
			user.setMessage(Constants.NO_FILE_UPLOADED);
			model.addObject(user);
			model.setViewName(Constants.REGISTER);
		}

		logger.info(Constants.VALIDATING_FORM  + getMethodName());
		if (result.hasErrors()) {
			logger.error(Constants.FORM_ERROR  + getMethodName());
			model.setViewName(Constants.REGISTER);
		} else {
			user.setFileLocation(UPLOADED_FOLDER + "\\" + file.getOriginalFilename());
			registration.save(user);
			user.setMessage(Constants.USER_CREATED);
			model.setViewName(Constants.WELCOME);
			logger.debug(Constants.USER_CREATED  + getMethodName());
		}
		logger.info(Constants.EXIT  + getMethodName());
		return model;
	}

	/**
	 * @param model
	 * @param login
	 * @return
	 */
	@GetMapping("/login")
	public String loginPage(Model model, Login login) {
		model.addAttribute(Constants.LOGIN, new Login());
		return Constants.LOGIN;
	}

	/**
	 * @param login
	 * @param model
	 * @param result
	 * @return
	 */
	@PostMapping("/login")
	public String login(@ModelAttribute(value = "login") Login login, Model model, BindingResult result) {
		logger.info(Constants.ENTRY  + getMethodName());

		logger.info(Constants.VERIFYING_USERNAME + getMethodName());
		User user = registration.findByUsername(login.getUsername());
		if ((user == null) || !(login.getPassword().equals(user.getPassword()))) {
			logger.error(Constants.INVALID_USERNAME  + getMethodName());
			result.rejectValue("username", "error.user", Constants.INVALID_USERNAME);
			model.addAttribute(Constants.LOGIN);
			return Constants.LOGIN;
		}
		user.setMessage(Constants.LOGIN_SUCCESSFULL);
		model.addAttribute(user);
		logger.info(Constants.LOGIN_SUCCESSFULL  + getMethodName());

		logger.info(Constants.EXIT  + getMethodName());
		return Constants.WELCOME;
	}

	/**
	 * @param userId
	 * @param model
	 * @param user
	 * @return
	 */
	@GetMapping("/edit/{userId}")
	public String updateUser(@PathVariable("userId") long userId, Model model, @ModelAttribute User user) {
		try {
			logger.info(Constants.VERIFYING_EMAIL_ID  + getMethodName());
			user = registration.findById(userId).get();
		} catch (Exception e) {
			logger.error(Constants.USER_ID_DOESNOT_EXISTS  + getMethodName());
			Login login = new Login();
			login.setMessage(Constants.INVALID_USERNAME);
			model.addAttribute(Constants.LOGIN, login);
			return Constants.LOGIN;
		}
		password = user.getPassword();
		model.addAttribute(user);
		return Constants.EDIT;
	}

	/**
	 * @param userId
	 * @param user
	 * @param result
	 * @param model
	 * @return
	 */
	@PostMapping("/edit/{userId}")
	public String saveUser(@PathVariable("userId") long userId, @Valid @ModelAttribute User user, BindingResult result,
			Model model) {
		logger.info(Constants.ENTRY  + getMethodName());

		logger.info(Constants.VALIDATING_FORM  + getMethodName());
		if (result.hasErrors()) {
			logger.error(Constants.FORM_ERROR  + getMethodName());
			result.rejectValue("username", "error.user", Constants.INVALID_USERNAME);
			user.setUserId(userId);
			return Constants.EDIT;
		}
		user.setPassword(password);
		user.setMessage(Constants.USER_UPDATED);
		registration.save(user);
		logger.debug(Constants.USER_UPDATED  + getMethodName());

		logger.info(Constants.EXIT  + getMethodName());
		return Constants.WELCOME;
	}

	/**
	 * @param userId
	 * @param user
	 * @param model
	 * @return
	 */
	@RequestMapping("/welcome/{userId}")
	public String welcome(@PathVariable long userId, @ModelAttribute User user, Model model) {
		user = registration.findById(userId).get();
		model.addAttribute(user);
		user.setMessage(Constants.NO_DETAILS_UPDATED);
		return Constants.WELCOME;
	}

	/**
	 * @return
	 */
	@GetMapping(value = "/showResume")
	public ResponseEntity<byte[]> downloadFile() {
		logger.info(Constants.ENTRY  + getMethodName());
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.parseMediaType("application/pdf"));
		String filename = UPLOADED_FOLDER + "\\Training_OOPS.pdf";
		headers.add("content-disposition", "inline;filename=" + filename);
		filename = filename.substring(filename.lastIndexOf("\\") + 1);
		headers.setContentDispositionFormData(filename, filename);
		headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");
		ResponseEntity<byte[]> response = new ResponseEntity<byte[]>(headers, HttpStatus.OK);
		logger.info(Constants.EXIT  + getMethodName());
		return response;
	}

	/**
	 * @param model
	 * @param login
	 * @return
	 */
	@GetMapping("/resetPassword")
	public String resetPassword(Model model, @ModelAttribute Login login) {
		model.addAttribute(login);
		return Constants.PASSWORD_RESET;
	}

	/**
	 * @param username
	 * @param login
	 * @param result
	 * @param model
	 * @return
	 */
	@PostMapping("/resetPassword")
	public String updatePassword(@RequestParam("username") String username,
			@ModelAttribute(value = "login") Login login, BindingResult result, Model model) {
		logger.info(Constants.ENTRY  + getMethodName());

		logger.info(Constants.VERIFYING_USERNAME  + getMethodName());
		User user = registration.findByUsername(username);
		if (user == null) {
			logger.error(Constants.INVALID_USERNAME  + getMethodName());
			result.rejectValue("username", "error.user", Constants.INVALID_USERNAME);
			return Constants.PASSWORD_RESET;
		} else if (!(login.getPassword().equals(login.getReEnterPassword()))) {
			logger.error(Constants.PASSWORD_MISMATCH  + getMethodName());
			result.rejectValue("password", "error.user", Constants.PASSWORD_MISMATCH);
			return Constants.PASSWORD_RESET;
		} else {
			user.setPassword(login.getPassword());
		}
		registration.save(user);
		login.setMessage(Constants.PASSWORD_UPDATED);
		logger.info(Constants.EXIT  + getMethodName());
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

	public String getMethodName() {
		String method = this.getClass().getSimpleName() + "."
				+ Thread.currentThread().getStackTrace()[2].getMethodName() + "()";
		return method;
	}
}
