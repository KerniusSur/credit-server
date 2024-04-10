package com.financehw.kernius.loan.mapper;

import com.financehw.kernius.loan.dto.response.LoanResponse;
import com.financehw.kernius.loan.entity.LoanEntity;
import com.financehw.kernius.loanextension.mapper.LoanExtensionMapper;
import com.financehw.kernius.utils.ListUtil;

public class LoanMapper {
  public static LoanResponse toResponse(LoanEntity loanEntity) {
    return new LoanResponse()
        .setId(loanEntity.getId())
        .setAmount(loanEntity.getAmount())
        .setInterestRate(loanEntity.getInterestRate())
        .setInterestAmount(loanEntity.getInterestAmount())
        .setLoanTermInDays(loanEntity.getLoanTermInDays())
        .setStatus(loanEntity.getStatus())
        .setDueDate(loanEntity.getDueDate())
        .setCreatedAt(loanEntity.getCreatedAt())
        .setUpdatedAt(loanEntity.getUpdatedAt())
        .setIpAddress(loanEntity.getIpAddress())
        .setLoanExtensions(
            ListUtil.map(loanEntity.getLoanExtensions(), LoanExtensionMapper::toResponse));
  }
}
