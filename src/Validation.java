import java.util.HashMap;
import java.util.Map;

public final class Validation {

    public static boolean inputValidation(String name, TaskProgress status) {

        boolean statusCheck = false;
        for (TaskProgress stat : TaskProgress.values()) {
            if (stat == status) {
                statusCheck = true;
            }
        }

        if (!statusCheck) {
            System.out.println("Такого статуса не существует");
            return false;
        }
        if (name == null) {
            System.out.println("Имя не должно быть пустым");
            return false;
        }

        if (status == null) {
            System.out.println("Статус не может быть пустым");
            return false;
        }

        return true;
    }

    public static boolean inputValidationUpdate(int id, String name, TaskProgress status) {

        boolean statusCheck = false;
        for (TaskProgress stat : TaskProgress.values()) {
            if (stat == status) {
                statusCheck = true;
            }
        }

        if (!statusCheck) {
            System.out.println("Такого статуса не существует");
            return false;
        }
        if (name == null) {
            System.out.println("Имя не должно быть пустым");
            return false;
        }

        if (status == null) {
            System.out.println("Статус не может быть пустым");
            return false;
        }

        return true;
    }


    public static boolean epicValidation(int id) {

        if (!TaskManager.getEpics().containsKey(id)) {
            System.out.println("Эпик " + id + " не существует.");
            return false;
        }
        return true;
    }

    public static boolean taskValidation(int id) {

        if (!TaskManager.getTasks().containsKey(id)) {
            System.out.println("Эпик " + id + " не существует.");
            return false;
        }
        return true;
    }

    public static boolean subTaskValidation(int id) {


        Map<Integer, Subtask> currentSubtask = new HashMap<>();
        for (Epic task : TaskManager.getEpics().values()) {
            currentSubtask.putAll(task.getSubtasks());
        }
        if (!currentSubtask.containsKey(id)) {
            System.out.println("Такой задачи не существует");
            return false;
        }
        return true;
    }
}