package com.financehw.kernius.auth.utils;

import com.financehw.kernius.client.entity.ClientEntity;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import jakarta.annotation.Nullable;
import jakarta.servlet.http.HttpServletRequest;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.*;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class JwtUtil {
  private static final Logger LOGGER = LoggerFactory.getLogger(JwtUtil.class);
  private static final String APPLICATION_ID = "application_id";
  private static final String EMAIL = "email";
  private static final String PERSONAL_ID = "personal_id";
  private static final String CLIENT_ID = "client_id";

  private final CookieUtil cookieUtil;
  private final Key secretKey;
  private final JwtParser jwtParser;
  private final int jwtExpirationMs;

  public JwtUtil(
      CookieUtil cookieUtil,
      @Value("${app.jwt.secret}") String jwtSecret,
      @Value("${app.jwt.expirationMs}") int jwtExpirationMs) {
    this.cookieUtil = cookieUtil;
    this.secretKey =
        Keys.hmacShaKeyFor(Base64.getEncoder().encode(jwtSecret.getBytes(StandardCharsets.UTF_8)));
    this.jwtParser = Jwts.parserBuilder().setSigningKey(this.secretKey).build();
    this.jwtExpirationMs = jwtExpirationMs;
  }

  public String generateJwtToken(ClientEntity client) {
    var now = new Date();

    return Jwts.builder()
        .setSubject("me")
        .claim(CLIENT_ID, client.getId())
        .claim(EMAIL, client.getEmail())
        .claim(PERSONAL_ID, client.getPersonalId())
        .setIssuedAt(now)
        .setExpiration(new Date(now.getTime() + jwtExpirationMs))
        .signWith(secretKey)
        .compact();
  }

  /**
   * This method returns user email claim from JWT token.
   *
   * @param token JWT token string.
   * @return UUID value
   */
  public UUID getApplicationIdClaim(String token) {
    isValid(token);
    var rawUuid = (String) jwtParser.parseClaimsJws(token).getBody().get(APPLICATION_ID);
    return UUID.fromString(rawUuid);
  }

  /**
   * This method returns user email claim from JWT token.
   *
   * @param token JWT token string.
   * @return String or null.
   */
  @Nullable
  public String getEmailClaim(String token) {
    isValid(token);
    return (String) jwtParser.parseClaimsJws(token).getBody().get(EMAIL);
  }

  /**
   * This method returns user personal id claim from JWT token.
   *
   * @param token JWT token string.
   * @return String or null.
   */
  @Nullable
  public String getPersonalIdClaim(String token) {
    isValid(token);
    return (String) jwtParser.parseClaimsJws(token).getBody().get(PERSONAL_ID);
  }

  /**
   * This method returns user id from JWT token.
   *
   * @param token JWT token string.
   * @return Integer or null.
   */
  @Nullable
  public Integer getUserId(String token) {
    isValid(token);
    return (Integer) jwtParser.parseClaimsJws(token).getBody().get(CLIENT_ID);
  }

  /**
   * This method validates JWT claim. There are three possible cases: 1) Returns false if JWT claim
   * is empty or null; 2) Returns false if JWT claim is not parsable; 3) Returns true if JWT claim
   * is parsable.
   *
   * @param jwtClaim JWT claim string.
   * @return true or false according to JWT claim.
   */
  public boolean isValid(String jwtClaim) {
    if (jwtClaim == null || StringUtils.isBlank(jwtClaim)) {
      LOGGER.error("JWT token is empty");
      return false;
    }

    try {
      jwtParser.parseClaimsJws(jwtClaim);
      return true;
    } catch (SignatureException e) {
      LOGGER.error("Invalid JWT signature: {}", e.getMessage());
    } catch (MalformedJwtException e) {
      LOGGER.error("Invalid JWT token: {}", e.getMessage());
    } catch (ExpiredJwtException e) {
      LOGGER.error("JWT token is expired: {}", e.getMessage());
    } catch (UnsupportedJwtException e) {
      LOGGER.error("JWT token is unsupported: {}", e.getMessage());
    } catch (IllegalArgumentException e) {
      LOGGER.error("JWT claims string is empty: {}", e.getMessage());
    }
    return false;
  }

  /**
   * This method parses http servlet request and returns a JWT token. It does that by retrieving the
   * value from authentication cookie.
   *
   * @param request incoming request.
   * @return JWT token string or null.
   */
  @Nullable
  public String parseJwt(HttpServletRequest request) {
    return cookieUtil.getAuthorizationCookieValue(request);
  }
}
