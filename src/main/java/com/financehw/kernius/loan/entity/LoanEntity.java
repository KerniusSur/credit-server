package com.financehw.kernius.loan.entity;

import com.financehw.kernius.client.entity.ClientEntity;
import com.financehw.kernius.loanextension.entity.LoanExtensionEntity;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "loan")
public class LoanEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private BigDecimal amount;

  @Column(scale = 3, precision = 5)
  private BigDecimal interestRate;

  private BigDecimal interestAmount;
  private Integer loanTermInDays;
  private LocalDate dueDate;

  @Enumerated(EnumType.STRING)
  private LoanStatus status;

  @Temporal(TemporalType.TIMESTAMP)
  private LocalDateTime createdAt;

  private LocalDateTime updatedAt;
  private String ipAddress;

  @ManyToOne(fetch = FetchType.LAZY)
  private ClientEntity client;

  @OneToMany(
      mappedBy = "loan",
      cascade = CascadeType.ALL,
      orphanRemoval = true,
      fetch = FetchType.LAZY)
  private List<LoanExtensionEntity> loanExtensions = new ArrayList<>();

  @PrePersist
  private void onCreate() {
    this.createdAt = LocalDateTime.now();
    this.setDueDate(LocalDate.now().plusDays(loanTermInDays));
  }

  public Long getId() {
    return id;
  }

  public LoanEntity setId(Long id) {
    this.id = id;
    return this;
  }

  public BigDecimal getAmount() {
    return amount;
  }

  public LoanEntity setAmount(BigDecimal amount) {
    this.amount = amount;
    return this;
  }

  public BigDecimal getInterestRate() {
    return interestRate;
  }

  public LoanEntity setInterestRate(BigDecimal interestRate) {
    this.interestRate = interestRate;
    return this;
  }

  public BigDecimal getInterestAmount() {
    return interestAmount;
  }

  public LoanEntity setInterestAmount(BigDecimal interestAmount) {
    this.interestAmount = interestAmount;
    return this;
  }

  public Integer getLoanTermInDays() {
    return loanTermInDays;
  }

  public LoanEntity setLoanTermInDays(Integer loanTermInDays) {
    this.loanTermInDays = loanTermInDays;
    return this;
  }

  public LocalDate getDueDate() {
    return dueDate;
  }

  public LoanEntity setDueDate(LocalDate dueDate) {
    this.dueDate = dueDate;
    return this;
  }

  public LoanStatus getStatus() {
    return status;
  }

  public LoanEntity setStatus(LoanStatus status) {
    this.status = status;
    return this;
  }

  public LocalDateTime getCreatedAt() {
    return createdAt;
  }

  public LoanEntity setCreatedAt(LocalDateTime createdAt) {
    this.createdAt = createdAt;
    return this;
  }

  public LocalDateTime getUpdatedAt() {
    return updatedAt;
  }

  public LoanEntity setUpdatedAt(LocalDateTime updatedAt) {
    this.updatedAt = updatedAt;
    return this;
  }

  public String getIpAddress() {
    return ipAddress;
  }

  public LoanEntity setIpAddress(String ipAddress) {
    this.ipAddress = ipAddress;
    return this;
  }

  public ClientEntity getClient() {
    return client;
  }

  public LoanEntity setClient(ClientEntity client) {
    this.client = client;
    return this;
  }

  public List<LoanExtensionEntity> getLoanExtensions() {
    return loanExtensions;
  }

  public LoanEntity setLoanExtensions(List<LoanExtensionEntity> loanExtensions) {
    this.loanExtensions = loanExtensions;
    return this;
  }

  public LoanEntity addLoanExtension(LoanExtensionEntity loanExtension) {
    loanExtensions.add(loanExtension);
    loanExtension.setLoan(this);
    return this;
  }
}
