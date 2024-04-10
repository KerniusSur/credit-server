package com.financehw.kernius.utils;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

@AutoConfigureMockMvc
@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public abstract class IntegrationTest {
  @Autowired private MockMvc mockMvc;

  public static String asJsonString(final Object obj) {
    try {
      return new ObjectMapper().findAndRegisterModules().writeValueAsString(obj);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  public ResultActions performGet(String path) {
    try {
      return mockMvc.perform(get(path).accept(MediaType.APPLICATION_JSON));
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  public ResultActions performGet(String path, Map<String, Object> params) {
    try {
      MockHttpServletRequestBuilder builder = get(path);
      if (!params.isEmpty()) {
        params.forEach((key, value) -> builder.param(key, value.toString()));
      }
      return mockMvc.perform(builder.accept(MediaType.APPLICATION_JSON));
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  public ResultActions performPost(String path, Object object) {
    try {
      return mockMvc.perform(
          post(path)
              .accept(MediaType.APPLICATION_JSON)
              .contentType(MediaType.APPLICATION_JSON)
              .content(asJsonString(object)));
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  public ResultActions performPut(String path, Object object) {
    try {
      return mockMvc.perform(
          put(path)
              .accept(MediaType.APPLICATION_JSON)
              .contentType(MediaType.APPLICATION_JSON)
              .content(asJsonString(object)));
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  public ResultActions performDelete(String path) {
    try {
      return mockMvc.perform(
          delete(path).accept(MediaType.APPLICATION_JSON).contentType(MediaType.APPLICATION_JSON));
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }
}
