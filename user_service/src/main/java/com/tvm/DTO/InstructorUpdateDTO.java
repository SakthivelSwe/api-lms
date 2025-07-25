package com.tvm.DTO;

import lombok.Data;

@Data
public class InstructorUpdateDTO {

    private String email;
    private String password;
    private String phone_number;
    private int experience;
}