package com.financehw.kernius.loan;

import com.financehw.kernius.client.entity.ClientEntity;
import com.financehw.kernius.exception.ApiException;
import com.financehw.kernius.loan.dto.request.LoanApplicationRequest;
import com.financehw.kernius.loan.dto.request.LoanUpdateRequest;
import com.financehw.kernius.loan.entity.LoanEntity;
import com.financehw.kernius.loan.entity.LoanStatus;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class LoanService {
  private final LoanRepository loanRepository;
  private final String ERROR_PREFIX = "err.loan.";

  private final BigDecimal loanInterestRateShortTerm;
  private final BigDecimal loanInterestRateLongTerm;
  private final BigDecimal loanInterestRateMediumTerm;
  private final BigDecimal minLoanAmount;
  private final BigDecimal maxLoanAmount;
  private final Integer minLoanTermInDays;
  private final Integer maxLoanTermInDays;
  private final Integer maxLoanRequestsPerIpPerDay;

  public LoanService(
      LoanRepository loanRepository,
      @Value("${app.minLoanAmount}") BigDecimal minLoanAmount,
      @Value("${app.maxLoanAmount}") BigDecimal maxLoanAmount,
      @Value("${app.loanInterestRateShortTerm}") BigDecimal loanInterestRateShortTerm,
      @Value("${app.loanInterestRateLongTerm}") BigDecimal loanInterestRateLongTerm,
      @Value("${app.loanInterestRateMediumTerm}") BigDecimal loanInterestRateMediumTerm,
      @Value("${app.minLoanTermInDays}") Integer minLoanTermInDays,
      @Value("${app.maxLoanTermInDays}") Integer maxLoanTermInDays,
      @Value("${app.maxLoanRequestsPerIpPerDay}") Integer maxLoanRequestsPerIpPerDay) {

    this.loanRepository = loanRepository;
    this.loanInterestRateShortTerm = loanInterestRateShortTerm;
    this.loanInterestRateLongTerm = loanInterestRateLongTerm;
    this.loanInterestRateMediumTerm = loanInterestRateMediumTerm;
    this.minLoanAmount = minLoanAmount;
    this.maxLoanAmount = maxLoanAmount;
    this.minLoanTermInDays = minLoanTermInDays;
    this.maxLoanTermInDays = maxLoanTermInDays;
    this.maxLoanRequestsPerIpPerDay = maxLoanRequestsPerIpPerDay;
  }

  @Transactional
  public LoanEntity findById(Long id) {
    return loanRepository
        .findById(id)
        .orElseThrow(() -> ApiException.notFound(ERROR_PREFIX + "notFound"));
  }

  public List<LoanEntity> findAllClientLoansByClientId(Long clientId) {
    return loanRepository.findAllByClientId(clientId);
  }

  public List<LoanEntity> findAllApprovedLoansByClientId(Long clientId) {
    return loanRepository.findAllByClientIdAndStatus(clientId, LoanStatus.APPROVED);
  }

  public LoanEntity createLoan(LoanApplicationRequest request, Long clientId) {
    if (request.getAmount().compareTo(minLoanAmount) < 0
        || request.getAmount().compareTo(maxLoanAmount) > 0) {
      throw ApiException.bad(ERROR_PREFIX + "invalidLoanAmount")
          .addLabel("Requested Amount", request.getAmount())
          .addLabel("Minimum Loan Amount", minLoanAmount)
          .addLabel("Maximum Loan Amount", maxLoanAmount);
    }

    int loanTermInDays = request.getLoanTermInMonths() * 30;
    if (loanTermInDays < minLoanTermInDays || loanTermInDays > maxLoanTermInDays) {
      throw ApiException.bad(ERROR_PREFIX + "invalidTerm")
          .addLabel("Requested Term", loanTermInDays)
          .addLabel("Minimum Term", minLoanTermInDays)
          .addLabel("Maximum Term", maxLoanTermInDays);
    }

    BigDecimal interestRate = getInterestRate(loanTermInDays).setScale(4, RoundingMode.HALF_UP);
    BigDecimal interestAmount =
        request.getAmount().multiply(interestRate).setScale(2, RoundingMode.HALF_UP);

    Boolean hasReachedMaxLoanRequestsPerIpPerDay = hasReachedMaxLoanRequestsPerIpPerDay(request);
    Boolean isBetweenRiskHoursAndMaxAmount =
        request.getAmount().equals(maxLoanAmount) && request.getApplicationDate().getHour() <= 6;

    LoanEntity entity =
        new LoanEntity()
            .setAmount(request.getAmount())
            .setInterestRate(interestRate)
            .setInterestAmount(interestAmount)
            .setLoanTermInDays(loanTermInDays)
            .setStatus(
                isBetweenRiskHoursAndMaxAmount || hasReachedMaxLoanRequestsPerIpPerDay
                    ? LoanStatus.REJECTED
                    : LoanStatus.APPROVED)
            .setIpAddress(request.getIpAddress())
            .setClient(new ClientEntity().setId(clientId));

    entity = loanRepository.save(entity);

    if (hasReachedMaxLoanRequestsPerIpPerDay) {
      throw ApiException.reject(
              "Sorry, you have reached the maximum number of loan requests per day. Please try again tomorrow.")
          .addLabel("IP Address", request.getIpAddress())
          .addLabel("Max Requests", maxLoanRequestsPerIpPerDay);
    }

    if (isBetweenRiskHoursAndMaxAmount) {
      throw ApiException.reject(
              "Sorry, we are not able to provide loans for "
                  + maxLoanAmount
                  + " between the hours of 00:00 and 6:00. Please try again later.")
          .addLabel("Local Time", request.getApplicationDate().toLocalTime());
    }

    return entity;
  }

  public void updateLoan(LoanUpdateRequest request) {
    LoanEntity loan = findById(request.getId()).setUpdatedAt(LocalDateTime.now());

    if (request.getStatus() != null) {
      loan.setStatus(request.getStatus());
    }

    if (request.getInterestAmount() != null) {
      loan.setInterestAmount(request.getInterestAmount());
    }

    if (request.getInterestRate() != null) {
      loan.setInterestRate(request.getInterestRate());
    }

    if (request.getDueDate() != null) {
      loan.setDueDate(request.getDueDate());
    }

    loanRepository.save(loan);
  }

  private BigDecimal getInterestRate(Integer loanTermInDays) {
    return loanTermInDays < 360
        ? loanInterestRateShortTerm
        : loanTermInDays < 720 ? loanInterestRateMediumTerm : loanInterestRateLongTerm;
  }

  private boolean hasReachedMaxLoanRequestsPerIpPerDay(LoanApplicationRequest request) {
    Integer loansTakenOutToday =
        loanRepository.countByIpAddressAndCreatedAtBetween(
            request.getIpAddress(),
            LocalDate.now().atStartOfDay(),
            LocalDate.now().atTime(LocalTime.MAX));

    return loansTakenOutToday >= maxLoanRequestsPerIpPerDay;
  }
}
