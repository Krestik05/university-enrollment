package university.repository;

import university.model.Enrollment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class InMemoryEnrollmentRepository implements EnrollmentRepository {
    private final Map<Integer, Enrollment> storage = new HashMap<>();

    @Override
    public void save(Enrollment enrollment) {
        storage.put(enrollment.getId(), enrollment);
    }

    @Override
    public Optional<Enrollment> findById(int id) {
        return Optional.ofNullable(storage.get(id));
    }

    @Override
    public List<Enrollment> findAll() {
        return new ArrayList<>(storage.values());
    }

    @Override
    public void update(Enrollment enrollment) {
        storage.put(enrollment.getId(), enrollment);
    }

    @Override
    public Optional<Enrollment> findActiveByStudentAndCourse(int studentId, int courseId) {
        return storage.values().stream()
                .filter(e -> e.isActive()
                        && e.getStudent().getId() == studentId
                        && e.getCourse().getId() == courseId)
                .findFirst();
    }
}