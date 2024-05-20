package com.talan.adminmodule.service;

import com.talan.adminmodule.dto.*;

import javax.sql.DataSource;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.talan.adminmodule.entity.ParamAudit;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.w3c.dom.css.Counter;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;


@ExtendWith(SpringExtension.class)
@TestPropertySource(locations = "classpath:application-test.properties")
@ActiveProfiles("test")
@SpringBootTest

class ParamTableServiceTest {


    @Autowired
    private ParamTableService paramTableService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

    }
   @Test
   void testAddInstance() {
       Map<String, String> instanceData = new HashMap<>();
       instanceData.put("first_name", "Camavinga");
       instanceData.put("last_name", "CamaoCamao");
       instanceData.put("last_update",LocalDateTime.now().toString() );
       String tableName = "actor";
       ResponseDto response = paramTableService.addInstance(instanceData, tableName);
       assertNotNull(response);
       assertEquals("Record added successfully", response.getSuccess());

   }

    @Test
    void testRetrieveAllTablesWithFilteredColumns() {
        int limit = 3;
        int offset = 0;
        TablesWithColumns result = paramTableService.retrieveAllTablesWithFilteredColumns(limit, offset);
        assertNotNull(result);
        assertEquals(limit, result.getAllTablesWithColumns().size());
        for (TableInfo filteredTable : result.getAllTablesWithColumns()) {
            for (ColumnInfo filteredColumn : filteredTable.getColumns()) {
                assertNotEquals("ACTIVE", filteredColumn.getName());
            }
        }
    }

@Test
    void testAllColumns(){
       String tableName= "actor";
      List<ColumnInfo> columns =paramTableService.getAllColumns(tableName);
       assertNotNull(columns);
    for (ColumnInfo column : columns) {
        assertNotNull(column.getName());
        assertNotNull(column.getType());
    }
}
@Test
    void testUpdateRequest(){
       String tableName="language";
       Map<String,String> instanceData = new HashMap<>();
       instanceData.put("language_id","1");
       instanceData.put("name","Guejmi");
       String username = "Kroos";
    UpdateRequest updateRequest = new UpdateRequest(instanceData,tableName,username);
    ResponseDto responseDto =paramTableService.addUpdateRequest(updateRequest);
    assertEquals("Update request validated and added successfully.",responseDto.getSuccess());
    ResponseDto response =paramTableService.addUpdateRequest(updateRequest);
    assertEquals("Update request already exists.",response.getError());
    response =paramTableService.cancelUpdateRequest("1",tableName);
    assertEquals("Update of 1 cancelled successfully",response.getSuccess());
    response =paramTableService.cancelUpdateRequest("3000",tableName);
    assertEquals("Request not found: 3000",response.getError());
    instanceData.replace("language_id","3000");
    ResponseDto error = paramTableService.addUpdateRequest(updateRequest);
    assertEquals("Validation failed, update request not added.",error.getError());

}

