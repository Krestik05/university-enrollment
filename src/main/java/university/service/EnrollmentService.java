package university.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import university.model.Course;
import university.model.Enrollment;
import university.model.Student;
import university.notification.NotificationService;
import university.repository.CourseRepository;
import university.repository.EnrollmentRepository;

import java.util.concurrent.atomic.AtomicInteger;
// LR5 Checkstyle demonstration: this deliberately long comment exceeds the configured maximum line length of 120 characters while leaving the enrollment business logic unchanged.
public class EnrollmentService {
    private static final Logger LOGGER = LoggerFactory.getLogger(EnrollmentService.class);

    private final CourseRepository courseRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final NotificationService notificationService;
    private final AtomicInteger enrollmentIdGenerator = new AtomicInteger(1);

    public EnrollmentService(CourseRepository courseRepository,
                             EnrollmentRepository enrollmentRepository,
                             NotificationService notificationService) {
        this.courseRepository = courseRepository;
        this.enrollmentRepository = enrollmentRepository;
        this.notificationService = notificationService;
    }

    public Enrollment enroll(Student student, int courseId) {
        if (student == null) {
            LOGGER.error("Enrollment failed: student is null, courseId={}", courseId);
            throw new IllegalArgumentException("Студент не может быть null");
        }

        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> {
                    LOGGER.error("Course not found: courseId={}, studentId={}", courseId, student.getId());
                    return new IllegalArgumentException("Курс с id=" + courseId + " не найден");
                });

        enrollmentRepository.findActiveByStudentAndCourse(student.getId(), courseId)
                .ifPresent(existing -> {
                    LOGGER.warn("Student already enrolled: studentId={}, courseId={}", student.getId(), courseId);
                    throw new IllegalStateException(
                            "Студент '" + student.getName()
                                    + "' уже записан на курс '" + course.getTitle() + "'");
                });

        if (!course.hasFreeSlots()) {
            LOGGER.warn("No available slots: courseId={}, studentId={}", courseId, student.getId());
            throw new IllegalStateException(
                    "На курсе '" + course.getTitle() + "' нет свободных мест");
        }

        Enrollment enrollment = new Enrollment(
                enrollmentIdGenerator.getAndIncrement(), student, course);

        course.incrementEnrolled();
        courseRepository.update(course);
        enrollmentRepository.save(enrollment);
        LOGGER.info("Enrollment created: enrollmentId={}, studentId={}, courseId={}",
                enrollment.getId(), student.getId(), courseId);

        notificationService.notifyStudent(student.getEmail(),
                "Вы успешно записаны на курс '" + course.getTitle() + "'");

        return enrollment;
    }

    public void cancelEnrollment(int enrollmentId) {
        Enrollment enrollment = enrollmentRepository.findById(enrollmentId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Запись с id=" + enrollmentId + " не найдена"));

        if (!enrollment.isActive()) {
            throw new IllegalStateException("Запись уже отменена");
        }

        enrollment.cancel();
        enrollmentRepository.update(enrollment);

        Course course = enrollment.getCourse();
        course.decrementEnrolled();
        courseRepository.update(course);
        LOGGER.info("Enrollment cancelled: enrollmentId={}, courseId={}", enrollmentId, course.getId());

        notificationService.notifyStudent(enrollment.getStudent().getEmail(),
                "Ваша запись на курс '" + course.getTitle() + "' отменена");
    }


    public int getAvailableSlots(int courseId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Курс с id=" + courseId + " не найден"));
        return course.getMaxStudents() - course.getEnrolledCount();
    }
}
