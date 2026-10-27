package university.model;

public class Course {
    private final int id;
    private final String title;
    private final String instructor;
    private final int maxStudents;
    private int enrolledCount;

    public Course(int id, String title, String instructor, int maxStudents) {
        if (maxStudents <= 0) {
            throw new IllegalArgumentException("Вместимость курса должна быть положительной");
        }
        this.id = id;
        this.title = title;
        this.instructor = instructor;
        this.maxStudents = maxStudents;
        this.enrolledCount = 0;
    }

    public int getId() { return id; }
    public String getTitle() { return title; }
    public String getInstructor() { return instructor; }
    public int getMaxStudents() { return maxStudents; }
    public int getEnrolledCount() { return enrolledCount; }

    public boolean hasFreeSlots() {
        return enrolledCount < maxStudents;
    }

    public void incrementEnrolled() {
        if (!hasFreeSlots()) {
            throw new IllegalStateException("На курсе нет свободных мест");
        }
        this.enrolledCount++;
    }

    public void decrementEnrolled() {
        if (enrolledCount > 0) {
            this.enrolledCount--;
        }
    }

    @Override
    public String toString() {
        return "Course{id=" + id + ", title='" + title + "', instructor='" + instructor
                + "', enrolled=" + enrolledCount + "/" + maxStudents + "}";
    }
}