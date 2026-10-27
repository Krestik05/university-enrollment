package university.repository;

import university.model.Enrollment;
import java.util.List;
import java.util.Optional;

public interface EnrollmentRepository {
    void save(Enrollment enrollment);
    Optional<Enrollment> findById(int id);
    List<Enrollment> findAll();
    void update(Enrollment enrollment);

    Optional<Enrollment> findActiveByStudentAndCourse(int studentId, int courseId);
}