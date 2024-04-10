package com.financehw.kernius.loanextension.dto.response;

import java.time.LocalDateTime;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@EqualsAndHashCode
public class LoanExtensionResponse {
  private Long id;
  private Long loanId;
  private Integer additionalLoanTermInDays;
  private LocalDateTime createdAt;
}
