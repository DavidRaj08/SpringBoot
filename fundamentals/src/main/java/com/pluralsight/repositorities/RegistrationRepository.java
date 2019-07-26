package com.pluralsight.repositorities;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.pluralsight.entities.User;

@Repository
public interface RegistrationRepository extends CrudRepository<User, Long> {

	//User registerUser(User user);

	
}
