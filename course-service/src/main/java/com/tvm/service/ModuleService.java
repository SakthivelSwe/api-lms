package com.tvm.service;

import com.tvm.dto.ModuleDTO;

import java.util.List;

public interface ModuleService {

    ModuleDTO addModuleToCourse(Long courseId, ModuleDTO moduleDTO);

    List<ModuleDTO> getModulesByCourse(Long courseId);

    ModuleDTO updateModule(Long moduleId, ModuleDTO moduleDTO);

    void deleteModule(Long moduleId);
}
