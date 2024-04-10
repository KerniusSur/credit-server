package com.financehw.kernius.loanextension.entity;

import com.financehw.kernius.loan.entity.LoanEntity;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "loan_extension")
public class LoanExtensionEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private Integer additionalLoanTermInDays;

  @Temporal(TemporalType.TIMESTAMP)
  private LocalDateTime createdAt;

  @ManyToOne(fetch = FetchType.LAZY)
  private LoanEntity loan;

  @PrePersist
  private void onCreate() {
    this.createdAt = LocalDateTime.now();
  }

  public Long getId() {
    return id;
  }

  public LoanExtensionEntity setId(Long id) {
    this.id = id;
    return this;
  }

  public Integer getAdditionalLoanTermInDays() {
    return additionalLoanTermInDays;
  }

  public LoanExtensionEntity setAdditionalLoanTermInDays(Integer additionLoanTermInDays) {
    this.additionalLoanTermInDays = additionLoanTermInDays;
    return this;
  }

  public LocalDateTime getCreatedAt() {
    return createdAt;
  }

  public LoanExtensionEntity setCreatedAt(LocalDateTime createdAt) {
    this.createdAt = createdAt;
    return this;
  }

  public LoanEntity getLoan() {
    return loan;
  }

  public LoanExtensionEntity setLoan(LoanEntity loan) {
    this.loan = loan;
    return this;
  }
}
