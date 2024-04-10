package com.financehw.kernius;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

@SpringBootApplication
@EnableWebSecurity
public class KerniusApplication {

  public static void main(String[] args) {
    SpringApplication.run(KerniusApplication.class, args);
  }
}
