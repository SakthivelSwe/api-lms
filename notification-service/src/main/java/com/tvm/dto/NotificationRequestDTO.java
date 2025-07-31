package com.tvm.dto;

public class NotificationRequestDTO {
    private String toEmail;
    private String subject;
    private String content;
    private String templateName;

    private String studentName;
    private String courseName;

    // Additional fields
    private String updateDetails;
    private String score;
    private String offerTitle;
    private String offerDetails;
    private String expiryDate;

    // Getters and Setters
    public String getToEmail() { return toEmail; }
    public void setToEmail(String toEmail) { this.toEmail = toEmail; }

    public String getSubject() { return subject; }
    public void setSubject(String subject) { this.subject = subject; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public String getTemplateName() { return templateName; }
    public void setTemplateName(String templateName) { this.templateName = templateName; }

    public String getStudentName() { return studentName; }
    public void setStudentName(String studentName) { this.studentName = studentName; }

    public String getCourseName() { return courseName; }
    public void setCourseName(String courseName) { this.courseName = courseName; }

    public String getUpdateDetails() { return updateDetails; }
    public void setUpdateDetails(String updateDetails) { this.updateDetails = updateDetails; }

    public String getScore() { return score; }
    public void setScore(String score) { this.score = score; }

    public String getOfferTitle() { return offerTitle; }
    public void setOfferTitle(String offerTitle) { this.offerTitle = offerTitle; }

    public String getOfferDetails() { return offerDetails; }
    public void setOfferDetails(String offerDetails) { this.offerDetails = offerDetails; }

    public String getExpiryDate() { return expiryDate; }
    public void setExpiryDate(String expiryDate) { this.expiryDate = expiryDate; }
}