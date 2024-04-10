package com.financehw.kernius.unit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.financehw.kernius.client.entity.ClientEntity;
import com.financehw.kernius.exception.ApiException;
import com.financehw.kernius.loan.LoanRepository;
import com.financehw.kernius.loan.LoanService;
import com.financehw.kernius.loan.dto.request.LoanApplicationRequest;
import com.financehw.kernius.loan.dto.request.LoanUpdateRequest;
import com.financehw.kernius.loan.entity.LoanEntity;
import com.financehw.kernius.loan.entity.LoanStatus;
import com.financehw.kernius.utils.TestDataFactory;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
public class LoanUnitTests {
  private static TestDataFactory dataFactory;
  private final Integer maxLoanRequestsPerIpPerDay = 3;
  private final BigDecimal minLoanAmount = new BigDecimal(100);
  private final BigDecimal maxLoanAmount = new BigDecimal(15000);
  private final Integer minLoanTermInDays = 30;
  private final Integer maxLoanTermInDays = 1800;
  private final BigDecimal loanInterestRateShortTerm = new BigDecimal("0.129");
  private final BigDecimal loanInterestRateMediumTerm = new BigDecimal("0.114");
  private final BigDecimal loanInterestRateLongTerm = new BigDecimal("0.099");

  @Mock LoanRepository loanRepository;
  @InjectMocks LoanService loanService;

  @BeforeAll
  static void setup() {
    dataFactory = new TestDataFactory();
  }

  @BeforeEach
  void init() {
    ReflectionTestUtils.setField(loanService, "maxLoanAmount", maxLoanAmount);
    ReflectionTestUtils.setField(loanService, "minLoanAmount", minLoanAmount);
    ReflectionTestUtils.setField(loanService, "minLoanTermInDays", minLoanTermInDays);
    ReflectionTestUtils.setField(loanService, "maxLoanTermInDays", maxLoanTermInDays);
    ReflectionTestUtils.setField(
        loanService, "maxLoanRequestsPerIpPerDay", maxLoanRequestsPerIpPerDay);
    ReflectionTestUtils.setField(
        loanService, "loanInterestRateShortTerm", loanInterestRateShortTerm);
    ReflectionTestUtils.setField(
        loanService, "loanInterestRateMediumTerm", loanInterestRateMediumTerm);
    ReflectionTestUtils.setField(loanService, "loanInterestRateLongTerm", loanInterestRateLongTerm);
  }

  @Test
  void findById_success() {
    ClientEntity mockClient = dataFactory.getValidClient();
    LoanEntity mockLoanEntity = dataFactory.getValidLoanEntity(mockClient);

    when(loanRepository.findById(mockLoanEntity.getId())).thenReturn(Optional.of(mockLoanEntity));

    LoanEntity foundLoan = loanService.findById(mockLoanEntity.getId());

    assertEquals(mockLoanEntity, foundLoan);
    verify(loanRepository).findById(mockLoanEntity.getId());
  }

  @Test
  void findById_notFound() {
    Long loanId = 999L;
    String expectedMessage = "err.loan.notFound";
    when(loanRepository.findById(loanId)).thenReturn(Optional.empty());

    ApiException exception = assertThrows(ApiException.class, () -> loanService.findById(loanId));
    assertEquals(expectedMessage, exception.getMessage());
    verify(loanRepository).findById(loanId);
  }

  @Test
  void findAllClientLoansByClientId_success() {
    ClientEntity client = dataFactory.getValidClient();
    List<LoanEntity> expectedLoans = dataFactory.getUserLoanList();

    when(loanRepository.findAllByClientId(client.getId())).thenReturn(expectedLoans);

    List<LoanEntity> actualLoans = loanService.findAllClientLoansByClientId(client.getId());

    assertEquals(expectedLoans.size(), actualLoans.size());
    verify(loanRepository).findAllByClientId(client.getId());
  }

  @Test
  void findAllApprovedLoansByClientId_success() {
    ClientEntity client = dataFactory.getValidClient();
    List<LoanEntity> expectedLoans = dataFactory.getUserLoanList();

    when(loanRepository.findAllByClientIdAndStatus(client.getId(), LoanStatus.APPROVED))
        .thenReturn(expectedLoans);

    List<LoanEntity> actualLoans = loanService.findAllApprovedLoansByClientId(client.getId());

    assertEquals(expectedLoans.size(), actualLoans.size());
    verify(loanRepository).findAllByClientIdAndStatus(client.getId(), LoanStatus.APPROVED);
  }

