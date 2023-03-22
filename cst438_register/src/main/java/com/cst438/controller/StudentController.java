package com.cst438.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.cst438.domain.CourseRepository;
import com.cst438.domain.Enrollment;
import com.cst438.domain.EnrollmentRepository;
import com.cst438.domain.ScheduleDTO;
import com.cst438.domain.Student;
import com.cst438.domain.StudentRepository;
import com.cst438.service.GradebookService;
import org.springframework.web.bind.annotation.CrossOrigin;

@RestController
@CrossOrigin(origins = "http://localhost:3000")
public class StudentController {

	@Autowired
	CourseRepository courseRepository;

	@Autowired
	StudentRepository studentRepository;

	@Autowired
	EnrollmentRepository enrollmentRepository;

	@Autowired
	GradebookService gradebookService;

	/*
	 * get current Student
	 */
	@GetMapping("/student")
	public Student getStudent( @RequestParam("student_id") int student_id) {
		System.out.println("/student called.");

		Student student = studentRepository.findById(student_id);

		if (student != null) {
			System.out.println("/student student "+student.getName()+" "+student.getEmail());
			return student;
		} else {
			System.out.println("/student student not found. "+student_id);
			throw  new ResponseStatusException( HttpStatus.BAD_REQUEST, "Student not found. " );
		}
	}

	@PostMapping("/student")
	@Transactional
	private Student createStudent(@RequestBody Student newStudent) {

		Student student = studentRepository.findByEmail(newStudent.getEmail());

		if (student != null) {
			System.out.println("/student student email already exists. " + newStudent.getEmail());
			throw  new ResponseStatusException( HttpStatus.BAD_REQUEST, "Student email exists. " );
		}

		student = new Student();
		student.setName(newStudent.getName());
		student.setEmail(newStudent.getEmail());

		Student savedStudent = studentRepository.save(student);

		return student;
	}

	@PostMapping("/student/hold/add")
	@Transactional
	private Student addHold(@RequestBody Student currentStudent) {

		Student student = studentRepository.findById(currentStudent.getStudent_id());

		if (student == null) {
			System.out.println("/student student not found. "+currentStudent.getStudent_id());
			throw  new ResponseStatusException( HttpStatus.BAD_REQUEST, "Student not found. " );
		}

		student.setStatus("HOLD");
		Student savedStudent = studentRepository.save(student);

		return student;
	}

	@PostMapping("/student/hold/remove")
	@Transactional
	private Student removeHold(@RequestBody Student currentStudent) {

		Student student = studentRepository.findById(currentStudent.getStudent_id());

		if (student == null) {
			System.out.println("/student student not found. "+currentStudent.getStudent_id());
			throw  new ResponseStatusException( HttpStatus.BAD_REQUEST, "Student not found. " );
		}

		student.setStatus(null);
		Student savedStudent = studentRepository.save(student);

		return student;
	}
}
