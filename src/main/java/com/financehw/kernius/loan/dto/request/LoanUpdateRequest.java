package com.financehw.kernius.loan.dto.request;

import com.financehw.kernius.loan.entity.LoanStatus;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@EqualsAndHashCode
public class LoanUpdateRequest {
  @NotNull private Long id;
  private LoanStatus status;
  private BigDecimal interestAmount;
  private BigDecimal interestRate;
  private LocalDate dueDate;
}
