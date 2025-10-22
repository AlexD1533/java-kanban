package taskmanager.util;

import taskmanager.model.Epic;
import java.time.DateTimeException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

public final class Validation {
    public static boolean inputValidation(String name, String startTime, String endTime) {
        DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
        if (name == null || name.isBlank()) {
            System.out.println("Имя не должно быть пустым");
            return false;
        }
        if (startTime == null || startTime.isBlank()) {
            System.out.println("Поле startTime не должно быть пустым");
            return false;
        }
        try {
            LocalDateTime.parse(startTime, formatter);
            LocalDateTime.parse(endTime, formatter);

        } catch (DateTimeException e) {
            System.out.println("Неправильный формат ввода даты");
            return false;
        }
        return true;
    }

    public static boolean epicValidation(int id, Map<Integer, Epic> epics) {
        if (!epics.containsKey(id)) {
            System.out.println("Эпик " + id + " не существует.");
            return false;
        }
        return true;
    }

    public static boolean subTasksEmptyValidationByEpic(int id, Map<Integer, Epic> epics) {
        if (epics.get(id).getSubtasks().isEmpty()) {
            System.out.println("Список подзадач в эпике: " + id + " пуст");
            return false;
        }
        return true;
    }
}