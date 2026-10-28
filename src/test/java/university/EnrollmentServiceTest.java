package university;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import university.model.Course;
import university.model.Enrollment;
import university.model.Student;
import university.notification.NotificationService;
import university.repository.CourseRepository;
import university.repository.EnrollmentRepository;
import university.repository.InMemoryCourseRepository;
import university.repository.InMemoryEnrollmentRepository;
import university.service.EnrollmentService;

import static org.junit.jupiter.api.Assertions.*;

class EnrollmentServiceTest {

    private EnrollmentService enrollmentService;
    private CourseRepository courseRepository;
    private EnrollmentRepository enrollmentRepository;
    private Course java;
    private Student student1;
    private Student student2;

    @BeforeEach
    void setUp() {
        courseRepository = new InMemoryCourseRepository();
        enrollmentRepository = new InMemoryEnrollmentRepository();

        NotificationService notificationService = (email, message) -> { };

        enrollmentService = new EnrollmentService(
                courseRepository, enrollmentRepository, notificationService);

        java = new Course(1, "Java-разработка", "Иванов И.И.", 3);
        courseRepository.save(java);

        student1 = new Student(1, "Алексей", "alex@university.com");
        student2 = new Student(2, "Мария", "maria@university.com");
    }

    @Test
    void uspeshnayaZapisNaKurs() {
        Enrollment enrollment = enrollmentService.enroll(student1, 1);

        assertNotNull(enrollment);
        assertTrue(enrollment.isActive());
        assertEquals(student1, enrollment.getStudent());
        assertEquals(java, enrollment.getCourse());
        assertEquals(1, java.getEnrolledCount());
    }

    @Test
    void povtornayaZapisZapreschena() {
        enrollmentService.enroll(student1, 1);

        IllegalStateException ex = assertThrows(
                IllegalStateException.class,
                () -> enrollmentService.enroll(student1, 1)
        );

        assertTrue(ex.getMessage().contains("уже записан"));
        assertEquals(1, java.getEnrolledCount());
    }

    @Test
    void netSvobodnyhMest() {
        Course smallCourse = new Course(2, "Мини-курс", "Сидоров С.С.",0);
        courseRepository.save(smallCourse);

        enrollmentService.enroll(student1, 2);

        IllegalStateException ex = assertThrows(
                IllegalStateException.class,
                () -> enrollmentService.enroll(student2, 2)
        );

        assertTrue(ex.getMessage().contains("нет свободных мест"));
        assertEquals(1, smallCourse.getEnrolledCount());
    }

    @Test
    void povtornayaZapisPosleOtmeny() {
        Enrollment first = enrollmentService.enroll(student1, 1);
        enrollmentService.cancelEnrollment(first.getId());

        Enrollment second = enrollmentService.enroll(student1, 1);

        assertNotNull(second);
        assertTrue(second.isActive());
        assertNotEquals(first.getId(), second.getId());
        assertEquals(1, java.getEnrolledCount());
    }

    /** ЛР4 / вариант 6: проверка свободных мест после записи */
    @Test
    void dostupnyeMestaPosleZapisi() {
        assertEquals(3, enrollmentService.getAvailableSlots(1));

        enrollmentService.enroll(student1, 1);

        assertEquals(2, enrollmentService.getAvailableSlots(1));
    }
}