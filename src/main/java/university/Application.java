package university;

import university.model.Course;
import university.model.Enrollment;
import university.model.Student;
import university.notification.ConsoleNotificationService;
import university.notification.NotificationService;
import university.repository.CourseRepository;
import university.repository.EnrollmentRepository;
import university.repository.InMemoryCourseRepository;
import university.repository.InMemoryEnrollmentRepository;
import university.service.EnrollmentService;

import java.util.Scanner;

public class Application {

    public static void main(String[] args) {

        CourseRepository courseRepository = new InMemoryCourseRepository();
        EnrollmentRepository enrollmentRepository = new InMemoryEnrollmentRepository();
        NotificationService notificationService = new ConsoleNotificationService();

        EnrollmentService enrollmentService = new EnrollmentService(
                courseRepository, enrollmentRepository, notificationService);


        Course java = new Course(1, "Java-разработка", "Иванов И.И.", 3);
        Course databases = new Course(2, "Базы данных", "Петров П.П.", 2);
        Course math = new Course(3, "Математика", "Сидоров С.С.", 1);
        courseRepository.save(java);
        courseRepository.save(databases);
        courseRepository.save(math);


        Scanner scanner = new Scanner(System.in);
        int nextStudentId = 1;

        while (true) {
            System.out.println("\n=== МЕНЮ ===");
            System.out.println("1. Показать все курсы");
            System.out.println("2. Записать студента на курс");
            System.out.println("3. Отменить запись");
            System.out.println("0. Выход");
            System.out.print("Выбор: ");

            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1":
                    printCourses(courseRepository);
                    break;

                case "2":
                    enrollStudent(scanner, enrollmentService, courseRepository, nextStudentId++);
                    break;

                case "3":
                    cancelEnrollment(scanner, enrollmentService);
                    break;

                case "0":
                    System.out.println("Выход.");
                    scanner.close();
                    return;

                default:
                    System.out.println("Неизвестная команда. Попробуйте снова.");
            }
        }
    }

    private static void printCourses(CourseRepository courseRepository) {
        System.out.println("\n--- Курсы ---");
        for (Course c : courseRepository.findAll()) {
            System.out.println(c.getId() + ". " + c.getTitle()
                    + " | преподаватель: " + c.getInstructor()
                    + " | мест: " + c.getEnrolledCount() + "/" + c.getMaxStudents());
        }
    }

    private static void enrollStudent(Scanner scanner,
                                      EnrollmentService service,
                                      CourseRepository courseRepository,
                                      int studentId) {
        System.out.print("Имя студента: ");
        String name = scanner.nextLine().trim();

        System.out.print("Email студента: ");
        String email = scanner.nextLine().trim();

        printCourses(courseRepository);

        System.out.print("Введите ID курса: ");
        int courseId;
        try {
            courseId = Integer.parseInt(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            System.out.println("Ошибка: ID курса должен быть числом.");
            return;
        }

        Student student = new Student(studentId, name, email);

        try {
            Enrollment enrollment = service.enroll(student, courseId);
            System.out.println("Успешно: " + enrollment);
        } catch (IllegalArgumentException | IllegalStateException e) {
            System.out.println("Ошибка: " + e.getMessage());
        }
    }

    private static void cancelEnrollment(Scanner scanner, EnrollmentService service) {
        System.out.print("Введите ID записи (Enrollment id): ");
        int enrollmentId;
        try {
            enrollmentId = Integer.parseInt(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            System.out.println("Ошибка: ID должен быть числом.");
            return;
        }

        try {
            service.cancelEnrollment(enrollmentId);
            System.out.println("Запись " + enrollmentId + " отменена.");
        } catch (IllegalArgumentException | IllegalStateException e) {
            System.out.println("Ошибка: " + e.getMessage());
        }
    }
}