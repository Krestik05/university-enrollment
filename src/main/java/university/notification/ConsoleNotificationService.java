package university.notification;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConsoleNotificationService implements NotificationService {
    private static final Logger LOGGER = LoggerFactory.getLogger(ConsoleNotificationService.class);

    @Override
    public void notifyStudent(String email, String message) {
        LOGGER.info("[NOTIFICATION] -> {}: {}", email, message);
    }
}
