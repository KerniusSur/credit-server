package com.financehw.kernius.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

@Component
public class AuthEntryPointJwt implements AuthenticationEntryPoint {
  private static final Logger LOGGER = LoggerFactory.getLogger(AuthEntryPointJwt.class);

  @Override
  public void commence(
      HttpServletRequest request,
      HttpServletResponse response,
      AuthenticationException authException)
      throws IOException {
    LOGGER.warn(
        "AUTH-WARNING: message = [{}] method = [{}] path = [{}]",
        authException.getMessage(),
        request.getMethod(),
        request.getRequestURI());
    response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Error: Unauthorized");
  }
}
