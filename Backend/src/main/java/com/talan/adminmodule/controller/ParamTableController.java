package com.talan.adminmodule.controller;

import com.talan.adminmodule.dto.*;
import com.talan.adminmodule.entity.ParamAudit;
import com.talan.adminmodule.service.ParamTableService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.web.servletapi.SecurityContextHolderAwareRequestWrapper;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RequestMapping("/api/tables")
@RestController
@CrossOrigin(origins = "*")

public class ParamTableController {
    @Autowired
    private ParamTableService tableService;
    @Autowired
    private HttpServletRequest request;
    @GetMapping("/{limit}/{offset}")
    public TablesWithColumns retrieveAllTablesAndColumns(@PathVariable int limit, @PathVariable int offset) {
        return tableService.retrieveAllTablesWithFilteredColumns(limit,offset);
    }

    @GetMapping("/{tableName}")
    public DataFromTable getDataFromTable(
            @PathVariable String tableName,
            @ModelAttribute TableDataRequest request
    ) {
        return tableService.getDataFromTable(
                tableName,
                request
        );
    }
    @PostMapping("/cancelupdate/{tableName}/{primaryKeyValue}")
    public ResponseDto cancelupdaterequest(@PathVariable String primaryKeyValue,
                                           @PathVariable String tableName){
        return tableService.cancelUpdateRequest(primaryKeyValue,tableName);
    }


   @PutMapping("/update/{tableName}")
   public ResponseDto updateInstance(@RequestBody Map<String, String> instanceData,
                                     @PathVariable String tableName) {
       SecurityContextHolderAwareRequestWrapper requestWrapper = new SecurityContextHolderAwareRequestWrapper(request, "ROLE_");
       String username = requestWrapper.getRemoteUser();
       UpdateRequest updateRequest = new UpdateRequest(instanceData,tableName,username);

       return tableService.addUpdateRequest(updateRequest);

   }
    @PostMapping("/{tableName}")
    public ResponseDto addInstance(@RequestBody Map<String, String> instanceData,
                                   @PathVariable("tableName") String tableName) {

        return tableService.addInstance(instanceData, tableName);

    }
@PostMapping("/{tableName}/delete/{primaryKeyValue}")
public ResponseDto deleteRecord(@PathVariable String tableName, @PathVariable String primaryKeyValue) {
    SecurityContextHolderAwareRequestWrapper requestWrapper = new SecurityContextHolderAwareRequestWrapper(request, "ROLE_");
    String username = requestWrapper.getRemoteUser();
    DeleteRequest deleteRequest = new DeleteRequest(tableName, primaryKeyValue,username);

    return tableService.addDeleteRequest(deleteRequest);

}
    @PostMapping("/{tableName}/canceldeletion/{primaryKeyValue}")
public ResponseDto canceldeleterequest(@PathVariable String tableName, @PathVariable String primaryKeyValue){
        return tableService.cancelDeleteRequest(tableName,primaryKeyValue);
    }
    @GetMapping("/{tableName}/history")
       public List<ParamAudit> paramHistory(@PathVariable String tableName){
        return tableService.paramHistory(tableName);
    }


        }
