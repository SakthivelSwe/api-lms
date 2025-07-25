package com.tvm.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.persistence.Id;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Enrollment {

        @Id
        private String enrollmentId;

        private LocalDateTime enrollmentDate;

        private String status;

        @ManyToOne
        @JoinColumn(name = "student_id")
        private StudentInfo student;


        // add two
        // one is add course_id
        //another is add certificate_id

}