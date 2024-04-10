package com.financehw.kernius.auth.entity;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

public class AuthenticatedProfile implements Authentication {
  private Long id;
  private String email;
  private String personalId;
  private boolean authenticated;
  private List<String> authorityList = new ArrayList<>();

  public AuthenticatedProfile() {}

  @Override
  public Object getDetails() {
    return this.id;
  }

  @Override
  public Object getPrincipal() {
    return this.id;
  }

  @Override
  public Object getCredentials() {
    return null;
  }

  @Override
  public boolean isAuthenticated() {
    return this.authenticated;
  }

  @Override
  public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {
    this.authenticated = isAuthenticated;
  }

  public String getEmail() {
    return email;
  }

  public AuthenticatedProfile setEmail(String email) {
    this.email = email;
    return this;
  }

  public String getPersonalId() {
    return personalId;
  }

  public AuthenticatedProfile setPersonalId(String personalId) {
    this.personalId = personalId;
    return this;
  }

  @Override
  public String getName() {
    return null;
  }

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return authorityList.stream().map((SimpleGrantedAuthority::new)).collect(Collectors.toList());
  }

  public Long getId() {
    return id;
  }

  public AuthenticatedProfile setId(Long userId) {
    this.id = userId;
    return this;
  }

  public AuthenticatedProfile setAuthorityList(List<String> authorityList) {
    this.authorityList = authorityList;
    return this;
  }
}
