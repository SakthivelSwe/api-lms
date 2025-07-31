package com.tvm.service;

import com.tvm.client.CourseServiceClient;
import com.tvm.client.ExamServiceClient;
import com.tvm.client.UserServiceClient;
import com.tvm.model.Certificate;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import com.tvm.repository.CertificateRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.xhtmlrenderer.pdf.ITextRenderer;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.List;

@Service
public class CertificateService{

    @Autowired
    private  CertificateRepository certificateRepository;

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private UserServiceClient userServiceClient;

    @Autowired
    private CourseServiceClient courseServiceClient;

    @Autowired
    private ExamServiceClient examServiceClient;

    //certificate generate -- step 1
    public Certificate generatePdf(String studentId, String courseId) {
        try {
            // 1. Fetch student name and course name
            String studentName = userServiceClient.getStudentName(studentId);
            String courseName = courseServiceClient.getCourseName(courseId);
            String studentEmail = userServiceClient.getEmailName(studentId);


            // 2. Load certificate template from resources
            InputStream inputStream = getClass().getClassLoader().getResourceAsStream("template/certificate-template.html");
            if (inputStream == null) {
                throw new FileNotFoundException("certificate-template not found in resources/template/");
            }

            String htmlContent = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);

            // 3. Replace placeholders in HTML
            htmlContent = htmlContent
                    .replace("{studentName}", studentName)
                    .replace("{courseName}", courseName)
                    .replace("{issueDate}", LocalDate.now().toString());

            // 4. Generate PDF from HTML
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            ITextRenderer renderer = new ITextRenderer();
            renderer.setDocumentFromString(htmlContent);
            renderer.layout();
            renderer.createPDF(outputStream);
            byte[] pdfBytes = outputStream.toByteArray();

            // 5. Create and save certificate
            Certificate certificate = new Certificate();
            certificate.setStudentId(studentId);
            certificate.setCourseId(courseId);
            certificate.setIssueDate(LocalDate.now());
            certificate.setPdfData(pdfBytes);


            sendEmailWithAttachment(
                    studentEmail,
                    "Course Completion Certificate - " + courseName,
                    "Hi " + studentName + ",\n\nCongrats on completing your course! Please find your certificate attached.",
                    pdfBytes,
                    studentName
            );

            return certificateRepository.save(certificate);

        } catch (Exception e) {
            throw new RuntimeException("Failed to generate and save certificate", e);
        }
    }

    public void sendEmailWithAttachment(String toEmail, String subject, String body, byte[] pdfData, String studentName) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setFrom("ganeshkumar22j@gmail.com");
            helper.setTo(toEmail);
            helper.setSubject(subject);
            helper.setText(body);

            // Attach PDF as a file (filename is created here)
            helper.addAttachment("Certificate_" + studentName + ".pdf", new ByteArrayResource(pdfData));

            mailSender.send(message);
            System.out.println("Email with certificate sent successfully");

        } catch (MessagingException e) {
            throw new RuntimeException("Failed to send email with attachment", e);
        }
    }

    //fetch all certificates
    public List<Certificate> allCertificate() {
        return certificateRepository.findAll();
    }
}
