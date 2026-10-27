package university.model;

import java.time.LocalDateTime;

public class Enrollment {
    private final int id;
    private final Student student;
    private final Course course;
    private final LocalDateTime enrolledAt;
    private boolean active;

    public Enrollment(int id, Student student, Course course) {
        this.id = id;
        this.student = student;
        this.course = course;
        this.enrolledAt = LocalDateTime.now();
        this.active = true;
    }

    public int getId() { return id; }
    public Student getStudent() { return student; }
    public Course getCourse() { return course; }
    public LocalDateTime getEnrolledAt() { return enrolledAt; }
    public boolean isActive() { return active; }

    public void cancel() {
        this.active = false;
    }

    @Override
    public String toString() {
        return "Enrollment{id=" + id + ", student=" + student.getName()
                + ", course=" + course.getTitle() + ", active=" + active + "}";
    }
}