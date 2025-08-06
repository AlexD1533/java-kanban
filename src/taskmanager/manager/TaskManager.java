package taskmanager.manager;

import taskmanager.model.*;
import taskmanager.util.*;
import taskmanager.model.Task;

import java.util.HashMap;
import java.util.Map;

public class TaskManager {
    private static int counter = 0;
    private static final Map<Integer, Task> tasks = new HashMap<>();
    private static final Map<Integer, Epic> epics = new HashMap<>();

    public Map<Integer, Subtask> getAllSubtasks(Map<Integer, Epic> epics) {
        Map<Integer, Subtask> currentSubtask = new HashMap<>();
        for (Epic task : epics.values()) {
            currentSubtask.putAll(task.getSubtasks());
        }
        return currentSubtask;
    }

    public void createTask(TaskType type, String name, String description, int epicId, TaskProgress status) {
        if (!Validation.inputValidation(name, status)) {
            return;
        }
        int id = counter++;
        switch (type) {
            case TaskType.TASK:
                tasks.put(id, new Task(id, name, description, type, status));
                System.out.println("Задача создана: " + id + " " + name);
                break;
            case TaskType.EPIC:
                TaskProgress defaultStatus = TaskProgress.NEW;
                epics.put(id, new Epic(id, name, description, type, defaultStatus));
                System.out.println("Эпик создан: " + id + " " + name);
                break;
            case TaskType.SUBTASK:
                if (!Validation.epicValidation(epicId, epics)) {
                    break;
                }
                epics.get(epicId).getSubtasks().put(id, new Subtask(id, name, description, type, epicId, status));
                updateEpicTaskStatus(epicId);
                System.out.println("Подзадача создана: " + id + " " + name + " в эпике №" + epicId);
                break;
            default:
                System.out.println("Неправильный тип задачи");
                break;
        }
    }

    public void printTasksByType(TaskType type) {
        switch (type) {
            case TaskType.TASK:
                if (tasks.isEmpty()) {
                    System.out.println("Задач такого типа нет");
                    return;
                }
                for (Task task : tasks.values()) {
                    System.out.println(task);
                }
                break;
            case TaskType.EPIC:
                if (epics.isEmpty()) {
                    System.out.println("Задач такого типа нет");
                    return;
                }
                for (Epic task : epics.values()) {
                    System.out.println(task);
                }
                break;
            case TaskType.SUBTASK:
                if (epics.isEmpty()) {
                    System.out.println("Задач такого типа нет");
                    return;
                }
                System.out.println(getAllSubtasks(epics));

                break;
            default:
                System.out.println("Неправильный тип задачи");
                break;
        }
    }

    public void deleteTasksByType(TaskType type) {
        switch (type) {
            case TaskType.TASK:
                tasks.clear();
                break;
            case TaskType.EPIC:

                epics.clear();
                break;
            case TaskType.SUBTASK:

                if (epics.isEmpty()) {
                    System.out.println("Задач такого типа нет");
                    return;
                }
                for (Epic task : epics.values()) {
                    task.getSubtasks().clear();
                    updateEpicTaskStatus(task.getId());
                }
                break;
            default:
                System.out.println("Неправильный тип задачи");
                break;
        }
    }

    public void deleteTasksById(TaskType type, int id) {

        switch (type) {
            case TaskType.TASK:
                if (!Validation.taskValidation(id, tasks)) {
                    break;
                }
                tasks.remove(id);
                break;
            case TaskType.EPIC:
                if (!Validation.epicValidation(id, epics)) {
                    break;
                }
                epics.remove(id);
                break;
            case TaskType.SUBTASK:
                if (!Validation.subTaskValidation(id, epics)) {
                    break;
                }
                int epicId = getAllSubtasks(epics).get(id).getEpicId();
                if (!Validation.subTaskValidationByEpic(epicId, id, epics)) {
                    break;
                }
                epics.get(epicId).getSubtasks().remove(id);
                updateEpicTaskStatus(epicId);
                break;
            default:
                System.out.println("Неправильный тип задачи");
                break;
        }
    }

