package com.tvm.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ModuleDTO {
    private Long id;
    private String moduleName;
    private String moduleDescription;
    private String videoUrl;
    private String pdfUrl;
    private Long courseId;
}
