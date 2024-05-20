package com.talan.adminmodule.service;

import com.talan.adminmodule.config.DatabaseInitializer;
import com.talan.adminmodule.dto.*;
import com.talan.adminmodule.dto.ColumnInfo;
import com.talan.adminmodule.entity.ParamAudit;
import com.talan.adminmodule.repository.ParamAuditRepository;
import jakarta.annotation.PostConstruct;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Scheduled;

import org.springframework.security.web.servletapi.SecurityContextHolderAwareRequestWrapper;
import org.springframework.stereotype.Service;
import javax.sql.DataSource;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

@Service
public class ParamTableService {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private DatabaseInitializer databaseInitializer;
@Autowired
    private ParamAuditRepository paramAuditRepository;
    @Autowired
    private HttpServletRequest request;
    @Autowired
    private PlatformTransactionManager transactionManager;

    private static final Logger LOGGER = LoggerFactory.getLogger(ParamTableService.class);

    private TablesWithColumns allTablesWithColumns =new TablesWithColumns();

    public static final String ACTIVE ="active";
    List<UpdateRequest>updateRequests = new ArrayList<>();
     List<DeleteRequest> deleteRequests =new ArrayList<>();
    @PostConstruct
    public void initialize() {
        allTablesWithColumns = databaseInitializer.getAllTablesWithColumns();
    }
    public ParamTableService(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

//Tjib l tablesNumber mel allTablesWithColumns f initializer fiha structure DB baaed iteration fel filtered l request hedhi
    // + filtre column ACTIVE t set listcolumns jdida f TableInfo
    public TablesWithColumns retrieveAllTablesWithFilteredColumns(int limit, int offset) {
        List<TableInfo> paginatedTables = new ArrayList<>();
        TablesWithColumns tablesWithColumns = new TablesWithColumns();
        int endIndex = (int) Math.min(allTablesWithColumns.getNumberTables(), offset + (long)limit);


        for (int i = offset; i < endIndex; i++) {
            TableInfo paramTable = allTablesWithColumns.getAllTablesWithColumns().get(i);
            List<ColumnInfo> columns = paramTable.getColumns();
            List<ColumnInfo> filteredColumns =  columns.stream().filter(column->!column.getName().equals(ACTIVE)).toList();
            paramTable.setColumns(filteredColumns);
            paginatedTables.add(paramTable);
        }
        tablesWithColumns.setAllTablesWithColumns(paginatedTables);
        tablesWithColumns.setNumberTables(allTablesWithColumns.getNumberTables());
        return tablesWithColumns;
    }

// ALL COLUMNS BEL ACTIVE !!!
    public List<ColumnInfo> getAllColumns(String tableName) {
        TableInfo tableInfoOptional=allTablesWithColumns.getAllTablesWithColumns().stream()
                .filter(table -> table.getName().equals(tableName))
                .findFirst().orElse(null);
        List<ColumnInfo> alltablecolumns=new ArrayList<>();
        if (tableInfoOptional!=null){
            alltablecolumns  = tableInfoOptional.getColumns();
        }
       return alltablecolumns;
    }

    //BUILD l query w baaed executi w l map eli bech return KOL ROW converted to string aala le types par exemple table (NO TOSTRING)
    // iteration aal list updateRequest w DelteRequest ken fama idrequete== rowid (scheduled for deletion/edition)
    public DataFromTable getDataFromTable(String tableName,TableDataRequest request) {
        StringBuilder sqlQuery = buildSqlQuery(tableName, request);
        LOGGER.debug("Executing SQL: {}", sqlQuery);
        DataFromTable dataFromTable =new DataFromTable();
     List<String> deletedRequestsData =new ArrayList<>();
     List<String> updatedRequestsData =new ArrayList<>();
     dataFromTable.setData (jdbcTemplate.queryForList(sqlQuery.toString()));
        System.out.println("QUERY "+sqlQuery);


        for (Map<String, Object> row : dataFromTable.getData()) {
            String primaryKeyValue = row.get(primaryKeyDetails(tableName).getName()).toString();
            for (DeleteRequest deleteRequest : getDeleteRequestByTable(tableName)) {
                if (primaryKeyValue.equals(deleteRequest.getPrimaryKeyValue())) {
                    deletedRequestsData.add(deleteRequest.getPrimaryKeyValue());
                }
            }
            for (UpdateRequest updateRequest : getUpdateRequestByTable(tableName)) {
                if (primaryKeyValue.equals(updateRequest.getInstanceData().get(primaryKeyDetails(tableName).getName()))) {
                    updatedRequestsData.add(updateRequest.getInstanceData().get(primaryKeyDetails(tableName).getName()));
                }
            }
        }
        dataFromTable.setDeleteRequests(deletedRequestsData);
        dataFromTable.setUpdateRequests(updatedRequestsData);
      return  dataFromTable;

    }
    public StringBuilder buildSqlQuery(String tableName, TableDataRequest request) {
        StringBuilder sqlQuery = new StringBuilder();
        sqlQuery.append(buildSelectClause(tableName, request))
                .append(" FROM ").append(tableName)
                .append(" WHERE ").append(tableName).append(".active = 'true'");

        if (request.getSearch() != null && !request.getSearch().isEmpty() && !request.getSearch().equals("undefined") && !request.getColumns().isEmpty()) {
            sqlQuery.append(" AND (")
                     .append(request.getColumns().stream()
                    .map(column -> "LOWER ( CAST ("+column+" AS TEXT ) )" + " LIKE  '%"+request.getSearch().toLowerCase()+"%'")
                    .collect(Collectors.joining(" OR ")));
            sqlQuery.append(")");
        }

        sqlQuery.append(orderByClause(tableName, request))
                .append(limitOffsetClause(request));
        return sqlQuery;
    }

    //STEP 1 SQLREQUEST FETCH DATA SELECT IF COLUMNS EMPTY APPEND ALL SELECTCOLUMNS
    public StringBuilder buildSelectClause(String tableName, TableDataRequest request) {
        StringBuilder selectClause = new StringBuilder("SELECT ");
        List<String> columns = getAllColumns(tableName).stream().filter(Objects::nonNull)
                .map(ColumnInfo::getName)
                .filter(column -> !column.contains(ACTIVE))
                .toList();
        String pk = primaryKeyDetails(tableName).getName();
        if (request.getColumns().isEmpty()) {
            appendAllColumns(selectClause, columns,pk);
        } else {
            appendSpecifiedColumns(selectClause, columns,pk, request);
        }
        if (selectClause.charAt(selectClause.length() - 1) == ' ') {
            selectClause.delete(selectClause.length() - 2, selectClause.length());
        }
        return selectClause;

    }

    public void appendAllColumns(StringBuilder selectClause, List<String> columns, String pk) {
        for (String column : columns) {
            selectClause.append(column).append(", ");

        }

    }

    public void appendSpecifiedColumns(StringBuilder selectClause, List<String> columns,String pk, TableDataRequest request) {
        for (String column : request.getColumns()) {
            if (columns.contains(column)) {
                selectClause.append(column).append(", ");
            }
        }
    }



    public String orderByClause(String tableName, TableDataRequest request) {
        String sortByColumn = request.getSortByColumn() != null && !request.getSortByColumn().equals("null") && !request.getSortByColumn().isEmpty()
                ? request.getSortByColumn()
                : primaryKeyDetails(tableName).getName();
        String order= request.getSortOrder()!=null &&!request.getSortOrder().equals("null") &&!request.getSortOrder().isEmpty()&& sortByColumn.equals(request.getSortByColumn()) ? request.getSortOrder() :" " ;
        return " ORDER BY " + sortByColumn +" "+order;
    }


    public String limitOffsetClause(TableDataRequest request) {
        StringBuilder limitOffsetClause = new StringBuilder();
        if (request.getLimit() > 0) {
            limitOffsetClause.append(" LIMIT ").append(request.getLimit());
            if (request.getOffset() > 0) {
                limitOffsetClause.append(" OFFSET ").append(request.getOffset());
            }
        }
        return limitOffsetClause.toString();
    }




    //ADD ITERATION COLUMNS M STRUCTURE IF COLUMN ==key f instance data  affectation w !primaryKey
    // ==> convert type instance datavalue l column type +column+ placeholder l vel value +value
    public ResponseDto addInstance(Map<String, String> instanceData, String tableName) {
        List<ColumnInfo> allColumns = getAllColumns(tableName);
        StringBuilder columns = new StringBuilder();
        StringBuilder values = new StringBuilder();
        List<Object> params = new ArrayList<>();

        for (ColumnInfo column : allColumns) {
            if (!column.getName().equalsIgnoreCase(primaryKeyDetails(tableName).getName())) {
                String dataValue = instanceData.get(column.getName());

                params.add(convertToDataType(dataValue, column.getType()));
                columns.append(column.getName()).append(",");
                values.append("?,");
            }
        }

        if (!columns.toString().isEmpty()) {
            columns.deleteCharAt(columns.length() - 1);
            values.deleteCharAt(values.length() - 1);
        }
        String sql = String.format("INSERT INTO %s (%s) VALUES (%s)", tableName, columns, values);
        jdbcTemplate.update(sql, params.toArray());

        String username = getUsernameFromSecurityContext();
        Integer version = findMaxVersionByTableName(tableName) + 1;
        Map<String, String> cleanmap = instanceData.entrySet().stream().filter(value -> !(value.getValue().isEmpty())&&!(value.getValue().equals("undefined"))) .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        String auditRow = cleanmap.toString().substring(1, cleanmap.toString().length()-1);
        ParamAudit audit = ParamAudit.constructForInsertion(tableName, "ADDED", version,auditRow, username);
        paramAuditRepository.save(audit);
ResponseDto responseDto = new ResponseDto();
        responseDto.setSuccess("Record added successfully");
        return responseDto;
    }

    public String getUsernameFromSecurityContext() {
        SecurityContextHolderAwareRequestWrapper requestWrapper = new SecurityContextHolderAwareRequestWrapper(request, "ROLE_");
        return requestWrapper.getRemoteUser();
    }


public ResponseDto addUpdateRequest(UpdateRequest updateRequest) {
    ResponseDto responseDto = new ResponseDto();

    boolean exists = updateRequests.stream()
            .anyMatch(req -> req.getTableName().equals(updateRequest.getTableName()) &&
                    req.getInstanceData().equals(updateRequest.getInstanceData()));
    if (!exists) {
        if (simulateUpdate(updateRequest)) {

                updateRequests.add(updateRequest);

            responseDto.setSuccess("Update request validated and added successfully.");
        } else {
            responseDto.setError("Validation failed, update request not added.");
        }
    } else {
        responseDto.setError("Update request already exists.");
    }

    return responseDto;
}
    public ResponseDto cancelUpdateRequest(String primaryKeyValue , String tableName) {
        ResponseDto responseDto = new ResponseDto();
         String primaryKey= primaryKeyDetails(tableName).getName();
        boolean requestFound = false;
        for (UpdateRequest update : updateRequests) {
            if (update.getTableName().equals(tableName) &&
                    update.getInstanceData().get(primaryKey).equals(primaryKeyValue)) {
                int size = updateRequests.size();
                updateRequests.remove(update);
                int newsize = updateRequests.size();

                if (size != newsize) {
                    responseDto.setSuccess("Update of " + primaryKeyValue + " cancelled successfully");
                } else {
                    responseDto.setError("Update of " + primaryKeyValue + " not cancelled");
                }
                requestFound = true;
                break;
            }
        }
        if (!requestFound) {
            responseDto.setError("Request not found: " + primaryKeyValue);
        }

        return responseDto;
    }
  public  List<UpdateRequest> getUpdateRequestByTable(String tableName){
        return updateRequests.stream()
                .filter(updateRequest -> updateRequest.getTableName().equals(tableName)).toList();
    }
   public List<DeleteRequest> getDeleteRequestByTable(String tableName){
        return deleteRequests.stream()
                .filter(deleteRequest -> deleteRequest.getTableName().equals(tableName)).toList();
    }
        public ResponseDto updateInstance (UpdateRequest updateRequest,Integer version){
            ResponseDto responseDto=new ResponseDto();

                List<ColumnInfo> allColumns = getAllColumns(updateRequest.getTableName());
                StringBuilder setClause = new StringBuilder();
                StringBuilder sqlQuery = new StringBuilder();

                List<Object> params = new ArrayList<>();

                String primaryKeyColumn = primaryKeyDetails(updateRequest.getTableName()).getName();
                sqlQuery.append("UPDATE ")
                        .append(updateRequest.getTableName()) ;


                Object primaryKeyValue = new Object();
                for (ColumnInfo columnMap : allColumns) {
                    String columnName = columnMap.getName();
                    String columnType = columnMap.getType();

                    if (updateRequest.getInstanceData().containsKey(columnName)) {
                        Object convertedValue = convertToDataType(updateRequest.getInstanceData().get(columnName), columnType);


                        if (!columnName.equals(primaryKeyColumn)) {
                            setClause.append(columnName).append(" = ?, ");
                            params.add(convertedValue);
                        } else {
                            primaryKeyValue = convertedValue;
                        }
                    }
                }
                setClause.delete(setClause.length() - 2, setClause.length());
                sqlQuery.append(" SET ")
                        .append(setClause)
                        .append(" WHERE ")
                        .append(primaryKeyColumn);
                if (primaryKeyValue!=null){
                    sqlQuery.append(" = ")
                            .append(primaryKeyValue);
                }

                int rowsUpdated = jdbcTemplate.update(sqlQuery.toString(), params.toArray());
                if (rowsUpdated > 0 && primaryKeyValue!=null) {
                    responseDto.setSuccess("Instance updated successfully");
                    Map<String, String> cleanmap = updateRequest.getInstanceData().entrySet().stream().filter(value -> !(value.getValue().isEmpty())&&!(value.getValue().equals("undefined"))) .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
                    String auditRow = cleanmap.toString().substring(1, cleanmap.toString().length()-1);
                ParamAudit paramAudit = ParamAudit.constructForUpdate(updateRequest.getTableName(), primaryKeyValue.toString(), auditRow, "EDITED", version, updateRequest.getUsername());

                paramAuditRepository.save(paramAudit);

                } else {
                    responseDto.setError( "message No records updated");
                }



            return responseDto;
        }
        public boolean simulateDelete(DeleteRequest request){
        boolean result=false;
        Integer version =5;
            DefaultTransactionDefinition def = new DefaultTransactionDefinition();
            def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
            def.setName("simulateDeleteTransaction");
            TransactionStatus status = transactionManager.getTransaction(def);
            try { ResponseDto responseDto=deleteInstance(request,version);
                if (responseDto.getSuccess()!=null){
                    result=true;}
                return result;
            }   finally {
                transactionManager.rollback(status);
            }
        }
    public boolean simulateUpdate(UpdateRequest updateRequest) {
        boolean result=false;
        Integer version=5;
        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
        def.setName("simulateUpdateTransaction");

        TransactionStatus status = transactionManager.getTransaction(def);
try { ResponseDto responseDto=updateInstance(updateRequest,version);
    if (responseDto.getSuccess()!=null){
        result=true;}
    return result;
    }   finally {
        transactionManager.rollback(status);
        }
    }

    public ResponseDto addDeleteRequest(DeleteRequest deleteRequest) {
        ResponseDto responseDto = new ResponseDto();
        if (deleteRequests.stream().noneMatch(req ->
                req.getTableName().equals(deleteRequest.getTableName()) &&
                        req.getPrimaryKeyValue().equals(deleteRequest.getPrimaryKeyValue()))&&simulateDelete(deleteRequest)){
            deleteRequests.add(deleteRequest);
            responseDto.setSuccess("INSTANCE ADDED TO DELETE 8 AM");
        }
        else {
            responseDto.setError("INSTANCE ALREADY EXISTS");
        }
        return responseDto ;
    }
    public ResponseDto cancelDeleteRequest(String tableName, String primaryKeyValue) {
        ResponseDto responseDto = new ResponseDto();
        boolean requestFound = false;

        for (DeleteRequest delete : deleteRequests) {
            if (delete.getTableName().equals(tableName) &&
                    delete.getPrimaryKeyValue().equals(primaryKeyValue)) {
                int size = deleteRequests.size();
                deleteRequests.remove(delete);
                int newsize = deleteRequests.size();

                if (size != newsize) {
                    responseDto.setSuccess("Deletion of " + primaryKeyValue + " cancelled successfully");
                } else {
                    responseDto.setError("Deletion of " + primaryKeyValue + " not cancelled");
                }
                requestFound = true;
                break;
            }
        }
        if (!requestFound) {
            responseDto.setError("Request not found: " + primaryKeyValue);
        }

        return responseDto;
    }
    @Transactional
    @Scheduled(cron = "0 56 21 * * *")
    public void executeUpdate() {
        List<String> uniqueTableNames = new ArrayList<>();
        for (UpdateRequest updateRequest : updateRequests) {
            if (!uniqueTableNames.contains(updateRequest.getTableName())) {
                uniqueTableNames.add(updateRequest.getTableName());
            }
        }

        for (String uniqueTableName : uniqueTableNames) {
            List<UpdateRequest> tableUpdateRequests = getUpdateRequestByTable(uniqueTableName);
            Integer version = findMaxVersionByTableName(uniqueTableName) + 1;
            for (UpdateRequest tableupdateRequest : tableUpdateRequests) {

              ResponseDto  responseDto = updateInstance(tableupdateRequest, version);
                if (responseDto.getSuccess() != null) {
                    updateRequests.remove(tableupdateRequest);
                } else break;
            }

        }
    }
    @Transactional
   @Scheduled(cron = "0 56 21 * * *")
    public void executeDeletion () {
                List<String> uniqueTableNames = new ArrayList<>();
        for (DeleteRequest deleteRequest : deleteRequests) {
                    if (!uniqueTableNames.contains(deleteRequest.getTableName())) {
                        uniqueTableNames.add(deleteRequest.getTableName());
                    }
                }
               for (String uniqueTableName : uniqueTableNames){
                List <DeleteRequest>tableDeletedRequests = getDeleteRequestByTable(uniqueTableName);
                Integer version=findMaxVersionByTableName(uniqueTableName)+1;

                 for (DeleteRequest tableDeletedRequest:tableDeletedRequests){
                   ResponseDto responseDto = deleteInstance(tableDeletedRequest,version);
                   if (responseDto.getSuccess()!=null){
                       deleteRequests.remove(tableDeletedRequest);
                   }else break;

                }}


        }
   public ResponseDto deleteInstance(DeleteRequest deleteRequest,Integer version){
       ResponseDto responseDto = new ResponseDto();
        int rowsUpdated = 0;
       String inputValue = deleteRequest.getPrimaryKeyValue();
       String primaryKeyColumn = primaryKeyDetails(deleteRequest.getTableName()).getName();
       String primaryKeyColumnType = primaryKeyDetails(deleteRequest.getTableName()).getType();
       Object primaryKeyValue = convertToDataType(inputValue, primaryKeyColumnType);
       StringBuilder sqlQuery;
       sqlQuery = new StringBuilder();
       sqlQuery.append("UPDATE ")
               .append(deleteRequest.getTableName())
               .append(" SET active = false WHERE ")
               .append(primaryKeyColumn)
               .append(" = ?");

       rowsUpdated =jdbcTemplate.update(sqlQuery.toString(), primaryKeyValue);

       if (rowsUpdated > 0) {
           responseDto.setSuccess("Record updated successfully ");
           deleteRequests.remove(deleteRequest);

           ParamAudit paramAudit = ParamAudit.constructForDeletion(deleteRequest.getTableName(),inputValue,"DELETED",version,deleteRequest.getUssername());
           paramAuditRepository.save(paramAudit);
       }
       return responseDto;
   }

         public Integer findMaxVersionByTableName(String tableName){
        return paramAuditRepository.findMaxVersionByTableName(tableName);
         }
        public Object convertToDataType (String inputValue, String columnType){

            Object convertedValue;

                convertedValue = switch (columnType.toLowerCase()) {
                    case "int8", "bigint", "bigserial", "serial" -> Long.parseLong(inputValue);
                    case "int", "integer", "smallint" -> Integer.parseInt(inputValue);
                    case "varchar", "text", "bpchar" -> inputValue;
                    case "timestamptz" -> {
                        LocalDateTime dateTime;
                        if (inputValue.isEmpty()||inputValue.equals("null") ||inputValue.equals("undefined")) {
                            dateTime = LocalDateTime.now();
                        } else {
                            dateTime = LocalDateTime.parse(inputValue, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
                        }
                        yield Timestamp.valueOf(dateTime);
                    }

                    default -> inputValue;
                };

            return convertedValue;
        }

    public ColumnInfo primaryKeyDetails(String tableName) {
       Optional <ColumnInfo> optionalPk= allTablesWithColumns.getAllTablesWithColumns().stream()
                .filter(table -> table.getName().equals(tableName))
                .findFirst()
                .map(TableInfo::getPk);
        return optionalPk.orElseThrow(() -> new IllegalStateException("Primary key not found for table: " + tableName));

    }
        public List<ParamAudit> paramHistory(String tableName){
        return paramAuditRepository.findByTableName(tableName);
        }

    }
