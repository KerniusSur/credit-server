package com.financehw.kernius.loan.dto.request;

import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@EqualsAndHashCode
public class LoanApplicationRequest {
  @NotNull private BigDecimal amount;
  @NotNull private Integer loanTermInMonths;
  // applicationDate date should be sent by the client, as the server may have a different time zone
  @NotNull private LocalDateTime applicationDate;
  // ipAddress should be sent by the client, as the server may be behind a load balancer, however
  // if the server is directly accessible, and ipAddress is null it can be obtained from the request
  private String ipAddress;
}
