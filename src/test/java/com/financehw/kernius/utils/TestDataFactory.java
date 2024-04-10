package com.financehw.kernius.utils;

import com.financehw.kernius.auth.dto.request.EmailPasswordLoginRequest;
import com.financehw.kernius.auth.dto.request.PersonalIdPasswordLoginRequest;
import com.financehw.kernius.auth.dto.request.RegisterRequest;
import com.financehw.kernius.auth.entity.AuthenticatedProfile;
import com.financehw.kernius.client.dto.request.ClientCreateRequest;
import com.financehw.kernius.client.entity.ClientEntity;
import com.financehw.kernius.loan.dto.request.LoanApplicationRequest;
import com.financehw.kernius.loan.dto.request.LoanUpdateRequest;
import com.financehw.kernius.loan.dto.response.LoanResponse;
import com.financehw.kernius.loan.entity.LoanEntity;
import com.financehw.kernius.loan.entity.LoanStatus;
import com.financehw.kernius.loanextension.dto.request.LoanExtensionRequest;
import com.financehw.kernius.loanextension.entity.LoanExtensionEntity;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class TestDataFactory {
  public AuthenticatedProfile getAuthenticatedProfile(ClientEntity client) {
    return new AuthenticatedProfile().setId(client.getId());
  }

  public String getDummyJwtToken() {
    return "dummy_auth_token";
  }

  public ClientEntity getValidClient() {
    return new ClientEntity()
        .setId(1L)
        .setName("John")
        .setLastName("Doe")
        .setEmail("john.doe@email.com")
        .setPhoneNumber("+37060123456")
        .setPersonalId("50001010000")
        .setPassword("password");
  }

  public LoanEntity getValidLoan() {
    ClientEntity client = getValidClient();
    return new LoanEntity()
        .setId(1L)
        .setAmount(BigDecimal.valueOf(1000).setScale(2, RoundingMode.HALF_UP))
        .setInterestRate(BigDecimal.valueOf(0.129).setScale(4, RoundingMode.HALF_UP))
        .setInterestAmount(BigDecimal.valueOf(129).setScale(2, RoundingMode.HALF_UP))
        .setLoanTermInDays(30)
        .setStatus(LoanStatus.APPROVED)
        .setClient(client)
        .setDueDate(LocalDate.of(2024, 4, 23))
        .setIpAddress("192.168.0.1")
        .setCreatedAt(LocalDateTime.of(2024, 3, 22, 12, 0))
        .setLoanExtensions(List.of(getValidLoanExtensionEntity()));
  }

  public List<LoanEntity> getUserLoanList() {
    ClientEntity client = getValidClient();
    List<LoanExtensionEntity> loanExtensions = new ArrayList<>(getLoanExtensionList());
    loanExtensions.remove(0);

    return List.of(
        getValidLoan(),
        new LoanEntity()
            .setId(2L)
            .setAmount(BigDecimal.valueOf(2000).setScale(2, RoundingMode.HALF_UP))
            .setInterestRate(BigDecimal.valueOf(0.114).setScale(4, RoundingMode.HALF_UP))
            .setInterestAmount(BigDecimal.valueOf(228).setScale(2, RoundingMode.HALF_UP))
            .setLoanTermInDays(540)
            .setStatus(LoanStatus.APPROVED)
            .setClient(client)
            .setDueDate(LocalDate.of(2025, 9, 15))
            .setCreatedAt(LocalDateTime.of(2024, 3, 22, 12, 0))
            .setIpAddress("192.168.0.1")
            .setLoanExtensions(loanExtensions),
        new LoanEntity()
            .setId(3L)
            .setAmount(BigDecimal.valueOf(3000).setScale(2, RoundingMode.HALF_UP))
            .setInterestRate(BigDecimal.valueOf(0.099).setScale(4, RoundingMode.HALF_UP))
            .setInterestAmount(BigDecimal.valueOf(297).setScale(2, RoundingMode.HALF_UP))
            .setLoanTermInDays(1080)
            .setStatus(LoanStatus.APPROVED)
            .setClient(client)
            .setDueDate(LocalDate.of(2027, 3, 9))
            .setCreatedAt(LocalDateTime.of(2024, 3, 22, 12, 0))
            .setIpAddress("192.168.0.1")
            .setLoanExtensions(List.of()),
        new LoanEntity()
            .setId(6L)
            .setAmount(BigDecimal.valueOf(15000).setScale(2, RoundingMode.HALF_UP))
            .setInterestRate(BigDecimal.valueOf(0.099).setScale(4, RoundingMode.HALF_UP))
            .setInterestAmount(BigDecimal.valueOf(1485).setScale(2, RoundingMode.HALF_UP))
            .setLoanTermInDays(1080)
            .setStatus(LoanStatus.REJECTED)
            .setClient(client)
            .setDueDate(LocalDate.of(2027, 3, 9))
            .setCreatedAt(LocalDateTime.of(2024, 3, 22, 12, 0))
            .setIpAddress("192.168.0.1")
            .setLoanExtensions(List.of()));
  }

  public List<LoanEntity> getUserApprovedLoanList() {
    List<LoanEntity> approvedList = new ArrayList<>(getUserLoanList());
    approvedList.remove(3);
    return approvedList;
  }

  public EmailPasswordLoginRequest getValidEmailPasswordLoginRequest(ClientEntity client) {
    return new EmailPasswordLoginRequest()
        .setEmail(client.getEmail())
        .setPassword(client.getPassword());
  }

  public PersonalIdPasswordLoginRequest getValidPersonalIdPasswordLoginRequest(
      ClientEntity client) {
    return new PersonalIdPasswordLoginRequest()
        .setPersonalId(client.getPersonalId())
        .setPassword(client.getPassword());
  }

  public RegisterRequest getValidRegisterRequest() {
    return new RegisterRequest()
        .setName("John")
        .setLastName("Doe")
        .setEmail("john.doe@email.com")
        .setPhoneNumber("+37060123456")
        .setPersonalId("50001010000")
        .setPassword("password");
  }

  public LoanEntity getValidLoanEntity(ClientEntity client) {
    return new LoanEntity()
        .setId(1L)
        .setAmount(BigDecimal.valueOf(1000))
        .setInterestRate(BigDecimal.valueOf(0.129))
        .setInterestAmount(BigDecimal.valueOf(129))
        .setLoanTermInDays(360)
        .setClient(client)
        .setIpAddress("127.0.0.1");
  }

  public LoanApplicationRequest getValidLoanApplicationRequest() {
    return new LoanApplicationRequest()
        .setAmount(BigDecimal.valueOf(1000).setScale(2, RoundingMode.HALF_UP))
        .setLoanTermInMonths(12)
        .setApplicationDate(LocalDateTime.of(2024, 3, 22, 12, 0))
        .setIpAddress("127.0.0.1");
  }

  public LoanUpdateRequest getValidLoanUpdateRequest() {
    return new LoanUpdateRequest()
        .setId(1L)
        .setStatus(LoanStatus.APPROVED)
        .setDueDate(LocalDate.of(2024, 4, 19))
        .setInterestAmount(BigDecimal.valueOf(151));
  }

  public LoanResponse getValidLoanResponse() {
    return new LoanResponse()
        .setId(7L)
        .setAmount(BigDecimal.valueOf(1000).setScale(2, RoundingMode.HALF_UP))
        .setInterestRate(BigDecimal.valueOf(0.114).setScale(4, RoundingMode.HALF_UP))
        .setInterestAmount(BigDecimal.valueOf(114).setScale(2, RoundingMode.HALF_UP))
        .setLoanTermInDays(360)
        .setDueDate(LocalDate.now().plusDays(360))
        .setStatus(LoanStatus.APPROVED)
        .setCreatedAt(LocalDateTime.now())
        .setIpAddress("127.0.0.1")
        .setLoanExtensions(List.of());
  }

  public LoanExtensionRequest getValidLoanExtensionRequest(Long loanId) {
    return new LoanExtensionRequest().setLoanId(loanId);
  }

  public LoanExtensionEntity getValidLoanExtensionEntity() {
    return new LoanExtensionEntity()
        .setId(1L)
        .setLoan(getValidLoanEntity(getValidClient()))
        .setAdditionalLoanTermInDays(7)
        .setCreatedAt(LocalDateTime.of(2024, 3, 22, 14, 0));
  }

  public List<LoanExtensionEntity> getLoanExtensionList() {
    return List.of(
        getValidLoanExtensionEntity(),
        new LoanExtensionEntity()
            .setId(2L)
            .setLoan(new LoanEntity().setId(2L))
            .setAdditionalLoanTermInDays(7)
            .setCreatedAt(LocalDateTime.of(2024, 3, 22, 14, 0)),
        new LoanExtensionEntity()
            .setId(3L)
            .setLoan(new LoanEntity().setId(2L))
            .setAdditionalLoanTermInDays(7)
            .setCreatedAt(LocalDateTime.of(2024, 3, 22, 14, 0)));
  }

  public ClientCreateRequest getValidClientCreateRequest() {
    return new ClientCreateRequest()
        .setName("John")
        .setLastName("Doe")
        .setEmail("john.doe@gmail.com")
        .setPhoneNumber("+37060123456")
        .setPersonalId("50001010000")
        .setPassword("password");
  }
}
