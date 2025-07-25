package com.tvm.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class StudentInfo {

    @Id
    private String studentId;
    private String studentName;
    private String email;
    private String password;
    private String gender;
    private String phone_number;
    private String dob;
    private String status="INACTIVE";

    @OneToMany(mappedBy = "student", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<Enrollment> enrollment;
}
