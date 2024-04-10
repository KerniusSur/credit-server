package com.financehw.kernius.client.dto.request;

import jakarta.validation.constraints.NotEmpty;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@EqualsAndHashCode
public class ClientCreateRequest {
  private String name;
  private String lastName;
  @NotEmpty private String email;
  @NotEmpty private String phoneNumber;
  @NotEmpty private String personalId;
  @NotEmpty private String password;
}
