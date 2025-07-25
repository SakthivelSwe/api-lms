package com.tvm.DTO;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class EnrollDTO {

    private String enrollment_id;
    private LocalDateTime enrollment_date;
    private String status;
    private String student_id;

}
