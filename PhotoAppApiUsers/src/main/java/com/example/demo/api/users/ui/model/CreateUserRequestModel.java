package com.example.demo.api.users.ui.model;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.springframework.stereotype.Component;

@Component
public class CreateUserRequestModel {

	@NotNull(message="First name can not be null")
	@Size(min = 2, message="First name must not be less than 2 characters")
	String firstName;

	@NotNull(message="Last name can not be null")
	@Size(min = 2, message="Last name must not be less than 2 characters")
	String lastName;
	
	@NotNull(message="Password can not be null")
	@Size(min=8, max=16, message="Password length should be bwtween 8 to 16")
	String password;
	
	@NotNull(message="Email can not be null")
	@Email
	String email;
	
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
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	@Override
	public String toString() {
		return "CreateUserRequestModel [firstName=" + firstName + ", lastName=" + lastName + ", password=" + password
				+ ", email=" + email + "]";
	}
	
}
