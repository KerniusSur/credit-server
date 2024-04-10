package com.financehw.kernius.client.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "client")
public class ClientEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String name;
  private String lastName;
  private String email;
  private String phoneNumber;
  private String personalId;
  private String password;

  @Temporal(TemporalType.TIMESTAMP)
  private LocalDateTime createdAt;

  @PrePersist
  private void onCreate() {
    this.createdAt = LocalDateTime.now();
  }

  public Long getId() {
    return id;
  }

  public ClientEntity setId(Long id) {
    this.id = id;
    return this;
  }

  public String getName() {
    return name;
  }

  public ClientEntity setName(String name) {
    this.name = name;
    return this;
  }

  public String getLastName() {
    return lastName;
  }

  public ClientEntity setLastName(String lastName) {
    this.lastName = lastName;
    return this;
  }

  public String getEmail() {
    return email;
  }

  public ClientEntity setEmail(String email) {
    this.email = email;
    return this;
  }

  public String getPhoneNumber() {
    return phoneNumber;
  }

  public ClientEntity setPhoneNumber(String phoneNumber) {
    this.phoneNumber = phoneNumber;
    return this;
  }

  public String getPersonalId() {
    return personalId;
  }

  public ClientEntity setPersonalId(String personalId) {
    this.personalId = personalId;
    return this;
  }

  public String getPassword() {
    return password;
  }

  public ClientEntity setPassword(String password) {
    this.password = password;
    return this;
  }

  public LocalDateTime getCreatedAt() {
    return createdAt;
  }
}
