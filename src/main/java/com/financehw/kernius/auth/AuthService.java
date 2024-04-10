package com.financehw.kernius.auth;

import com.financehw.kernius.auth.dto.request.EmailPasswordLoginRequest;
import com.financehw.kernius.auth.dto.request.PersonalIdPasswordLoginRequest;
import com.financehw.kernius.auth.dto.request.RegisterRequest;
import com.financehw.kernius.auth.utils.CookieUtil;
import com.financehw.kernius.auth.utils.JwtUtil;
import com.financehw.kernius.client.ClientService;
import com.financehw.kernius.client.dto.request.ClientCreateRequest;
import com.financehw.kernius.client.entity.ClientEntity;
import com.financehw.kernius.exception.ApiException;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class AuthService {
  private final JwtUtil jwtUtil;
  private final CookieUtil cookieUtil;
  private final ClientService clientService;
  private final PasswordEncoder encoder;

  private final String ERROR_MESSAGE_PREFIX = "err.auth.";

  public AuthService(
      JwtUtil jwtUtil,
      ClientService clientService,
      CookieUtil cookieUtil,
      PasswordEncoder encoder) {
    this.jwtUtil = jwtUtil;
    this.cookieUtil = cookieUtil;
    this.clientService = clientService;
    this.encoder = encoder;
  }

  public void login(EmailPasswordLoginRequest request, HttpServletResponse response) {
    ClientEntity client = clientService.findByEmailOrNull(request.getEmail());
    validateClientAndAddAuthorityCookie(client, request.getPassword(), response);
  }

  public void login(PersonalIdPasswordLoginRequest request, HttpServletResponse response) {
    ClientEntity client = clientService.findByPersonalIdOrNull(request.getPersonalId());
    validateClientAndAddAuthorityCookie(client, request.getPassword(), response);
  }

  public void logout(HttpServletResponse response) {
    cookieUtil.deleteAuthorizationCookie(response);
  }

  public void register(RegisterRequest request, HttpServletResponse response) {
    if (clientService.existsByEmail(request.getEmail())) {
      throw ApiException.conflict(ERROR_MESSAGE_PREFIX + "conflict.email");
    }

    if (clientService.existsByPersonalId(request.getPersonalId())) {
      throw ApiException.conflict(ERROR_MESSAGE_PREFIX + "conflict.personalId");
    }

    ClientCreateRequest clientCreateRequest =
        new ClientCreateRequest()
            .setName(request.getName())
            .setLastName(request.getLastName())
            .setEmail(request.getEmail())
            .setPhoneNumber(request.getPhoneNumber())
            .setPersonalId(request.getPersonalId())
            .setPassword(encoder.encode(request.getPassword()));

    ClientEntity client = clientService.createClient(clientCreateRequest);
    addAuthorityCookie(client, response);
  }

  private void validateClientAndAddAuthorityCookie(
      ClientEntity client, String rawPassword, HttpServletResponse response) {
    if (client == null || !encoder.matches(rawPassword, client.getPassword())) {
      throw ApiException.bad(ERROR_MESSAGE_PREFIX + "invalidCredentials");
    }

    addAuthorityCookie(client, response);
  }

  private void addAuthorityCookie(ClientEntity client, HttpServletResponse response) {
    String token = jwtUtil.generateJwtToken(client);
    cookieUtil.addAuthorizationCookie(token, response);
  }
}
