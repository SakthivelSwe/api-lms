package com.tvm.controller;

import com.tvm.dto.*;
import com.tvm.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/modules")
@PreAuthorize("hasRole('INSTRUCTOR')")
public class ModuleController {

    @Autowired
    private ModuleService moduleService;

    @PostMapping("/{courseId}/modules")
    public ResponseEntity<ModuleDTO> addModule(@PathVariable Long courseId, @RequestBody ModuleDTO dto) {
        return ResponseEntity.ok(moduleService.addModuleToCourse(courseId, dto));
    }

    @GetMapping("/{courseId}/modules")
    public ResponseEntity<List<ModuleDTO>> getModules(@PathVariable Long courseId) {
        return ResponseEntity.ok(moduleService.getModulesByCourse(courseId));
    }

//    @GetMapping
//    public ResponseEntity<List<ModuleResponseDTO>> getModules(@PathVariable Long courseId) {
//        List<ModuleResponseDTO> modules = moduleService.getModulesByCourse(courseId);
//        return ResponseEntity.ok(modules);
//    }
    @PutMapping("/{id}")
    public ResponseEntity<ModuleDTO> updateModule(@PathVariable Long id, @RequestBody ModuleDTO dto) {
        return ResponseEntity.ok(moduleService.updateModule(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteModule(@PathVariable Long id) {
        moduleService.deleteModule(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{courseId}")
    public ResponseEntity<ModuleDTO> createModule(
            @PathVariable Long courseId,
            @RequestBody ModuleDTO dto
    ) {
        ModuleDTO created = moduleService.addModuleToCourse(courseId, dto);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

}
