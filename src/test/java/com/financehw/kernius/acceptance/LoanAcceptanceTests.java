package com.financehw.kernius.acceptance;

import static org.junit.jupiter.api.Assertions.*;

import com.financehw.kernius.auth.entity.AuthenticatedProfile;
import com.financehw.kernius.client.entity.ClientEntity;
import com.financehw.kernius.exception.ApiException;
import com.financehw.kernius.loan.LoanController;
import com.financehw.kernius.loan.LoanService;
import com.financehw.kernius.loan.dto.request.LoanApplicationRequest;
import com.financehw.kernius.loan.dto.response.LoanResponse;
import com.financehw.kernius.loan.entity.LoanEntity;
import com.financehw.kernius.loan.entity.LoanStatus;
import com.financehw.kernius.loan.mapper.LoanMapper;
import com.financehw.kernius.utils.IntegrationTest;
import com.financehw.kernius.utils.ListUtil;
import com.financehw.kernius.utils.TestDataFactory;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

public class LoanAcceptanceTests extends IntegrationTest {
  private static TestDataFactory dataFactory;
  private static AuthenticatedProfile profile;

  @Autowired private LoanController loanController;
  @Autowired private LoanService loanService;

  @Value("${app.maxLoanAmount}")
  private BigDecimal maxLoanAmount;

  @Value("${app.loanInterestRateMediumTerm}")
  private BigDecimal loanInterestRateMediumTerm;

  @Value("${app.maxLoanRequestsPerIpPerDay}")
  private Integer maxLoanRequestsPerIpPerDay;

  @BeforeAll
  static void init() {
    dataFactory = new TestDataFactory();
    ClientEntity client = dataFactory.getValidClient();
    profile = dataFactory.getAuthenticatedProfile(client);
  }

  @Test
  public void findAllClientLoans_success() {
    List<LoanResponse> expected =
        ListUtil.map(dataFactory.getUserLoanList(), LoanMapper::toResponse);

    List<LoanResponse> actual = loanController.findAllClientLoans(profile);

    assertEquals(expected.size(), actual.size());
    for (int i = 0; i < expected.size(); i++) {
      assertEquals(expected.get(i), actual.get(i));
    }
  }

  @Test
  public void findAllApprovedClientLoans_success() {
    List<LoanResponse> expected =
        ListUtil.map(dataFactory.getUserApprovedLoanList(), LoanMapper::toResponse);
    List<LoanResponse> actual = loanController.findAllClientApprovedLoans(profile);

    assertEquals(expected.size(), actual.size());
    for (int i = 0; i < expected.size(); i++) {
      assertEquals(expected.get(i), actual.get(i));
    }
  }

  @Test
  public void applyForLoan_success() {
    LoanApplicationRequest request = dataFactory.getValidLoanApplicationRequest();
    LoanResponse expected = dataFactory.getValidLoanResponse();
    LoanResponse actual = loanController.applyForLoan(request, profile);
    expected.setCreatedAt(actual.getCreatedAt());

    assertEquals(expected, actual);
    assertEquals(LoanStatus.APPROVED, actual.getStatus());
    assertEquals(
        loanInterestRateMediumTerm.setScale(4, RoundingMode.HALF_UP), actual.getInterestRate());
    assertEquals(expected.getInterestAmount(), actual.getInterestAmount());
    assertEquals(expected.getDueDate(), actual.getDueDate());
  }

  @Test
  public void applyForLoan_fail_maxAmountRiskHours() {
    LoanApplicationRequest request = dataFactory.getValidLoanApplicationRequest();
    LocalDateTime riskHour = LocalDateTime.of(2024, 3, 22, 3, 0);
    String expectedMessage =
        "Sorry, we are not able to provide loans for "
            + maxLoanAmount
            + " between the hours of 00:00 and 6:00. Please try again later. Local Time="
            + riskHour.toLocalTime();
    request.setAmount(maxLoanAmount);
    request.setApplicationDate(riskHour);

    ApiException actual =
        assertThrows(ApiException.class, () -> loanController.applyForLoan(request, profile));

    Long loanId = 7L;
    LoanEntity createdEntity = loanService.findById(loanId);

    assertEquals(expectedMessage, actual.getMessage());
    assertEquals(request.getAmount().setScale(2, RoundingMode.HALF_UP), createdEntity.getAmount());
    assertEquals(request.getLoanTermInMonths() * 30, createdEntity.getLoanTermInDays());
    assertEquals(LoanStatus.REJECTED, createdEntity.getStatus());
  }

  @Test
  public void applyForLoan_fail_maxAmountOfApplicationsPerDayPerRequest() {
    LoanApplicationRequest request = dataFactory.getValidLoanApplicationRequest();
    request.setApplicationDate(LocalDateTime.now().withHour(12).withMinute(0));
    String expectedMessage =
        "Sorry, you have reached the maximum number of loan requests per day. Please try again tomorrow. IP Address="
            + request.getIpAddress()
            + ", Max Requests="
            + maxLoanRequestsPerIpPerDay;
    Long lastId = 0L;

    for (int i = 0; i < 3; i++) {
      LoanResponse actualPassing = loanController.applyForLoan(request, profile);
      lastId = actualPassing.getId();
      assertEquals(LoanStatus.APPROVED, actualPassing.getStatus());
    }

    ApiException actual =
        assertThrows(ApiException.class, () -> loanController.applyForLoan(request, profile));
    assertEquals(expectedMessage, actual.getMessage());

    LoanEntity rejectedEntity = loanService.findById(lastId + 1);
    assertEquals(LoanStatus.REJECTED, rejectedEntity.getStatus());
  }
}
