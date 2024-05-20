package com.talan.adminmodule.repository;

import com.talan.adminmodule.entity.ParamAudit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;
@Repository
public interface ParamAuditRepository extends JpaRepository<ParamAudit, Integer> {
    @Query("SELECT COALESCE(MAX(p.version), 0) FROM ParamAudit p WHERE p.tableName = :tableName")
    Integer findMaxVersionByTableName(String tableName);
    List<ParamAudit>findByTableName(String tableName);
}