@Test
    void testSimulateUpdateRequest(){
    String tableName="language";
    Map<String,String> instanceData = new HashMap<>();
    instanceData.put("language_id","1");
    instanceData.put("name","Guejmi");
    String username = "Kroos";
    UpdateRequest updateRequest = new UpdateRequest(instanceData,tableName,username);
    boolean result= paramTableService.simulateUpdate(updateRequest);
    assertTrue(result);
    instanceData.replace("language_id","3000");
    updateRequest.setInstanceData(instanceData);
    result = paramTableService.simulateUpdate(updateRequest);
    assertFalse(result);
}
    @Test
    void testDeleteRequest(){
        String tableName="language";
        DeleteRequest deleteRequest = new DeleteRequest(tableName,"3","Marhoum");
        ResponseDto responseDto =paramTableService.addDeleteRequest(deleteRequest);
        assertEquals("INSTANCE ADDED TO DELETE 8 AM",responseDto.getSuccess());
        ResponseDto response =paramTableService.addDeleteRequest(deleteRequest);
        assertEquals("INSTANCE ALREADY EXISTS",response.getError());
        ResponseDto cancel =paramTableService.cancelDeleteRequest(tableName,"3");
        assertEquals("Deletion of 3 cancelled successfully",cancel.getSuccess());
        ResponseDto alreadycanceled =paramTableService.cancelDeleteRequest(tableName,"3");
        assertEquals("Request not found: 3",alreadycanceled.getError());


    }
    @Test
    void testSimulateDeleteRequest(){
        String tableName="language";
        DeleteRequest deleteRequest = new DeleteRequest(tableName,"3","Marhoum");
        boolean result =paramTableService.simulateDelete(deleteRequest);
        assertTrue(result);
        DeleteRequest deleteRequestfail = new DeleteRequest(tableName,"3000","T.Henry");
        result =paramTableService.simulateDelete(deleteRequestfail);
        assertFalse(result);
    }
    @Test
    void testGetDataFromTable(){
        String tableName ="country";
        TableDataRequest tableDataRequest = new TableDataRequest();
        List<String> columns = new ArrayList<>();
        columns.add("country_id");
        columns.add("country");
        tableDataRequest.setColumns(columns);
        tableDataRequest.setLimit(2);
        tableDataRequest.setOffset(1);
        tableDataRequest.setSearch("un");
        tableDataRequest.setSortOrder("asc");
        tableDataRequest.setSortByColumn("");
        DataFromTable data =paramTableService.getDataFromTable(tableName,tableDataRequest);
        Map<String,String> successdata  = new HashMap<>();
        successdata.put("country_id","2");
        successdata.put("country","United Kingdom");
        List<Map<String,String>> dataFromTablesuccess = new ArrayList<>();
        dataFromTablesuccess.add(successdata);
        assertEquals(dataFromTablesuccess,data.getData());
        columns.clear();
        data = paramTableService.getDataFromTable(tableName,tableDataRequest);
        assertTrue(data.getData().get(0).containsKey("last_update"));

    }
    @Test
    void testGetDataFromTable2(){
        String tableName ="country";
        TableDataRequest tableDataRequest = new TableDataRequest();
        List<String> columns = new ArrayList<>();
        tableDataRequest.setLimit(3);
        tableDataRequest.setOffset(0);
        tableDataRequest.setSortOrder("asc");
        tableDataRequest.setSortByColumn("country");
        tableDataRequest.setColumns(columns);
        DeleteRequest deleteRequest = new DeleteRequest(tableName,"3","OKOCHA");
        paramTableService.addDeleteRequest(deleteRequest);
        Map<String,String> updatedata  = new HashMap<>();
        updatedata.put("country_id","2");
        updatedata.put("country","Madagascar");
        UpdateRequest updateRequest = new UpdateRequest(updatedata,tableName,"Adeyemi");
        paramTableService.addUpdateRequest(updateRequest);
        DataFromTable data = paramTableService.getDataFromTable(tableName,tableDataRequest);
        assertTrue(data.getData().get(0).containsKey("last_update"));
        assertFalse(data.getDeleteRequests().isEmpty());
        assertFalse(data.getUpdateRequests().isEmpty());

    }
    @Test
    void testExecuteDeleteRequests(){
        DeleteRequest deleteRequest1 = new DeleteRequest("actor","1","AlPacino");
        DeleteRequest deleteRequest2 = new DeleteRequest("actor","2","AlPacino");
        paramTableService.addDeleteRequest(deleteRequest1);
        paramTableService.addDeleteRequest(deleteRequest2);
        assertEquals(2,paramTableService.getDeleteRequestByTable("actor").size());
       paramTableService.executeDeletion();
      assertEquals(0,paramTableService.getDeleteRequestByTable("actor").size());

    }

    @Test
    void testExecuteUpdate(){
        Map<String,String> instanceData= new HashMap<>();
        instanceData.put("actor_id","3");
        instanceData.put("first_name","Shaggy");
        UpdateRequest updateRequest1 = new UpdateRequest(instanceData,"actor","Samalapa");
        paramTableService.addUpdateRequest(updateRequest1);
        assertEquals(1,paramTableService.getUpdateRequestByTable("actor").size());
        paramTableService.executeUpdate();
        assertEquals(0,paramTableService.getUpdateRequestByTable("actor").size());
        List<ParamAudit> history = paramTableService.paramHistory("actor");
        assertEquals(1,history.size());
    }



}

