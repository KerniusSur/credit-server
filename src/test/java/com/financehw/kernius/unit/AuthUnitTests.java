package com.financehw.kernius.unit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.financehw.kernius.auth.AuthService;
import com.financehw.kernius.auth.dto.request.EmailPasswordLoginRequest;
import com.financehw.kernius.auth.dto.request.PersonalIdPasswordLoginRequest;
import com.financehw.kernius.auth.dto.request.RegisterRequest;
import com.financehw.kernius.auth.utils.CookieUtil;
import com.financehw.kernius.auth.utils.JwtUtil;
import com.financehw.kernius.client.ClientService;
import com.financehw.kernius.client.dto.request.ClientCreateRequest;
import com.financehw.kernius.client.entity.ClientEntity;
import com.financehw.kernius.exception.ApiException;
import com.financehw.kernius.utils.TestDataFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
public class AuthUnitTests {
  private static TestDataFactory dataFactory;

  @Mock PasswordEncoder passwordEncoder;
  @Mock JwtUtil jwtUtil;
  @Mock CookieUtil cookieUtil;
  @Mock ClientService clientService;
  @InjectMocks AuthService authService;

  @BeforeAll
  static void init() {
    dataFactory = new TestDataFactory();
  }

  @Test
  void successful_login_with_email() {
    MockHttpServletResponse response = new MockHttpServletResponse();
    ClientEntity foundClient = dataFactory.getValidClient();
    String token = dataFactory.getDummyJwtToken();
    EmailPasswordLoginRequest loginRequest =
        dataFactory.getValidEmailPasswordLoginRequest(foundClient);

    when(clientService.findByEmailOrNull(anyString())).thenReturn(foundClient);
    when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true);
    when(jwtUtil.generateJwtToken(any())).thenReturn(token);

    authService.login(loginRequest, response);

