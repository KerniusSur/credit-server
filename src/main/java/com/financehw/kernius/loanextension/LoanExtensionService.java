package com.financehw.kernius.loanextension;

import com.financehw.kernius.exception.ApiException;
import com.financehw.kernius.loan.LoanService;
import com.financehw.kernius.loan.dto.request.LoanUpdateRequest;
import com.financehw.kernius.loan.entity.LoanEntity;
import com.financehw.kernius.loanextension.dto.request.LoanExtensionRequest;
import com.financehw.kernius.loanextension.entity.LoanExtensionEntity;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class LoanExtensionService {
  private static final String ERROR_PREFIX = "err.loanExtension.";
  private final LoanExtensionRepository loanExtensionRepository;
  private final LoanService loanService;

  @Value("${app.loanExtensionMultiplier}")
  private BigDecimal loanExtensionMultiplier;

  @Value("${app.defaultLoanExtensionTermInDays}")
  private Integer defaultLoanExtensionTermInDays;

  public LoanExtensionService(
      LoanExtensionRepository loanExtensionRepository, LoanService loanService) {
    this.loanExtensionRepository = loanExtensionRepository;
    this.loanService = loanService;
  }

  public List<LoanExtensionEntity> findAllByClientId(Long clientId) {
    return loanExtensionRepository.findAllByLoanClientId(clientId);
  }

  public LoanExtensionEntity extendLoan(LoanExtensionRequest request, Long clientId) {
    LoanExtensionEntity loanExtension =
        new LoanExtensionEntity()
            .setLoan(new LoanEntity().setId(request.getLoanId()))
            .setAdditionalLoanTermInDays(defaultLoanExtensionTermInDays);

    LoanEntity loan = loanService.findById(request.getLoanId());
    if (!loan.getClient().getId().equals(clientId)) {
      throw ApiException.unauthorized(ERROR_PREFIX + "forbidden");
    }

    BigDecimal newInterestRate =
        loan.getInterestRate().multiply(loanExtensionMultiplier).setScale(4, RoundingMode.HALF_UP);

    BigDecimal newInterestAmount =
        loan.getAmount().multiply(newInterestRate).setScale(2, RoundingMode.HALF_UP);

    LoanUpdateRequest loanUpdateRequest =
        new LoanUpdateRequest()
            .setId(loan.getId())
            .setInterestAmount(newInterestAmount)
            .setInterestRate(newInterestRate)
            .setDueDate(loan.getDueDate().plusDays(defaultLoanExtensionTermInDays));

    loanService.updateLoan(loanUpdateRequest);
    return loanExtensionRepository.save(loanExtension);
  }
}
