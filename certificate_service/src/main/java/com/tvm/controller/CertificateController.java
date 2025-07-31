package com.tvm.controller;

import com.tvm.client.ExamServiceClient;
import com.tvm.client.UserServiceClient;
import com.tvm.model.Certificate;
import com.tvm.repository.CertificateRepository;
import com.tvm.service.CertificateService;
import feign.FeignException;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController

public class CertificateController {

    @Autowired
    private  UserServiceClient userServiceClient;

    @Autowired
    private  CertificateService certificateService;

    @Autowired
    CertificateRepository certificateRepository;

    @Autowired
    private ExamServiceClient examServiceClient;

    //generate certificate -- only for admin
    @PostMapping("/generate/{studentId}/{courseId}")
    public ResponseEntity<?> generateCertificate(
            @PathVariable String studentId,
            @PathVariable String courseId) {

        String status;

        try {
            status = userServiceClient.getEnrollmentStatus(studentId, courseId);
            System.out.println(status);
        } catch (FeignException.NotFound e) {
            return ResponseEntity.badRequest().body("Enrollment not found.");
        }

        //check whether the course is finished or not in user service (in enrollment table)
        if (!"FINISHED".equalsIgnoreCase(status)) {
            return ResponseEntity.badRequest().body("Student has not completed the course.");
        }

        //check whether the student passed in the exam or not in exam service
        Boolean passed = examServiceClient.hasPassedExam(studentId, courseId);
        if (!Boolean.TRUE.equals(passed)) {
            return ResponseEntity.badRequest().body("Student has not passed the exam.");
        }

        Certificate cert = certificateService.generatePdf(studentId, courseId);
        return ResponseEntity.ok(cert);
    }

    //to view certificate by id
    @GetMapping("/preview/{id}")
    public void previewCertificate(@PathVariable Long id, HttpServletResponse response) throws IOException {
        Certificate cert = certificateRepository.findById(id).orElseThrow();

        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "inline; filename=certificate.pdf");
        response.getOutputStream().write(cert.getPdfData());
        response.getOutputStream().flush();
    }

    //to view all certificates -- used for both admin and instructor
    @GetMapping("/view")
    public List<Certificate> allEnroll(){
        return certificateService.allCertificate();
    }

}
