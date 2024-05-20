package com.talan.adminmodule.controller;

import static org.mockito.Mockito.when;

import com.talan.adminmodule.dto.CategoryDto;
import com.talan.adminmodule.service.CategoryService;

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

@ContextConfiguration(classes = {CategoryController.class})
@ExtendWith(SpringExtension.class)
@DisabledInAotMode
class CategoryControllerDiffblueTest {
    @Autowired
    private CategoryController categoryController;

    @MockBean
    private CategoryService categoryService;

    /**
     * Method under test: {@link CategoryController#getAllCategories()}
     */
    @Test
    void testGetAllCategories() throws Exception {
        // Arrange
        when(categoryService.findAll()).thenReturn(new ArrayList<>());
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get("/api/categories");

        // Act and Assert
        MockMvcBuilders.standaloneSetup(categoryController)
                .build()
                .perform(requestBuilder)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json"))
                .andExpect(MockMvcResultMatchers.content().string("[]"));
    }

    /**
     * Method under test: {@link CategoryController#getCategoryById(Integer)}
     */
    @Test
    void testGetCategoryById() throws Exception {
        // Arrange
        CategoryDto buildResult = CategoryDto.builder().id(1).name("Name").build();
        when(categoryService.findById(Mockito.<Integer>any())).thenReturn(buildResult);
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get("/api/categories/{id}", 1);

        // Act and Assert
        MockMvcBuilders.standaloneSetup(categoryController)
                .build()
                .perform(requestBuilder)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json"))
                .andExpect(MockMvcResultMatchers.content().string("{\"id\":1,\"name\":\"Name\"}"));
    }
}
