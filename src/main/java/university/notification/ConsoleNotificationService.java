package university.notification;

public class ConsoleNotificationService implements NotificationService {
    @Override
    public void notifyStudent(String email, String message) {
        System.out.println("[NOTIFICATION] -> " + email + ": " + message);
    }
}