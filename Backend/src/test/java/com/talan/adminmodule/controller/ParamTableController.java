package com.talan.adminmodule.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@TestPropertySource(locations = "classpath:application-test.properties")
@ActiveProfiles("test")
public class ParamTableController {
/*    @Autowired
    private ParamTableController paramTableController;
    @Test
    void testRetrieveAllTablesAndColumns(){
        int limit = 3;
        int offset =3;


    }*/
}
