package com.financehw.kernius.client;

import com.financehw.kernius.client.dto.request.ClientCreateRequest;
import com.financehw.kernius.client.entity.ClientEntity;
import com.financehw.kernius.exception.ApiException;
import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class ClientService {
  private final ClientRepository clientRepository;
  private final String ERROR_PREFIX = "err.client.";

  @Value("${app.isValidPersonalCodeRequired}")
  private Boolean isValidPersonalCodeRequired;

  public ClientService(ClientRepository clientRepository) {
    this.clientRepository = clientRepository;
  }

  public ClientEntity findByPersonalIdOrNull(String personalId) {
    return clientRepository.findByPersonalId(personalId).orElse(null);
  }

  public Boolean existsByEmail(String email) {
    return clientRepository.existsByEmail(email);
  }

  public Boolean existsByPersonalId(String personalId) {
    return clientRepository.existsByPersonalId(personalId);
  }

  public ClientEntity findByEmailOrNull(String email) {
    return clientRepository.findByEmail(email).orElse(null);
  }

  public ClientEntity createClient(ClientCreateRequest request) {
    if (clientRepository.findByPersonalId(request.getPersonalId()).isPresent()) {
      throw ApiException.conflict(ERROR_PREFIX + "conflict.personalId");
    }

    if (!isViablePersonalId(request.getPersonalId())) {
      throw ApiException.bad(ERROR_PREFIX + "invalidPersonalId");
    }

    ClientEntity entity = new ClientEntity();
    entity.setName(request.getName());
    entity.setLastName(request.getLastName());
    entity.setEmail(request.getEmail());
    entity.setPhoneNumber(request.getPhoneNumber());
    entity.setPersonalId(request.getPersonalId());
    entity.setPassword(request.getPassword());

    return clientRepository.save(entity);
  }

  private boolean isViablePersonalId(String personalId) {
    // If personal code validation is not required, return true
    if (!isValidPersonalCodeRequired) {
      return true;
    }

    if (personalId.length() != 11) {
      return false;
    }

    if (!personalId.matches("\\d+")) {
      return false;
    }

    int gender = Character.getNumericValue(personalId.charAt(0));
    int year = Integer.parseInt(personalId.substring(1, 3));
    int month = Integer.parseInt(personalId.substring(3, 5));
    int day = Integer.parseInt(personalId.substring(5, 7));

    if (!isValidBirthDate(year, month, day, gender)) {
      return false;
    }

    return isValidControlDigit(personalId);
  }

  private boolean isValidBirthDate(int year, int month, int day, int gender) {
    // Determine the birth century based on the first digit of the PIN and add it to the
    // birth year
    if (gender < 5) {
      if (gender < 3) {
        year += 1800;
      } else {
        year += 1900;
      }
    } else {
      year += 2000;
    }

    // Try to create a LocalDate object from the given values, if it fails, return false
    try {
      LocalDate date = LocalDate.of(year, month, day);

      return date.getYear() == year
          && date.getMonth() == Month.of(month)
          && date.getDayOfMonth() == day;

    } catch (Exception e) {
      return false;
    }
  }

  /**
   * The function checks the validity of a control digit in a personal identification number.
   *
   * @param pin The PIN to be validated.
   * @return The method is returns a boolean value indicating whether the control digit in the given
   *     personal code (PIN) is valid.
   */
  private boolean isValidControlDigit(String pin) {
    List<Integer> personalCodeDigits = new ArrayList<>();
    for (int i = 0; i < pin.length(); i++) {
      personalCodeDigits.add(Character.getNumericValue(pin.charAt(i)));
    }
    List<Integer> personalCodePrefix = personalCodeDigits.subList(0, 10);
    int personalCodeSuffix = personalCodeDigits.get(10);

    int controlNumber = calculatePersonalCodeControlNumber(personalCodePrefix, 1);
    if (controlNumber == 10) {
      controlNumber = calculatePersonalCodeControlNumber(personalCodePrefix, 3);
    }

    return controlNumber == 10 ? personalCodeSuffix == 0 : personalCodeSuffix == controlNumber;
  }

  /**
   * The function calculates the control number for a personal code based on a given prefix and
   * factor.
   *
   * @param prefix The `prefix` parameter is a list of integers that represents a part of a personal
   *     code.
   * @param factor The `factor` parameter is used to determine the modulus value for each element in
   *     the `prefix` list. It is added to the index before taking the modulus operation to
   *     calculate the mod value.
   * @return The method is returns the calculated control number.
   */
  private int calculatePersonalCodeControlNumber(List<Integer> prefix, int factor) {
    int sum = 0;
    for (int index = 0; index < prefix.size(); index++) {
      int mod = (index + factor) % 10;
      if (mod == 0) {
        mod += 1;
        factor += 1;
      }
      sum += prefix.get(index) * mod;
    }
    return sum % 11;
  }
}
