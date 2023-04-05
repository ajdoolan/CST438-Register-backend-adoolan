package com.cst438;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.cst438.domain.Student;
import com.cst438.domain.StudentRepository;

/*
 * (registration service) as an Admin I can add a new student to the registration service
 */

@SpringBootTest(classes = { Cst4380wRegistrationApplication.class })
public class EndToEndRegistrationApplicationAdminAddStudent {

  public static final String CHROME_DRIVER_FILE_LOCATION = "/Users/adoolan/Desktop/chromedriver_mac64/chromedriver";

  public static final String URL = "http://localhost:3000";

  public static final String TEST_USER_EMAIL = "tdoe@csumb.edu";

  public static final String TEST_USER_NAME = "Test Doe";

  public static final int SLEEP_DURATION = 3000; // 1 second.

  /*
   * When running in @SpringBootTest environment, database repositories can be used
   * with the actual database.
   */

  @Autowired
  StudentRepository studentRepository;

  @Test
  public void addStudentTest() throws Exception {

    /*
     * if student already exists, delete the student.
     */

    Student x = null;
    do {
      x = studentRepository.findByEmail(TEST_USER_EMAIL);
      if (x != null)
      studentRepository.delete(x);
    } while (x != null);

    // set the driver location and start driver
    //@formatter:off
    // browser  property name         Java Driver Class
    // edge   webdriver.edge.driver     EdgeDriver
    // FireFox   webdriver.firefox.driver   FirefoxDriver
    // IE     webdriver.ie.driver     InternetExplorerDriver
    //@formatter:on

    // set the driver location and start driver
    System.setProperty("webdriver.chrome.driver", CHROME_DRIVER_FILE_LOCATION);
    ChromeOptions ops = new ChromeOptions();
    ops.addArguments("--remote-allow-origins=*");

    WebDriver driver = new ChromeDriver(ops);

    // set an implicit wait time to avoid flaky tests
    driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);

    try {

      WebElement we;

      driver.get(URL);
      Thread.sleep(SLEEP_DURATION);

      // Open the Add Student form.
      we = driver.findElement(By.id("add-student"));
      we.click();

      // Enter student name and email.
      driver.findElement(By.xpath("//input[@name='name']")).sendKeys(TEST_USER_NAME);
      driver.findElement(By.xpath("//input[@name='email']")).sendKeys(TEST_USER_EMAIL);
      driver.findElement(By.xpath("//button[@id='Add']")).click();
      Thread.sleep(SLEEP_DURATION);

      Student s = studentRepository.findByEmailAndName(TEST_USER_EMAIL, TEST_USER_NAME);
      assertNotNull(s, "Student not found in database.");
      assertEquals(s.getEmail(), TEST_USER_EMAIL);
      assertEquals(s.getName(), TEST_USER_NAME);

    } catch (Exception ex) {
      throw ex;
    } finally {

      // clean up database.

      Student s = studentRepository.findByEmailAndName(TEST_USER_EMAIL, TEST_USER_NAME);
      if (s != null)
      studentRepository.delete(s);

      driver.close();
      driver.quit();
    }
  }
}