    verify(jwtUtil).generateJwtToken(any());
    verify(cookieUtil).addAuthorizationCookie(anyString(), eq(response));
    verify(passwordEncoder).matches(anyString(), anyString());
    verify(clientService).findByEmailOrNull(foundClient.getEmail());
  }

  @Test
  void successful_login_with_personal_id() {
    MockHttpServletResponse response = new MockHttpServletResponse();
    ClientEntity foundClient = dataFactory.getValidClient();
    String token = dataFactory.getDummyJwtToken();

    PersonalIdPasswordLoginRequest loginRequest =
        dataFactory.getValidPersonalIdPasswordLoginRequest(foundClient);

    when(clientService.findByPersonalIdOrNull(anyString())).thenReturn(foundClient);
    when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true);
    when(jwtUtil.generateJwtToken(foundClient)).thenReturn(token);

    authService.login(loginRequest, response);

    verify(clientService).findByPersonalIdOrNull(foundClient.getPersonalId());
    verify(passwordEncoder).matches(Mockito.anyString(), anyString());
    verify(jwtUtil).generateJwtToken(foundClient);
    verify(cookieUtil).addAuthorizationCookie(token, response);
  }

  @Test
  void should_throw_the_same_api_exception_on_any_incorrect_data_in_login() {
    MockHttpServletResponse response = new MockHttpServletResponse();
    ClientEntity foundClient = dataFactory.getValidClient();
    String expectedErrorMessage = "err.auth.invalidCredentials";
    EmailPasswordLoginRequest wrongPasswordEmailLoginRequest =
        dataFactory.getValidEmailPasswordLoginRequest(foundClient).setPassword("wrong_password");
    PersonalIdPasswordLoginRequest wrongPasswordPersonalIdLoginRequest =
        dataFactory
            .getValidPersonalIdPasswordLoginRequest(foundClient)
            .setPassword("wrong_password");
    EmailPasswordLoginRequest wrongEmailLoginRequest =
        dataFactory.getValidEmailPasswordLoginRequest(foundClient).setEmail("wrong_email");
    PersonalIdPasswordLoginRequest wrongPersonalIdLoginRequest =
        dataFactory
            .getValidPersonalIdPasswordLoginRequest(foundClient)
            .setPersonalId("wrong_personal_id");

    Mockito.when(passwordEncoder.matches(Mockito.anyString(), Mockito.anyString()))
        .thenReturn(false);
    Mockito.when(clientService.findByEmailOrNull(wrongPasswordEmailLoginRequest.getEmail()))
        .thenReturn(foundClient);
    Mockito.when(
            clientService.findByPersonalIdOrNull(
                wrongPasswordPersonalIdLoginRequest.getPersonalId()))
        .thenReturn(foundClient);
    Mockito.when(clientService.findByEmailOrNull(wrongEmailLoginRequest.getEmail()))
        .thenReturn(null);
    Mockito.when(clientService.findByPersonalIdOrNull(wrongPersonalIdLoginRequest.getPersonalId()))
        .thenReturn(null);

    ApiException exception1 =
        assertThrows(
            ApiException.class, () -> authService.login(wrongPasswordEmailLoginRequest, response));
    ApiException exception2 =
        assertThrows(
            ApiException.class,
            () -> authService.login(wrongPasswordPersonalIdLoginRequest, response));
    ApiException exception3 =
        assertThrows(ApiException.class, () -> authService.login(wrongEmailLoginRequest, response));
    ApiException exception4 =
        assertThrows(
            ApiException.class, () -> authService.login(wrongPersonalIdLoginRequest, response));

    assertEquals(expectedErrorMessage, exception1.getMessage());
    assertEquals(expectedErrorMessage, exception2.getMessage());
    assertEquals(expectedErrorMessage, exception3.getMessage());
    assertEquals(expectedErrorMessage, exception4.getMessage());
  }

  @Test
  void successful_logout() {
    MockHttpServletResponse response = new MockHttpServletResponse();
    authService.logout(response);
    verify(cookieUtil).deleteAuthorizationCookie(response);
    verify(cookieUtil, times(1)).deleteAuthorizationCookie(response);
  }

  @Test
  void successful_register() {
    MockHttpServletResponse response = new MockHttpServletResponse();
    ClientEntity foundClient = dataFactory.getValidClient();
    String token = dataFactory.getDummyJwtToken();

    RegisterRequest request = dataFactory.getValidRegisterRequest();
    String rawPassword = request.getPassword();

    when(clientService.existsByEmail(foundClient.getEmail())).thenReturn(false);
    when(clientService.existsByPersonalId(foundClient.getPersonalId())).thenReturn(false);
    when(passwordEncoder.encode(rawPassword)).thenReturn("encodedPassword");
    when(clientService.createClient(any(ClientCreateRequest.class))).thenReturn(foundClient);
    when(jwtUtil.generateJwtToken(foundClient)).thenReturn(token);

    authService.register(request, response);

    verify(clientService).createClient(any(ClientCreateRequest.class));
    verify(passwordEncoder).encode(rawPassword);
    verify(jwtUtil).generateJwtToken(foundClient);
    verify(cookieUtil).addAuthorizationCookie(token, response);
  }

  @Test
  void failed_register_conflict() {
    MockHttpServletResponse response = new MockHttpServletResponse();
    ClientEntity foundClient = dataFactory.getValidClient();
    String expectedEmailErrorMessage = "err.auth.conflict.email";
    String expectedPersonalIdErrorMessage = "err.auth.conflict.personalId";
    RegisterRequest request = dataFactory.getValidRegisterRequest();

    when(clientService.existsByEmail(foundClient.getEmail())).thenReturn(true);
    when(clientService.existsByPersonalId(foundClient.getPersonalId())).thenReturn(false);

    ApiException exception =
        assertThrows(ApiException.class, () -> authService.register(request, response));

    assertEquals(expectedEmailErrorMessage, exception.getMessage());

    when(clientService.existsByEmail(foundClient.getEmail())).thenReturn(false);
    when(clientService.existsByPersonalId(foundClient.getPersonalId())).thenReturn(true);

    exception = assertThrows(ApiException.class, () -> authService.register(request, response));

    assertThrows(ApiException.class, () -> authService.register(request, response));
    assertEquals(expectedPersonalIdErrorMessage, exception.getMessage());

    verify(clientService, times(0)).createClient(any());
  }
}
