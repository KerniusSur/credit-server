package com.financehw.kernius.acceptance;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.financehw.kernius.auth.entity.AuthenticatedProfile;
import com.financehw.kernius.client.entity.ClientEntity;
import com.financehw.kernius.loan.LoanService;
import com.financehw.kernius.loan.entity.LoanEntity;
import com.financehw.kernius.loanextension.LoanExtensionController;
import com.financehw.kernius.loanextension.dto.request.LoanExtensionRequest;
import com.financehw.kernius.loanextension.dto.response.LoanExtensionResponse;
import com.financehw.kernius.loanextension.mapper.LoanExtensionMapper;
import com.financehw.kernius.utils.IntegrationTest;
import com.financehw.kernius.utils.ListUtil;
import com.financehw.kernius.utils.TestDataFactory;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.transaction.annotation.Transactional;

public class LoanExtensionAcceptanceTests extends IntegrationTest {
  private static TestDataFactory dataFactory;
  private static AuthenticatedProfile profile;

  @Autowired private LoanExtensionController loanExtensionController;
  @Autowired private LoanService loanService;

  @Value("${app.loanExtensionMultiplier}")
  private BigDecimal loanExtensionMultiplier;

  @Value("${app.defaultLoanExtensionTermInDays}")
  private Integer defaultLoanExtensionTermInDays;

  @BeforeAll
  static void init() {
    dataFactory = new TestDataFactory();
    ClientEntity client = dataFactory.getValidClient();
    profile = dataFactory.getAuthenticatedProfile(client);
  }

  @Test
  public void findAllClientLoanExtensions_success() {
    List<LoanExtensionResponse> expected =
        ListUtil.map(dataFactory.getLoanExtensionList(), LoanExtensionMapper::toResponse);

    List<LoanExtensionResponse> actual =
        loanExtensionController.findAllClientLoanExtensions(profile);

    assertEquals(expected.size(), actual.size());
    for (int i = 0; i < expected.size(); i++) {
      assertEquals(expected.get(i), actual.get(i));
    }
  }

  @Test
  @Transactional
  public void extendLoan_success() {
    Long loanId = 6L;
    LoanExtensionRequest request = dataFactory.getValidLoanExtensionRequest(loanId);
    LoanExtensionResponse expected =
        LoanExtensionMapper.toResponse(
            dataFactory
                .getValidLoanExtensionEntity()
                .setId(4L)
                .setLoan(new LoanEntity().setId(loanId)));

    LoanEntity initialLoan = loanService.findById(loanId);
    LocalDate expectedDueDate = initialLoan.getDueDate().plusDays(defaultLoanExtensionTermInDays);
    BigDecimal expectedInterestRate =
        initialLoan
            .getInterestRate()
            .multiply(loanExtensionMultiplier)
            .setScale(4, RoundingMode.HALF_UP);
    BigDecimal expectedInterestAmount =
        initialLoan.getAmount().multiply(expectedInterestRate).setScale(2, RoundingMode.HALF_UP);

    LoanExtensionResponse actual = loanExtensionController.extendLoan(request, profile);
    expected.setCreatedAt(actual.getCreatedAt());
    assertEquals(expected, actual);

    LoanEntity extendedLoan = loanService.findById(loanId);

    assertEquals(expectedDueDate, extendedLoan.getDueDate());
    assertEquals(expectedInterestAmount, extendedLoan.getInterestAmount());
    assertEquals(expectedInterestRate, extendedLoan.getInterestRate());
  }
}
