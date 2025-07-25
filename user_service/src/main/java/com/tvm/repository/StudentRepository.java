package com.tvm.repository;

import com.tvm.model.StudentInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StudentRepository extends JpaRepository<StudentInfo,String> {

    @Query("SELECT s.studentId FROM StudentInfo s ORDER BY s.studentId DESC LIMIT 1")
    String findLastStudentId();

    Optional<StudentInfo> findByEmail(String email);
    Optional<StudentInfo> findByPassword(String password);

}

