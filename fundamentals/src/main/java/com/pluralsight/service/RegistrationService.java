package com.pluralsight.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pluralsight.entities.User;
import com.pluralsight.repositorities.RegistrationRepository;

@Service
@Transactional
public class RegistrationService {
	
	@Autowired
	RegistrationRepository registration;
	
	
	
	public User get(long id) {
        return registration.findById(id).get();
    }
}
