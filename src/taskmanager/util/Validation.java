package taskmanager.util;

import taskmanager.model.Epic;
import taskmanager.model.Task;
import taskmanager.model.Subtask;

import java.util.HashMap;
import java.util.Map;

public final class Validation {
    public static boolean inputValidation(String name) {
        if (name == null || name.isBlank()) {
            System.out.println("Имя не должно быть пустым");
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

    public static boolean taskValidation(int id, Map<Integer, Task> tasks) {
        if (!tasks.containsKey(id)) {
            System.out.println("Задача " + id + " не существует.");
            return false;
        }
        return true;
    }

    public static boolean subTaskValidation(int id, Map<Integer, Epic> epics) {
        Map<Integer, Subtask> currentSubtask = new HashMap<>();
        for (Epic task : epics.values()) {
            currentSubtask.putAll(task.getSubtasks());
        }
        if (!currentSubtask.containsKey(id)) {
            System.out.println("Такой задачи не существует");
            return false;
        }
        return true;
    }

    public static boolean subTaskValidationByEpic(int epicId, int id, Map<Integer, Epic> epics) {
        if (!epics.get(epicId).getSubtasks().containsKey(id)) {
            System.out.println("Такой задачи не существует в эпике: " + epicId);
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