package com.talan.adminmodule.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class DeleteRequest {
    private String tableName;
    private String primaryKeyValue;
    private String ussername;


}
