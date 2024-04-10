package com.financehw.kernius.loan.dto.response;

import com.financehw.kernius.loan.entity.LoanStatus;
import com.financehw.kernius.loanextension.dto.response.LoanExtensionResponse;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@EqualsAndHashCode
public class LoanResponse {
  private Long id;
  private BigDecimal amount;
  private BigDecimal interestRate;
  private BigDecimal interestAmount;
  private Integer loanTermInDays;
  private LoanStatus status;
  private LocalDate dueDate;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;
  private String ipAddress;
  private List<LoanExtensionResponse> loanExtensions;
}
