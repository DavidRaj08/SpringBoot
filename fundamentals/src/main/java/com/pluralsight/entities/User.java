package com.pluralsight.entities;

import java.util.Arrays;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Transient;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;

import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
public class User {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "user_id")
	private long userId;

	@NotEmpty(message = "*Please provide Username")
	private String username;

	
	private String password;

	@Column(name = "first_name")
	@NotEmpty(message = "*Please provide First Name")
	private String firstName;

	@Column(name = "last_name")
	@NotEmpty(message = "*Please provide Last Name")
	private String lastName;

	@Email(message = "*Please enter a valid Email")
	@NotEmpty(message = "*Please provide an Email")
	private String email;

	@Column(name = "phone_number")
	@NotEmpty(message = "*Please provide Phone Number")
	private String phoneNumber;

	@NotEmpty(message = "*Please provide experience")
	private String experience;

	@NotEmpty(message = "*Please provide notice period")
	@Column(name = "notice_period")
	private String noticePeriod;

	@Transient
	@JsonIgnore
	private MultipartFile[] file;

	private String fileLocation;

	@Transient
	private String message;

	public long getUserId() {
		return userId;
	}

	public void setUserId(long userId) {
		this.userId = userId;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public String getExperience() {
		return experience;
	}

	public void setExperience(String experience) {
		this.experience = experience;
	}

	public String getNoticePeriod() {
		return noticePeriod;
	}

	public void setNoticePeriod(String noticePeriod) {
		this.noticePeriod = noticePeriod;
	}

	public MultipartFile[] getFile() {
		return file;
	}

	public void setFile(MultipartFile[] file) {
		this.file = file;
	}

	public String getFileLocation() {
		return fileLocation;
	}

	public void setFileLocation(String fileLocation) {
		this.fileLocation = fileLocation;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	@Override
	public String toString() {
		return "User [userId=" + userId + ", username=" + username + ", password=" + password + ", firstName="
				+ firstName + ", lastName=" + lastName + ", email=" + email + ", phoneNumber=" + phoneNumber
				+ ", experience=" + experience + ", noticePeriod=" + noticePeriod + ", file=" + Arrays.toString(file)
				+ ", fileLocation=" + fileLocation + ", message=" + message + "]";
	}

}
