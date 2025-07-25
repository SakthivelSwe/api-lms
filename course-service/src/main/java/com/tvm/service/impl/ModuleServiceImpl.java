package com.tvm.service.impl;

import com.tvm.dto.ModuleDTO;
import com.tvm.exception.ResourceNotFoundException;
import com.tvm.model.CourseEntity;
import com.tvm.model.ModuleEntity;
import com.tvm.repository.CourseRepository;
import com.tvm.repository.ModuleRepository;
import com.tvm.service.ModuleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ModuleServiceImpl implements ModuleService {

    @Autowired
    private ModuleRepository moduleRepository;

    @Autowired
    private CourseRepository courseRepository;

    @Override
    public ModuleDTO addModuleToCourse(Long courseId, ModuleDTO dto) {
        CourseEntity course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Course", "id", courseId));

        ModuleEntity module = new ModuleEntity();
        module.setModuleName(dto.getModuleName());
        module.setModuleDescription(dto.getModuleDescription());
        module.setVideoUrl(dto.getVideoUrl());
        module.setPdfUrl(dto.getPdfUrl());
        module.setCourse(course);

        ModuleEntity saved = moduleRepository.save(module);

        return new ModuleDTO(
                saved.getId(),
                saved.getModuleName(),
                saved.getModuleDescription(),
                saved.getVideoUrl(),
                saved.getPdfUrl(),
                courseId
        );
    }

    @Override
    public List<ModuleDTO> getModulesByCourse(Long courseId) {
        return moduleRepository.findByCourse_Id(courseId).stream()
                .map(m -> new ModuleDTO(m.getId(), m.getModuleName(), m.getModuleDescription(), m.getVideoUrl(), m.getPdfUrl(), courseId))
                .collect(Collectors.toList());
    }

    @Override
    public ModuleDTO updateModule(Long moduleId, ModuleDTO dto) {
        ModuleEntity module = moduleRepository.findById(moduleId)
                .orElseThrow(() -> new ResourceNotFoundException("Module", "id", moduleId));

        module.setModuleName(dto.getModuleName());
        module.setModuleDescription(dto.getModuleDescription());
        module.setVideoUrl(dto.getVideoUrl());
        module.setPdfUrl(dto.getPdfUrl());

        ModuleEntity updated = moduleRepository.save(module);
        return new ModuleDTO(updated.getId(), updated.getModuleName(), updated.getModuleDescription(), updated.getVideoUrl(), updated.getPdfUrl(), updated.getCourse().getId());
    }

    @Override
    public void deleteModule(Long moduleId) {
        moduleRepository.deleteById(moduleId);
    }
}
