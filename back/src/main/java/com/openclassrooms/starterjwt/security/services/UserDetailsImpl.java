package com.openclassrooms.starterjwt.security.services;

import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;

import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Builder
@AllArgsConstructor
@Getter
@Setter
public class UserDetailsImpl implements UserDetails {
  private static final long serialVersionUID = 1L;
  @Getter
  @Setter
  private Long id;

  private String username;

  private String firstName;

  private String lastName;

  private Boolean admin;

  @JsonIgnore
  private String password;

//  public Boolean isAdmin() {
//    return admin;
//  }

  public UserDetailsImpl(Long id, String username, String firstName, String lastName, Boolean isAdmin) {
    this.id = id;
    this.username = username;
    this.firstName = firstName;
    this.lastName = lastName;
    this.admin = isAdmin;
  }

//  public UserDetailsImpl() {
//
//  }


  public Collection<? extends GrantedAuthority> getAuthorities() {
      return new HashSet<GrantedAuthority>();
  }

  @Override
  public boolean isAccountNonExpired() {
    return true;
  }

  @Override
  public boolean isAccountNonLocked() {
    return true;
  }

  @Override
  public boolean isCredentialsNonExpired() {
    return true;
  }

  @Override
  public boolean isEnabled() {
    return true;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o)
      return true;
    if (o == null || getClass() != o.getClass())
      return false;
    UserDetailsImpl user = (UserDetailsImpl) o;
    return Objects.equals(id, user.id);
  } 
}
