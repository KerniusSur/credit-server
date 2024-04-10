package com.financehw.kernius.auth;

import com.financehw.kernius.auth.dto.request.EmailPasswordLoginRequest;
import com.financehw.kernius.auth.dto.request.PersonalIdPasswordLoginRequest;
import com.financehw.kernius.auth.dto.request.RegisterRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Transactional
@RequestMapping("/api/v1/auth")
@Tag(name = "Auth", description = "Authentication API operations")
public class AuthController {
  private final AuthService authService;

  public AuthController(AuthService authService) {
    this.authService = authService;
  }

  @PostMapping("/login")
  @Operation(summary = "Login with email and password", method = "POST")
  public void login(@RequestBody EmailPasswordLoginRequest request, HttpServletResponse response) {
    authService.login(request, response);
  }

  @PostMapping("/login/personalId")
  @Operation(summary = "Login with personal ID and password", method = "POST")
  public void login(
      @RequestBody PersonalIdPasswordLoginRequest request, HttpServletResponse response) {
    authService.login(request, response);
  }

  @PostMapping("/logout")
  @Operation(summary = "Logout", method = "POST")
  public void logout(HttpServletResponse response) {
    authService.logout(response);
  }

  @PostMapping("/register")
  @Operation(summary = "Register", method = "POST")
  public void register(@RequestBody RegisterRequest request, HttpServletResponse response) {
    authService.register(request, response);
  }
}
