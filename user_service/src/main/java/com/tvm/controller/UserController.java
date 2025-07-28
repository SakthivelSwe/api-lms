package com.tvm.controller;

import com.tvm.Client.CourseClient;
import com.tvm.DTO.*;
import com.tvm.model.Enrollment;
import com.tvm.model.StudentInfo;
import com.tvm.service.StudentService;
import com.tvm.Client.CourseClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.batch.BatchProperties;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
public class UserController {

    @Autowired
    private StudentService studentService;

    @Autowired
    private CourseClient courseClient;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    //student register -- used for student only
    @PostMapping("/student/register")
    public ResponseEntity<String> register(@RequestBody StudentDTO dto) {
        return ResponseEntity.ok(studentService.registerStudent(dto));
    }

    //login check for student/instructor -- both student and instructor
    @PostMapping("/loginCheck")
    public ResponseEntity<String> login(@RequestBody LoginDTO dto){
        return ResponseEntity.ok(studentService.login(dto));
    }

    //enroll in course -- used for only student
    @PostMapping("/student/enroll/{stu_id}/{course_id}")
    public ResponseEntity<String> enrollStudent(@PathVariable String stu_id,@PathVariable String course_id){
        String result=studentService.enrollCourse(stu_id,course_id);
        return ResponseEntity.ok(result);
    }

    //fetch all enrolled students -- used for admin and instructor
    @GetMapping("/enrollStu")
    public List<Enrollment> allEnroll(){
        return studentService.getAllEnroll();
    }

    //fetch enroll course based on student id  -- used for student,instructor and admin
    @GetMapping("/user/enrollStu/{id}")
    public Object getEnroll(@PathVariable String id){
        Optional<Enrollment> optionalEnroll = studentService.getEnrollStu(id);

        if (optionalEnroll.isEmpty()) {
            return ResponseEntity.status(404).body(" Student not found with ID: " + id);
        }

        EnrollDTO dto = studentService.mapToEnrollDTO(optionalEnroll.get());
        return ResponseEntity.ok(dto);
    }

    //fetch student by ID -- used for admin,instructor and student
    @GetMapping("/user/{id}")
    public ResponseEntity<?> getOneStu(@PathVariable String id) {
        Optional<StudentInfo> optionalStudent = studentService.getStudent(id);

        if (optionalStudent.isEmpty()) {
            return ResponseEntity.status(404).body(" Student not found with ID: " + id);
        }

        StudentDTO dto = studentService.mapToStudentDTO(optionalStudent.get());
        return ResponseEntity.ok(dto);
    }

    //fetch all available students  -- used for instructor and admin
    @GetMapping("/allStudent")
    public List<StudentInfo> allStu(){
        return studentService.getAllStu();
    }

    //update student by id  -- used for student only
    @PutMapping("/student/updStudent/{id}")
    public ResponseEntity<String> stuUpdate(@PathVariable String id,@RequestBody StudentUpdateDTO studentUpdDTO){
        try {
            studentService.stuUpd(id, studentUpdDTO);
            return ResponseEntity.ok("Student updated successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Student not found"); // Just "Student not found"
        }
    }

    //delete student by id  -- used for admin only
    @DeleteMapping("/admin/delStudent/{id}")
    public ResponseEntity<String> delStudent(@PathVariable String id){
        try {
            String result = studentService.delStu(id);
            if (result.equals("Student deleted successfully")) {
                return ResponseEntity.ok(result);
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Student not found");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Something went wrong");
        }
    }

    //fetch all instructor -- used for only admin
    @GetMapping("/admin/allInstructor")
    public ResponseEntity<?> allInstructor() {
        String sql = "SELECT * FROM instructor_info";
        List<Map<String, Object>> instructors = jdbcTemplate.queryForList(sql);

        if (instructors.isEmpty()) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body("No instructors found");
        }

        return ResponseEntity.ok(instructors);
    }

    //update student status -- used for only admin (from INACTIVE TO ACTIVE)
    @PutMapping("/admin/updStuStatus/{id}/{status}")
    public ResponseEntity<?> updStuStatus(@PathVariable String id,@PathVariable String status){
        try {
            String updateStatus = studentService.updStatus(id,status);
            return ResponseEntity.ok(updateStatus);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Student ID not found");
        }
    }

    //update student status -- used for only admin (from INACTIVE TO ACTIVE)
    @PutMapping("/admin/updEnrStatus/{id}/{status}")
    public ResponseEntity<?> updEnrStatus(@PathVariable String id,@PathVariable String status){
        try {
            String updateStatus = studentService.updEnrStatus(id,status);
            return ResponseEntity.ok(updateStatus);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Enrolled ID not found");
        }
    }

    //fetch instructor by id -- used for both admin and instructor
    @GetMapping("/{id}")
    public ResponseEntity<?> getOneInst(@PathVariable String id) {
        try {
            Map<String, Object> instructor = studentService.instGet(id);
            return ResponseEntity.ok(instructor);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Instructor ID not found");
        }
    }

    //update instructor by id  -- used for instructor only
    @PutMapping("/instructor/updInstructor/{id}")
    public ResponseEntity<String> instUpdate(@PathVariable String id, @RequestBody InstructorUpdateDTO instructorUpdateDTO) {
        try {
            String result = studentService.instUpd(id, instructorUpdateDTO);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Instructor Id not found");
        }
    }

    //delete instructor by id -- used for admin only
    @DeleteMapping("/admin/delInstructor/{id}")
    public ResponseEntity<String> delInstructor(@PathVariable String id) {
        try {
            String result = studentService.delInst(id);
            if (result.equals("Instructor deleted successfully")) {
                return ResponseEntity.ok(result);
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(result);
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Something went wrong");
        }
    }

    // GET all courses -- used for instructor and admin
    @GetMapping("/all")
    public List<CourseDTO> fetchAllCourses() {
        return courseClient.getAllCourses();
    }

    //GET course by ID -- used for instructor and admin
    @GetMapping("/getCourse/{id}")
    public CourseDTO fetchCourseById(@PathVariable("id") String courseId) {
        return courseClient.getCourseById(courseId);
    }

    //NOTE:
    //no need to create methods for getAllCourse() , getCourseById().
    //because it is used to get details from course service.
    //instructor table and also values are inserted manually.
}
