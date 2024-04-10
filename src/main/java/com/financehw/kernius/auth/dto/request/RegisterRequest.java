package com.financehw.kernius.auth.dto.request;

import jakarta.validation.constraints.NotEmpty;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@EqualsAndHashCode
public class RegisterRequest {
  @NotEmpty private String name;
  @NotEmpty private String lastName;
  @NotEmpty private String email;
  @NotEmpty private String phoneNumber;
  @NotEmpty private String personalId;
  @NotEmpty private String password;
}