    public void printAllTasks() {
        if (tasks.isEmpty())  {
            System.out.println("Задач нет");
        }
        if (epics.isEmpty()) {
            System.out.println("Эпиков нет");
        }
        for (Task task : tasks.values()) {
            if (task != null) {
                System.out.println(task);
            }
        }
        for (Epic task : epics.values()) {
            if (task != null) {
                System.out.println(task);
            }
        }
        for (Epic task : epics.values()) {
            if (!task.getSubtasks().isEmpty()) {
                System.out.println(task.getSubtasks().values());
            }
        }
    }

    public void deleteAllTasks() {
        tasks.clear();
        epics.clear();
        System.out.println("Все задачи удалены");
    }

    public void findById(TaskType type, int id) {
        switch (type) {
            case TaskType.TASK:
                if (!Validation.taskValidation(id, tasks)) {
                    break;
                }
                System.out.println(tasks.get(id));
                break;
            case TaskType.EPIC:
                if (!Validation.epicValidation(id, epics)) {
                    break;
                }
                System.out.println(epics.get(id));
                break;
            case TaskType.SUBTASK:
                if (!Validation.subTaskValidation(id, epics)) {
                    break;
                }
                int epicId = getAllSubtasks(epics).get(id).getEpicId();
                System.out.println(epics.get(epicId).getSubtasks().get(id));
                break;
            default:
                System.out.println("Неправильный тип задачи");
                break;
        }
    }

    public void updateTask(TaskType type, int id, String name, String description, TaskProgress status, int epicId) {
        switch (type) {
            case TaskType.TASK:
                if (!Validation.taskValidation(id, tasks)) {
                    break;
                }
                tasks.put(id, new Task(id, name, description, type, status));
                System.out.println("Задача обновлена: " + id + " " + name);
                break;
            case TaskType.EPIC:
                if (!Validation.epicValidation(epicId, epics)) {
                    break;
                }
                TaskProgress defaultStatus = TaskProgress.NEW;
                epics.put(id, new Epic(id, name, description, type, defaultStatus));
                System.out.println("Эпик обновлен: " + id + " " + name);
                break;
            case TaskType.SUBTASK:
                if (!Validation.epicValidation(epicId, epics)) {
                    break;
                }
                if (!Validation.subTaskValidationByEpic(epicId, id, epics)) {
                    break;
                }
                epics.get(epicId).getSubtasks().put(id, new Subtask(id, name, description, type, epicId, status));
                System.out.println("Подзадача обновлена: " + id + " " + name + " в эпике: " + epicId);
                updateEpicTaskStatus(epicId);
                break;
            default:
                System.out.println("Неправильный тип задачи");
                break;
        }
    }

    public void getEpicTasks(int id) {

        if (!Validation.epicValidation(id, epics)) {
            return;
        }
        System.out.println("Информация о эпике:");
        System.out.println(epics.get(id));
        System.out.println("Подзадачи: ");

        if (!Validation.subTasksEmptyValidationByEpic(id, epics)) {
            return;
        }

        System.out.println(epics.get(id).getSubtasks().values());
    }

    public void updateEpicTaskStatus(int epicId) {
        if (!Validation.epicValidation(epicId, epics)) {
            return;
        }
        int newCount = 0;
        int doneCount = 0;
        for (Subtask task : epics.get(epicId).getSubtasks().values()) {
            if (task.getStatus() == TaskProgress.NEW) {
                newCount++;
            } else if (task.getStatus() == TaskProgress.DONE) {
                doneCount++;
            }
        }
        if (newCount == epics.get(epicId).getSubtasks().values().size() ||
                epics.get(epicId).getSubtasks().values().isEmpty()) {
            epics.get(epicId).setStatus(TaskProgress.NEW);
        } else if (doneCount == epics.get(epicId).getSubtasks().size()) {
            epics.get(epicId).setStatus(TaskProgress.DONE);
        } else {
            epics.get(epicId).setStatus(TaskProgress.IN_PROGRESS);
        }
    }
}


