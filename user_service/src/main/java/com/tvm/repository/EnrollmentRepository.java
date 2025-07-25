package com.tvm.repository;

import com.tvm.model.Enrollment;
import com.tvm.model.StudentInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EnrollmentRepository extends JpaRepository<Enrollment, String> {

    @Query("SELECT e.enrollmentId FROM Enrollment e ORDER BY e.enrollmentId DESC LIMIT 1")
    String findLastEnrollId();

    Optional<Enrollment> findByStudent_StudentId(String studentId);
}
