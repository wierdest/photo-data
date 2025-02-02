package com.wierdest.photo_data;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.wierdest.photo_data.controllers.HelloController;
import com.wierdest.photo_data.services.HelloService;

// tests just the layer below the server (request controller) layer

@WebMvcTest(HelloController.class)
class HelloWebMockTest {
   
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private HelloService service;

    @Test
    void helloParamShouldReturnDefaultMessageFromService()  throws Exception {
        when(service.getHelloMessage("")).thenReturn("Hello, Mock");
        this.mockMvc.perform(get("/hello/param?message=")).andDo(print()).andExpect(status().isOk())
            .andExpect(content().string(containsString("Hello, Mock")));
    }
}
