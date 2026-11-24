package university;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;
import university.model.Course;
import university.model.Student;
import university.repository.InMemoryCourseRepository;
import university.repository.InMemoryEnrollmentRepository;
import university.service.EnrollmentService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class EnrollmentLoggingTest {
    private Logger logger;
    private ListAppender<ILoggingEvent> appender;
    private EnrollmentService service;
    private InMemoryCourseRepository courses;
    private Student student;

    @BeforeEach
    void setUp() {
        logger = (Logger) LoggerFactory.getLogger(EnrollmentService.class);
        appender = new ListAppender<>();
        appender.start();
        logger.addAppender(appender);
        courses = new InMemoryCourseRepository();
        courses.save(new Course(1, "Java", "Иванов", 3));
        service = new EnrollmentService(courses, new InMemoryEnrollmentRepository(), (email, message) -> { });
        student = new Student(1, "Алексей", "alex@university.com");
    }

    @AfterEach
    void tearDown() {
        logger.detachAppender(appender);
        appender.stop();
    }

    @Test
    void successfulEnrollmentLogsInfo() {
        service.enroll(student, 1);

        assertTrue(appender.list.stream().anyMatch(event -> event.getLevel() == Level.INFO
                && event.getFormattedMessage().contains("Enrollment created")));
        assertEquals(1, courses.findById(1).orElseThrow().getEnrolledCount());
    }

    @Test
    void duplicateEnrollmentLogsWarn() {
        service.enroll(student, 1);
        appender.list.clear();

        assertThrows(IllegalStateException.class, () -> service.enroll(student, 1));

        assertTrue(appender.list.stream().anyMatch(event -> event.getLevel() == Level.WARN
                && event.getFormattedMessage().contains("already enrolled")));
        assertEquals(1, courses.findById(1).orElseThrow().getEnrolledCount());
    }

    @Test
    void missingCourseLogsError() {
        assertThrows(IllegalArgumentException.class, () -> service.enroll(student, 999));

        assertTrue(appender.list.stream().anyMatch(event -> event.getLevel() == Level.ERROR
                && event.getFormattedMessage().contains("Course not found")));
        assertEquals(0, courses.findById(1).orElseThrow().getEnrolledCount());
    }
}
