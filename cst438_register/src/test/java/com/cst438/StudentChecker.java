package com.cst438;

import org.junit.jupiter.api.Test;

import static com.cst438.test.utils.TestUtils.fromJsonString;
import static com.cst438.test.utils.TestUtils.asJsonString;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.cst438.domain.StudentRepository;
import com.cst438.service.GradebookService;
import com.cst438.controller.StudentController;
import com.cst438.domain.CourseRepository;
import com.cst438.domain.Enrollment;
import com.cst438.domain.EnrollmentRepository;
import com.cst438.domain.Student;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

@ContextConfiguration(classes = {CourseRepository.class, StudentRepository.class, EnrollmentRepository.class, GradebookService.class, StudentController.class})
@WebMvcTest
public class StudentChecker {
	
	MockHttpServletResponse response;
	
	static final String URL = "http://localhost:8080";
	public static final int TEST_COURSE_ID = 40442;
	public static final String TEST_STUDENT_EMAIL = "test@csumb.edu";
	public static final String TEST_STUDENT_NAME  = "test";
	public static final int TEST_YEAR = 2021;
	public static final String TEST_SEMESTER = "Fall";
	
	@MockBean
	CourseRepository courseRepository;

	@MockBean
	StudentRepository studentRepository;

	@MockBean
	EnrollmentRepository enrollmentRepository;

	@MockBean
	GradebookService gradebookService;
	
	@Autowired
	private MockMvc mvc;
	
	@Test
	public void testCreateStudent() throws Exception {
		Student student = new Student();
		student.setEmail(TEST_STUDENT_EMAIL);
		student.setName(TEST_STUDENT_NAME);
		student.setStatusCode(0);
		student.setStudent_id(1);
		
		response = mvc.perform(
				MockMvcRequestBuilders
			      .post("/student")
			      .content("{\n"
			      		+ "    \"name\": \"test\",\n"
			      		+ "    \"email\": \"test@csumb.edu\"\n"
			      		+ "}")
			      .contentType(MediaType.APPLICATION_JSON)
			      .accept(MediaType.APPLICATION_JSON))
				.andReturn().getResponse();
		
		assertEquals(200, response.getStatus());
		
		Student testStudent = fromJsonString(
               response.getContentAsString(), 
               Student.class);
		
		Student newStudent = studentRepository.findByEmail(TEST_STUDENT_EMAIL);
		
		assertTrue(testStudent.getName().equals("test"));
	}
	
	@Test
	public void testStudentAddHold() throws Exception {
		Student student = new Student();
		student.setEmail(TEST_STUDENT_EMAIL);
		student.setName(TEST_STUDENT_NAME);
		student.setStatusCode(0);
		student.setStudent_id(1);
		
	    given(studentRepository.findByEmail(TEST_STUDENT_EMAIL)).willReturn(student);
	    given(studentRepository.findById(1)).willReturn(student);
		
		response = mvc.perform(
				MockMvcRequestBuilders
			      .post("/student/hold/add")
			      .content("{\n"
				      		+ "    \"student_id\": \"1\"\n"
				      		+ "}")
			      .contentType(MediaType.APPLICATION_JSON)
			      .accept(MediaType.APPLICATION_JSON))
				.andReturn().getResponse();
		
		assertEquals(200, response.getStatus());
		
		Student testStudent = fromJsonString(
               response.getContentAsString(), 
               Student.class);
		
		assertTrue(testStudent.getStatus().equals("HOLD"));
	}
	
	@Test
	public void testStudentRemoveHold() throws Exception {
		Student student = new Student();
		student.setEmail(TEST_STUDENT_EMAIL);
		student.setName(TEST_STUDENT_NAME);
		student.setStatusCode(0);
		student.setStudent_id(1);
		
		// given  -- stubs for database repositories that return test data
	    given(studentRepository.findByEmail(TEST_STUDENT_EMAIL)).willReturn(student);
	    given(studentRepository.findById(1)).willReturn(student);
		
		
		response = mvc.perform(
				MockMvcRequestBuilders
			      .post("/student/hold/remove")
			      .content("{\n"
				      		+ "    \"student_id\": \"1\"\n"
				      		+ "}")
			      .contentType(MediaType.APPLICATION_JSON)
			      .accept(MediaType.APPLICATION_JSON))
				.andReturn().getResponse();
		
		assertEquals(200, response.getStatus());
		
		Student testStudent = fromJsonString(
               response.getContentAsString(), 
               Student.class);
		
		assertNull(testStudent.getStatus());
	}
}
