package com.financehw.kernius.unit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.financehw.kernius.client.ClientRepository;
import com.financehw.kernius.client.ClientService;
import com.financehw.kernius.client.dto.request.ClientCreateRequest;
import com.financehw.kernius.client.entity.ClientEntity;
import com.financehw.kernius.exception.ApiException;
import com.financehw.kernius.utils.TestDataFactory;
import java.util.Optional;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
public class ClientUnitTests {
  private static TestDataFactory dataFactory;
  private final Boolean isValidPersonalCodeRequired = false;

  @Mock ClientRepository clientRepository;
  @InjectMocks ClientService clientService;

  @BeforeAll
  static void init() {
    dataFactory = new TestDataFactory();
  }

  @Test
  void findByPersonalIdOrNull_success() {
    String personalId = "50001010000";
    ClientEntity expectedClient = dataFactory.getValidClient().setPersonalId(personalId);

    when(clientRepository.findByPersonalId(personalId)).thenReturn(Optional.of(expectedClient));

    ClientEntity result = clientService.findByPersonalIdOrNull(personalId);

    assertEquals(result, expectedClient);
    verify(clientRepository).findByPersonalId(personalId);
  }

  @Test
  void findByPersonalIdOrNull_clientNotFound() {
    String personalId = "50001019999";
    when(clientRepository.findByPersonalId(personalId)).thenReturn(Optional.empty());

    ClientEntity result = clientService.findByPersonalIdOrNull(personalId);

    assertNull(result);
    verify(clientRepository).findByPersonalId(personalId);
  }

  @Test
  void existsByEmail_true() {
    String email = "test@example.com";
    when(clientRepository.existsByEmail(email)).thenReturn(true);

    Boolean result = clientService.existsByEmail(email);
    assertFalse(isValidPersonalCodeRequired);
    assertTrue(result);
    verify(clientRepository).existsByEmail(email);
  }

  @Test
  void existsByEmail_false() {
    String email = "johny.doe@email.com";
    when(clientRepository.existsByEmail(email)).thenReturn(false);

    Boolean result = clientService.existsByEmail(email);

    assertFalse(result);
    verify(clientRepository).existsByEmail(email);
  }

  @Test
  void existsByPersonalId_true() {
    String personalId = "50001010000";
    when(clientRepository.existsByPersonalId(personalId)).thenReturn(true);

    Boolean result = clientService.existsByPersonalId(personalId);

    assertTrue(result);
    verify(clientRepository).existsByPersonalId(personalId);
  }

  @Test
  void existsByPersonalId_false() {
    String personalId = "50001019999";
    when(clientRepository.existsByPersonalId(personalId)).thenReturn(false);

    Boolean result = clientService.existsByPersonalId(personalId);

    assertFalse(result);
    verify(clientRepository).existsByPersonalId(personalId);
  }

  @Test
  void createClient_success() {
    ReflectionTestUtils.setField(clientService, "isValidPersonalCodeRequired", false);
    ClientCreateRequest request = dataFactory.getValidClientCreateRequest();

    when(clientRepository.findByPersonalId(anyString())).thenReturn(Optional.empty());
    when(clientRepository.save(any(ClientEntity.class)))
        .thenAnswer(invocation -> invocation.getArgument(0));

    ClientEntity result = clientService.createClient(request);

    verify(clientRepository).findByPersonalId(request.getPersonalId());
    verify(clientRepository).save(any(ClientEntity.class));
    assertNotNull(result);
    assertEquals(result.getName(), request.getName());
    assertEquals(result.getLastName(), request.getLastName());
    assertEquals(result.getEmail(), request.getEmail());
    assertEquals(result.getPhoneNumber(), request.getPhoneNumber());
    assertEquals(result.getPersonalId(), request.getPersonalId());
    assertEquals(result.getPassword(), request.getPassword());
  }
  ;

  @Test
  void createClient_conflict() {
    ClientCreateRequest request = dataFactory.getValidClientCreateRequest();
    when(clientRepository.findByPersonalId(anyString()))
        .thenReturn(Optional.of(new ClientEntity()));

    ApiException exception =
        assertThrows(ApiException.class, () -> clientService.createClient(request));

    assertEquals(exception.getMessage(), "err.client.conflict.personalId");
  }

  @Test
  void createClient_invalidPersonalId() {
    // Mocking the isValidPersonalCodeRequired to true in order to test the validation
    ReflectionTestUtils.setField(clientService, "isValidPersonalCodeRequired", true);
    ClientCreateRequest request =
        dataFactory.getValidClientCreateRequest().setPersonalId("1234567890");
    when(clientRepository.findByPersonalId(anyString())).thenReturn(Optional.empty());

    ApiException exception =
        assertThrows(ApiException.class, () -> clientService.createClient(request));

    assertEquals(exception.getMessage(), "err.client.invalidPersonalId");
    verify(clientRepository).findByPersonalId(request.getPersonalId());
  }
}
