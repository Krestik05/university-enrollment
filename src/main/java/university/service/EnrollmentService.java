package university.service;

import university.model.Course;
import university.model.Enrollment;
import university.model.Student;
import university.notification.NotificationService;
import university.repository.CourseRepository;
import university.repository.EnrollmentRepository;

import java.util.concurrent.atomic.AtomicInteger;

public class EnrollmentService {

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
            throw new IllegalArgumentException("Студент не может быть null");
        }

        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Курс с id=" + courseId + " не найден"));

        enrollmentRepository.findActiveByStudentAndCourse(student.getId(), courseId)
                .ifPresent(existing -> {
                    throw new IllegalStateException(
                            "Студент '" + student.getName()
                                    + "' уже записан на курс '" + course.getTitle() + "'");
                });

        if (!course.hasFreeSlots()) {
            throw new IllegalStateException(
                    "На курсе '" + course.getTitle() + "' нет свободных мест");
        }

        Enrollment enrollment = new Enrollment(
                enrollmentIdGenerator.getAndIncrement(), student, course);

        course.incrementEnrolled();
        courseRepository.update(course);
        enrollmentRepository.save(enrollment);

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

        notificationService.notifyStudent(enrollment.getStudent().getEmail(),
                "Ваша запись на курс '" + course.getTitle() + "' отменена");
    }
}
