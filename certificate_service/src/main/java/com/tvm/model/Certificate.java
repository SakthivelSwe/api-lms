package com.tvm.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;

@Entity
@Data
public class Certificate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String studentId;
    private String courseId;
    private LocalDate issueDate;

    @Lob
    @Column(columnDefinition = "LONGBLOB")
    private byte[] pdfData;  // Stores the PDF bytes
}
