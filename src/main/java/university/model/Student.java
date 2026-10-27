package university.model;

public class Student {
    private final int id;
    private final String name;
    private final String email;

    public Student(int id, String name, String email) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Имя студента не может быть пустым");
        }
        this.id = id;
        this.name = name;
        this.email = email;
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public String getEmail() { return email; }

    @Override
    public String toString() {
        return "Student{id=" + id + ", name='" + name + "', email='" + email + "'}";
    }
}