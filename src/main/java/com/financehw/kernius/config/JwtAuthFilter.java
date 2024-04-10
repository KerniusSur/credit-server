package com.financehw.kernius.config;

import com.financehw.kernius.auth.entity.AuthenticatedProfile;
import com.financehw.kernius.auth.utils.JwtUtil;
import com.financehw.kernius.exception.ApiException;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import java.io.IOException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class JwtAuthFilter implements Filter {
  private final JwtUtil jwtUtil;

  public JwtAuthFilter(JwtUtil jwtUtil) {
    this.jwtUtil = jwtUtil;
  }

  @Override
  public void init(FilterConfig filterConfig) {}

  @Override
  public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
      throws IOException, ServletException {
    String token = jwtUtil.parseJwt((HttpServletRequest) request);
    if (token != null && jwtUtil.isValid(token)) {
      Authentication profile = buildProfile(token);
      SecurityContextHolder.getContext().setAuthentication(profile);
    }
    chain.doFilter(request, response);
  }

  @Override
  public void destroy() {}

  private AuthenticatedProfile buildProfile(String token) throws ApiException {
    AuthenticatedProfile profile = new AuthenticatedProfile();
    Long userId = Long.valueOf(jwtUtil.getUserId(token));
    String email = jwtUtil.getEmailClaim(token);
    String personalId = jwtUtil.getPersonalIdClaim(token);
    profile.setId(userId).setEmail(email).setPersonalId(personalId).setAuthenticated(true);
    return profile;
  }
}