  @Test
  void createLoan_success() {
    LoanApplicationRequest request = dataFactory.getValidLoanApplicationRequest();
    ClientEntity client = dataFactory.getValidClient();
    LoanEntity expectedSavedLoan = dataFactory.getValidLoanEntity(client);

    when(loanRepository.save(any())).thenReturn(expectedSavedLoan);

    LoanEntity actualSavedLoan = loanService.createLoan(request, client.getId());

    assertEquals(expectedSavedLoan, actualSavedLoan);
    assertEquals(expectedSavedLoan.getLoanTermInDays(), request.getLoanTermInMonths() * 30);

    verify(loanRepository).save(any());
    verify(loanRepository).countByIpAddressAndCreatedAtBetween(anyString(), any(), any());
  }

  @Test
  void update_loan_success() {
    LoanEntity loan = dataFactory.getValidLoan();
    LoanUpdateRequest request = dataFactory.getValidLoanUpdateRequest();
    LoanEntity updatedLoan =
        dataFactory
            .getValidLoan()
            .setInterestAmount(request.getInterestAmount())
            .setStatus(request.getStatus())
            .setDueDate(request.getDueDate());

    when(loanRepository.findById(loan.getId())).thenReturn(Optional.of(loan));
    when(loanRepository.save(loan)).thenReturn(updatedLoan);

    loanService.updateLoan(request);

    assertEquals(updatedLoan.getId(), loan.getId());
    assertEquals(updatedLoan.getInterestAmount(), loan.getInterestAmount());
    assertEquals(updatedLoan.getStatus(), loan.getStatus());
    assertEquals(updatedLoan.getDueDate(), loan.getDueDate());

    verify(loanRepository).save(loan);
  }

  @Test
  void createLoan_invalidAmount_tooLow() {
    BigDecimal invalidAmount = minLoanAmount.subtract(BigDecimal.ONE);
    LoanApplicationRequest request =
        dataFactory.getValidLoanApplicationRequest().setAmount(invalidAmount);

    assertThrows(ApiException.class, () -> loanService.createLoan(request, 1L));
    verify(loanRepository, never()).save(any());
  }

  @Test
  void createLoan_invalidAmount_tooHigh() {
    BigDecimal invalidAmount = maxLoanAmount.add(BigDecimal.ONE);
    LoanApplicationRequest request =
        dataFactory.getValidLoanApplicationRequest().setAmount(invalidAmount);
    String expectedMessage =
        "err.loan.invalidLoanAmount "
            + "Minimum Loan Amount="
            + minLoanAmount
            + ", Requested Amount="
            + invalidAmount
            + ", Maximum Loan Amount="
            + maxLoanAmount;

    ApiException exception =
        assertThrows(ApiException.class, () -> loanService.createLoan(request, 1L));

    assertEquals(expectedMessage, exception.getMessage());
    verify(loanRepository, never()).save(any());
  }

  @Test
  void createLoan_maxRequestsReached() {
    LoanApplicationRequest request = dataFactory.getValidLoanApplicationRequest();
    String expectedMessage =
        "Sorry, you have reached the maximum number of loan requests per day. Please try again tomorrow. "
            + "IP Address="
            + request.getIpAddress()
            + ", Max Requests="
            + maxLoanRequestsPerIpPerDay;

    when(loanRepository.countByIpAddressAndCreatedAtBetween(anyString(), any(), any()))
        .thenReturn(maxLoanRequestsPerIpPerDay);

    ApiException exception =
        assertThrows(ApiException.class, () -> loanService.createLoan(request, 1L));

    assertEquals(expectedMessage, exception.getMessage());
    verify(loanRepository).countByIpAddressAndCreatedAtBetween(anyString(), any(), any());
  }

  @Test
  void createLoan_riskyHours() {
    LoanApplicationRequest request =
        dataFactory
            .getValidLoanApplicationRequest()
            .setAmount(maxLoanAmount)
            .setApplicationDate(LocalDateTime.of(LocalDate.now(), LocalTime.of(3, 0)));

    String expectedMessage =
        "Sorry, we are not able to provide loans for "
            + maxLoanAmount
            + " between the hours of 00:00 and 6:00. Please try again later. "
            + "Local Time="
            + request.getApplicationDate().toLocalTime();

    when(loanRepository.countByIpAddressAndCreatedAtBetween(anyString(), any(), any()))
        .thenReturn(maxLoanRequestsPerIpPerDay - 1);

    ApiException exception =
        assertThrows(ApiException.class, () -> loanService.createLoan(request, 1L));
    assertEquals(expectedMessage, exception.getMessage());
    verify(loanRepository).countByIpAddressAndCreatedAtBetween(anyString(), any(), any());
    verify(loanRepository).save(any());
  }
}
