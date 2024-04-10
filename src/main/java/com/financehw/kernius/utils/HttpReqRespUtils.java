package com.financehw.kernius.utils;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

public class HttpReqRespUtils {
  private static final String[] IP_HEADER_CANDIDATES = {
    "X-Forwarded-For",
    "Proxy-Client-IP",
    "WL-Proxy-Client-IP",
    "HTTP_X_FORWARDED_FOR",
    "HTTP_X_FORWARDED",
    "HTTP_X_CLUSTER_CLIENT_IP",
    "HTTP_CLIENT_IP",
    "HTTP_FORWARDED_FOR",
    "HTTP_FORWARDED",
    "HTTP_VIA",
    "REMOTE_ADDR"
  };

  /**
   * Retrieves the client's IP address from an available HTTP servlet request. This method attempts
   * to extract the IP address from various request headers where it might be present due to proxies
   * or load balancers.
   *
   * @return The client's IP address as a String, or "0.0.0.0" if not found.
   */
  public static String getClientIpAddressIfServletRequestExist() {
    if (RequestContextHolder.getRequestAttributes() == null) {
      return "0.0.0.0";
    }

    HttpServletRequest request =
        ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();

    for (String header : IP_HEADER_CANDIDATES) {
      String ipList = request.getHeader(header);
      if (ipList != null && ipList.length() != 0 && !"unknown".equalsIgnoreCase(ipList)) {
        String ip = ipList.split(",")[0];
        if (ip.equals("0:0:0:0:0:0:0:1")) {
          ip = "127.0.0.1";
        }

        return ip;
      }
    }
    String ip = request.getRemoteAddr();
    if (ip.equals("0:0:0:0:0:0:0:1")) {
      ip = "127.0.0.1";
    }

    return ip;
  }
}
