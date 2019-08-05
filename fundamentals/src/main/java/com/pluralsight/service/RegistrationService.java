package com.pluralsight.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pluralsight.entities.Login;
import com.pluralsight.entities.User;
import com.pluralsight.repositorities.RegistrationRepository;

@Service
@Transactional
public class RegistrationService /* implements UserDetailsService */ {

	@Autowired
	RegistrationRepository registration;

	public User get(long id) {
		return registration.findById(id).get();
	}

	/*
	 * @Override public UserDetails loadUserByUsername(String arg0) throws
	 * UsernameNotFoundException { return null; }
	 */

	public User login(Login loginDetails) {
		User user = registration.findByUsername(loginDetails.getUsername());
		if (user == null) {
			throw new RuntimeException("User does not exist.");
		}
		if (!user.getPassword().equals(loginDetails.getPassword())) {
			throw new RuntimeException("Password mismatch.");
		}
		
		return user;
	}

}
