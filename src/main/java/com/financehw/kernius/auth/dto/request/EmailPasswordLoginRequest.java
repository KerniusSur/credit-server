package com.financehw.kernius.auth.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@EqualsAndHashCode
public class EmailPasswordLoginRequest {
  @NotEmpty @Email private String email;
  @NotEmpty private String password;
}
