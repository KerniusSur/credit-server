package com.financehw.kernius.loanextension.mapper;

import com.financehw.kernius.loanextension.dto.response.LoanExtensionResponse;
import com.financehw.kernius.loanextension.entity.LoanExtensionEntity;

public class LoanExtensionMapper {
  public static LoanExtensionResponse toResponse(LoanExtensionEntity loanExtensionEntity) {
    return new LoanExtensionResponse()
        .setId(loanExtensionEntity.getId())
        .setLoanId(loanExtensionEntity.getLoan().getId())
        .setAdditionalLoanTermInDays(loanExtensionEntity.getAdditionalLoanTermInDays())
        .setCreatedAt(loanExtensionEntity.getCreatedAt());
  }
}
