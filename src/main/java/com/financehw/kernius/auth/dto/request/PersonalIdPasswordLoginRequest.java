package com.financehw.kernius.auth.dto.request;

import jakarta.validation.constraints.NotEmpty;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@EqualsAndHashCode
public class PersonalIdPasswordLoginRequest {
  @NotEmpty private String personalId;
  @NotEmpty private String password;
}
