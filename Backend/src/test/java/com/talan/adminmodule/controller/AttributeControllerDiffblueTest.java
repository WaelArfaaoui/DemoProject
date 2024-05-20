package com.talan.adminmodule.controller;

import static org.mockito.Mockito.when;

import com.talan.adminmodule.dto.AttributeDto;
import com.talan.adminmodule.service.AttributeService;

import java.util.ArrayList;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.aot.DisabledInAotMode;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@ContextConfiguration(classes = {AttributeController.class})
@ExtendWith(SpringExtension.class)
@DisabledInAotMode
class AttributeControllerDiffblueTest {
    @Autowired
    private AttributeController attributeController;

    @MockBean
    private AttributeService attributeService;

    /**
     * Method under test: {@link AttributeController#getAllAttributes()}
     */
    @Test
    void testGetAllAttributes() throws Exception {
        // Arrange
        when(attributeService.findAll()).thenReturn(new ArrayList<>());
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get("/api/attributes");

        // Act and Assert
        MockMvcBuilders.standaloneSetup(attributeController)
                .build()
                .perform(requestBuilder)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json"))
                .andExpect(MockMvcResultMatchers.content().string("[]"));
    }

    /**
     * Method under test: {@link AttributeController#getAttributeById(Integer)}
     */
    @Test
    void testGetAttributeById() throws Exception {
        // Arrange
        AttributeDto buildResult = AttributeDto.builder().id(1).name("Name").build();
        when(attributeService.findById(Mockito.<Integer>any())).thenReturn(buildResult);
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get("/api/attributes/{id}", 1);

        // Act and Assert
        MockMvcBuilders.standaloneSetup(attributeController)
                .build()
                .perform(requestBuilder)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json"))
                .andExpect(MockMvcResultMatchers.content().string("{\"id\":1,\"name\":\"Name\"}"));
    }
}
