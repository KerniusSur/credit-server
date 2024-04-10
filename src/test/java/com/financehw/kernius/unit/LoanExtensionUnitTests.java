package com.financehw.kernius.unit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.financehw.kernius.client.entity.ClientEntity;
import com.financehw.kernius.exception.ApiException;
import com.financehw.kernius.loan.LoanService;
import com.financehw.kernius.loan.dto.request.LoanUpdateRequest;
import com.financehw.kernius.loan.entity.LoanEntity;
import com.financehw.kernius.loanextension.LoanExtensionRepository;
import com.financehw.kernius.loanextension.LoanExtensionService;
import com.financehw.kernius.loanextension.dto.request.LoanExtensionRequest;
import com.financehw.kernius.loanextension.entity.LoanExtensionEntity;
import com.financehw.kernius.utils.TestDataFactory;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
public class LoanExtensionUnitTests {
  private static TestDataFactory dataFactory;
  private final BigDecimal loanExtensionMultiplier = BigDecimal.valueOf(1.5);
  private final Integer defaultLoanExtensionTermInDays = 7;

  @Mock private LoanExtensionRepository loanExtensionRepository;
  @Mock private LoanService loanService;
  @InjectMocks private LoanExtensionService loanExtensionService;

  @BeforeAll
  static void setup() {
    dataFactory = new TestDataFactory();
  }

  @BeforeEach
  void init() {
    ReflectionTestUtils.setField(
        loanExtensionService, "loanExtensionMultiplier", loanExtensionMultiplier);
    ReflectionTestUtils.setField(
        loanExtensionService, "defaultLoanExtensionTermInDays", defaultLoanExtensionTermInDays);
  }

  @Test
  void findAllByClientId_success() {
    ClientEntity client = dataFactory.getValidClient();
    LoanEntity loanEntity = dataFactory.getValidLoan();
    LoanExtensionEntity loanExtensionEntity = dataFactory.getValidLoanExtensionEntity();
    loanEntity.setLoanExtensions(List.of(loanExtensionEntity));

    when(loanExtensionRepository.findAllByLoanClientId(client.getId()))
        .thenReturn(List.of(loanExtensionEntity));

    List<LoanExtensionEntity> actualLoanExtensions =
        loanExtensionService.findAllByClientId(client.getId());

    assertEquals(loanEntity.getLoanExtensions(), actualLoanExtensions);
    assertEquals(loanEntity.getLoanExtensions().size(), actualLoanExtensions.size());
    verify(loanExtensionRepository).findAllByLoanClientId(client.getId());
  }

  @Test
  void extendLoan_success() {
    LoanEntity loanEntity = dataFactory.getValidLoan();
    Long loanId = loanEntity.getId();
    BigDecimal originalInterest = loanEntity.getInterestAmount();
    LocalDate originalDueDate = loanEntity.getDueDate();
    LoanExtensionRequest request = dataFactory.getValidLoanExtensionRequest(loanId);

    when(loanService.findById(loanId)).thenReturn(loanEntity);
    when(loanExtensionRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

    LoanExtensionEntity loanExtension =
        loanExtensionService.extendLoan(request, loanEntity.getClient().getId());

    assertEquals(loanExtension.getLoan().getId(), loanId);
    assertEquals(loanExtension.getAdditionalLoanTermInDays(), defaultLoanExtensionTermInDays);

    ArgumentCaptor<LoanUpdateRequest> loanUpdateRequestCaptor =
        ArgumentCaptor.forClass(LoanUpdateRequest.class);
    verify(loanService).updateLoan(loanUpdateRequestCaptor.capture());

    LoanUpdateRequest capturedRequest = loanUpdateRequestCaptor.getValue();

    assertEquals(loanId, capturedRequest.getId());
    assertEquals(
        originalInterest.multiply(loanExtensionMultiplier).setScale(2, RoundingMode.HALF_UP),
        capturedRequest.getInterestAmount());
    assertEquals(
        originalDueDate.plusDays(defaultLoanExtensionTermInDays), capturedRequest.getDueDate());
  }

  @Test
  void extendLoan_unauthorized() {
    LoanEntity loanEntity = dataFactory.getValidLoan();
    Long loanId = loanEntity.getId();
    LoanExtensionRequest request = dataFactory.getValidLoanExtensionRequest(loanId);
    String expectedMessage = "err.loanExtension.forbidden";
    Long wrongClientId = 999L;

    when(loanService.findById(loanId)).thenReturn(loanEntity);

    ApiException exception =
        assertThrows(
            ApiException.class, () -> loanExtensionService.extendLoan(request, wrongClientId));

    assertEquals(expectedMessage, exception.getMessage());
  }
}
