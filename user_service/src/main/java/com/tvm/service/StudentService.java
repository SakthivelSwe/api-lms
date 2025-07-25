package com.tvm.service;

import com.tvm.DTO.*;
import com.tvm.model.Enrollment;
import com.tvm.model.StudentInfo;
import com.tvm.repository.EnrollmentRepository;
import com.tvm.repository.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class StudentService {

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private EnrollmentRepository enrollRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    //register student
    public String registerStudent(StudentDTO dto) {

        //used to get last student id
        String lastId = studentRepository.findLastStudentId();
        int nextIdNumber = 1;

        if (lastId != null && lastId.startsWith("stu")) {
            String numberPart = lastId.substring(3);
            try {
                nextIdNumber = Integer.parseInt(numberPart) + 1;
            } catch (NumberFormatException e) {
                nextIdNumber = 1;
            }
        }
        String newStudentId = "stu" + nextIdNumber;

        StudentInfo student = new StudentInfo();
        student.setStudentId(newStudentId);
        student.setStudentName(dto.getStudentName());
        student.setPassword(dto.getPassword());
        student.setEmail(dto.getEmail());
        student.setPhone_number(dto.getPhone_number());
        student.setGender(dto.getGender());
        student.setDob(dto.getDob());

        studentRepository.save(student); // saves both due to cascade

        return "Student registered with ID: " + newStudentId;
    }

    //fetch student details -- STEP 2
    public StudentDTO mapToStudentDTO(StudentInfo student) {
        StudentDTO dto = new StudentDTO();
        dto.setStudentId(student.getStudentId());
        dto.setStudentName(student.getStudentName());
        dto.setEmail(student.getEmail());
        dto.setPassword(student.getPassword());
        dto.setPhone_number(student.getPhone_number());
        dto.setGender(student.getGender());
        dto.setDob(student.getDob());
        dto.setStatus(student.getStatus());

        return dto;
    }

    //fetch student details -- STEP 1
    public Optional<StudentInfo> getStudent(String id) {
        return studentRepository.findById(id);
    }

    //fetch enroll details -- STEP - 2
    public EnrollDTO mapToEnrollDTO(Enrollment enrollment)
    {
        EnrollDTO dto = new EnrollDTO();
        dto.setEnrollment_id(enrollment.getEnrollmentId());
        dto.setStudent_id(enrollment.getStudent().getStudentId());
        dto.setEnrollment_date(enrollment.getEnrollmentDate());
        dto.setStatus(enrollment.getStatus());

        return dto;
    }

    //fetch enroll details -- STEP : 1
    public Optional<Enrollment> getEnrollStu(String id) {
        return enrollRepository.findByStudent_StudentId(id);
    }

    //login check for student/instructor
    public String login(LoginDTO dto) {

        // First, check in student table
        Optional<StudentInfo> optionalStudent = studentRepository.findByEmail(dto.getEmail());

        if (optionalStudent.isPresent()) {
            StudentInfo student = optionalStudent.get();
            if (student.getPassword().equals(dto.getPassword())) {
                return "Login Success (Student)";
            }
        }

        // If not found in student, check in instructor table manually using JdbcTemplate
        String sql = "SELECT COUNT(*) FROM instructor_info WHERE email = ? AND password = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, dto.getEmail(), dto.getPassword());

        System.out.println("hello "+ count);
        if (count != null && count > 0) {
            return "Login Success (Instructor)";
        }

        return "Invalid Email or Password";
    }

    //update details for student
    public void stuUpd(String id, StudentUpdateDTO studentUpdDTO) {
        StudentInfo student=studentRepository.findById(id).
                orElseThrow(()->new RuntimeException("Student not found"));
        student.setEmail(studentUpdDTO.getEmail());
        student.setPassword(studentUpdDTO.getPassword());
        student.setPhone_number(studentUpdDTO.getPhone_number());
        studentRepository.save(student);
    }

    //delete student details by id
    public String delStu(String id) {
        boolean result = studentRepository.existsById(id);

        if (!result) {
            return "Student not found";
        }

        studentRepository.deleteById(id);
        return "Student deleted successfully";
    }

    //update details for instructor by using JDBCTemplate
    public String instUpd(String id, InstructorUpdateDTO instructorUpdateDTO) {
        String sql = "UPDATE instructor_info SET email = ?, password = ?, phone_number = ?, experience = ? WHERE instructor_id = ?";

        int count = jdbcTemplate.update(
                sql,
                instructorUpdateDTO.getEmail(),
                instructorUpdateDTO.getPassword(),
                instructorUpdateDTO.getPhone_number(),
                instructorUpdateDTO.getExperience(),
                id
        );

        if (count > 0) {
            return "Instructor updated successfully";
        }
        return "Instructor not found";
    }

    //delete instructor details by using JDBCTemplate
    public String delInst(String id) {

        String sql="DELETE FROM instructor_info WHERE instructor_id=?";
        int count=jdbcTemplate.update(sql,id);

        if(count>0){
            return "Instructor deleted successfully";
        }
        return "Instructor not found";
    }

    //stored data in enrollment
    public String enrollCourse(String stuId, String courseId) {
        String sql = "select status from student_info where student_id=?";
        String status = jdbcTemplate.queryForObject(sql, String.class, stuId);

        if (!"ACTIVE".equalsIgnoreCase(status)) {
            return "Enrollment failed: Student is not approved";
        }

        // used to get last enrollment id
        String lastEnrollId = enrollRepository.findLastEnrollId();
        int nextEnrollId = 1;

        if (lastEnrollId != null && lastEnrollId.startsWith("enr")) {
            String num = lastEnrollId.substring(3);
            try {
                nextEnrollId = Integer.parseInt(num) + 1;
            } catch (NumberFormatException e) {
                nextEnrollId = 1;
            }
        }

        String newEnrollId = "enr" + nextEnrollId;

        LocalDateTime localDateTime = LocalDateTime.now();

        // Insert into enrollment table
        String insert = "insert into enrollment(enrollment_id, enrollment_date, student_id, status) values (?, ?, ?, ?)";
        jdbcTemplate.update(insert, newEnrollId, localDateTime, stuId, "PENDING"); // ✅ correct order

        return "Enrolled successfully";
    }

    //fetch instructor by id
    public Map<String, Object> instGet(String id) {
        String sql = "select * from instructor_info where instructor_id=?";
        return jdbcTemplate.queryForMap(sql, id);
    }

    //fetch all student
    public List<StudentInfo> getAllStu() {
        return studentRepository.findAll();
    }

    //fetch all available enrolled
    public List<Enrollment> getAllEnroll() {
        return enrollRepository.findAll();
    }

    //fetch enrolled student by using student id
    public Optional<Enrollment> getEnroll(String id) {
        return enrollRepository.findByStudent_StudentId(id);
    }

    //update student status -- for admin
    public String updStatus(String id, String status) {

        String sql = "SELECT status FROM student_info WHERE student_id = ?";
        String oldStatus = jdbcTemplate.queryForObject(sql, String.class, id);
        System.out.println("Status from DB: " + oldStatus);

        String sql1 = "UPDATE student_info SET status= ? WHERE student_id = ?";
        int count = jdbcTemplate.update(sql1,status,id);

        if (count > 0) {
            return "Student status successfully: "+oldStatus+" to "+status;
        }
        return "Student not found";
    }

    //update enrolled student status -- for admin
    public String updEnrStatus(String id, String status) {

        String sql = "SELECT status FROM enrollment WHERE enrollment_id = ?";
        String oldStatus = jdbcTemplate.queryForObject(sql, String.class, id);
        System.out.println("Status from DB: " + oldStatus);

        String sql1 = "UPDATE enrollment SET status= ? WHERE enrollment_id = ?";
        int count = jdbcTemplate.update(sql1,status,id);

        if (count > 0) {
            return "Enrollment status successfully: "+oldStatus+" to "+status;
        }
        return "Enrollment Id not found";
    }

}
