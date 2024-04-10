package com.financehw.kernius.loanextension.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@EqualsAndHashCode
public class LoanExtensionRequest {
  @NotNull private Long loanId;
}
