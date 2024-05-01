package com.openclassrooms.starterjwt.payload.request;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

@Setter
@Getter
public class LoginRequest {
	@NotBlank
  private String email;

	@NotBlank
	private String password;

}
//public class LoginRequest {
//	@NotBlank
//	private String email;
//
//	@NotBlank
//	private String password;
//
//	public String getEmail() {
//		return email;
//	}
//
//	public void setEmail(String email) {
//		this.email = email;
//	}
//
//	public String getPassword() {
//		return password;
//	}
//
//	public void setPassword(String password) {
//		this.password = password;
//	}
//}
