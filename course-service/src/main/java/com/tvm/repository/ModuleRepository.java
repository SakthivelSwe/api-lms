package com.tvm.repository;

import com.tvm.model.ModuleEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ModuleRepository extends JpaRepository<ModuleEntity, Long> {
    List<ModuleEntity> findByCourse_Id(Long courseId);
}
