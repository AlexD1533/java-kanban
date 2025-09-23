package taskmanager.manager;

import taskmanager.model.Epic;
import taskmanager.model.Task;
import taskmanager.model.Subtask;
import taskmanager.model.TaskProgress;
import taskmanager.model.TaskType;
import taskmanager.util.Validation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InMemoryTaskManager implements TaskManager {
    private static int counter = 0;
    private static final Map<Integer, Task> tasks = new HashMap<>();
    private static final Map<Integer, Epic> epics = new HashMap<>();
    private final HistoryManager historyManager = Managers.getDefaultHistory();

    public Map<Integer, Task> getTasks() {
return Map.copyOf(tasks);
    }

    public Map<Integer, Epic> getEpics() {
        return Map.copyOf(epics);
    }

protected void addTask (int id, Task task) {
    tasks.put(id, task);
}

protected void addEpic (int id, Epic epic) {
    epics.put(id, epic);
}

protected void addSubtask(int id, Subtask subtask) {
    int epicId = subtask.getEpicId();
    Map<Integer, Subtask> current = new HashMap<>(epics.get(epicId).getSubtasks());
    current.put(id, subtask);
    addEpic(epicId, new Epic(epicId, epics.get(epicId).getName(),
            epics.get(epicId).getDescription(), epics.get(epicId).getType(),
            epics.get(epicId).getStatus(), current));
    updateEpicTaskStatus(epicId);
    System.out.println("Подзадача создана: " + id + " " + subtask.getName() + " в эпике №" + epicId);
}

    @Override
    public void createTask(TaskType type, String name, String description, int epicId, TaskProgress status) {
        if (!Validation.inputValidation(name)) {
            return;
        }
        int id = counter++;
        switch (type) {
            case TaskType.TASK:
                addTask(id, new Task(id, name, description, type, status));
                System.out.println("Задача создана: " + id + " " + name);
                break;
            case TaskType.EPIC:
                TaskProgress defaultStatus = TaskProgress.NEW;
                epics.put(id, new Epic(id, name, description, type, defaultStatus, new HashMap<>()));
                System.out.println("Эпик создан: " + id + " " + name);
                break;
            case TaskType.SUBTASK:
                if (!Validation.epicValidation(epicId, epics)) {
                    break;
                }
                addSubtask(id, new Subtask(id, name, description, type, epicId, status));

                break;
            default:
                System.out.println("Неправильный тип задачи");
                break;
        }
    }

    @Override
    public void deleteTasksById(TaskType type, int id) {

        switch (type) {
            case TaskType.TASK:
                if (!Validation.taskValidation(id, tasks)) {
                    break;
                }
                tasks.remove(id);
                historyManager.remove(id);
                break;
            case TaskType.EPIC:
                if (!Validation.epicValidation(id, epics)) {
                    break;
                }
                List<Integer> idEpicList = new ArrayList<>(epics.get(id).getSubtasks().keySet());

                epics.remove(id);

                historyManager.remove(id);
                for (Integer key : idEpicList) {
                    historyManager.remove(key);
                }
                break;
            case TaskType.SUBTASK:
                if (!Validation.subTaskValidation(id, epics)) {
                    break;
                }
                int epicId = getAllSubtasks(epics).get(id).getEpicId();
                if (!Validation.subTaskValidationByEpic(epicId, id, epics)) {
                    break;
                }
                Map<Integer, Subtask> current = new HashMap<>(epics.get(epicId).getSubtasks());
                current.remove(id);
                addEpic(epicId, new Epic(epicId, epics.get(epicId).getName(),
                        epics.get(epicId).getDescription(), epics.get(epicId).getType(),
                        epics.get(epicId).getStatus(), current));

                historyManager.remove(id);
                updateEpicTaskStatus(epicId);
                break;
            default:
                System.out.println("Неправильный тип задачи");
                break;
        }
    }

    @Override
    public void printAllTasks() {
        if (tasks.isEmpty()) {
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

    @Override
    public boolean deleteAllTasks() {
        tasks.clear();
        epics.clear();
        System.out.println("Все задачи удалены");
        counter = 0;
        return tasks.isEmpty() && epics.isEmpty();
    }

    @Override
    public void updateTask(TaskType type, int id, String name, String description, TaskProgress status, int epicId) {
        switch (type) {
            case TaskType.TASK:
                if (!Validation.taskValidation(id, tasks)) {
                    break;
                }
                addTask(id, new Task(id, name, description, type, status));
                System.out.println("Задача обновлена: " + id + " " + name);
                break;
            case TaskType.EPIC:
                if (!Validation.epicValidation(id, epics)) {
                    break;
                }
                addEpic(id, new Epic(id, name, description, type, status, epics.get(id).getSubtasks()));

                System.out.println("Эпик обновлен: " + id + " " + name);
                break;
            case TaskType.SUBTASK:
                if (!Validation.epicValidation(epicId, epics)) {
                    break;
                }
                if (!Validation.subTaskValidationByEpic(epicId, id, epics)) {
                    break;
                }

                Map<Integer, Subtask> current = new HashMap<>(epics.get(epicId).getSubtasks());

                current.put(id, new Subtask(id, name, description, type, epicId, status));

                addEpic(epicId, new Epic(epicId, epics.get(epicId).getName(),
                        epics.get(epicId).getDescription(), epics.get(epicId).getType(),
                        epics.get(epicId).getStatus(), current));

                System.out.println("Подзадача обновлена: " + id + " " + name + " в эпике: " + epicId);
                updateEpicTaskStatus(epicId);
                break;
            default:
                System.out.println("Неправильный тип задачи");
                break;
        }
    }

    @Override
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
        historyManager.addTask(epics.get(id));
        System.out.println(epics.get(id).getSubtasks().values());
    }

    @Override
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

    @Override
    public Map<Integer, Subtask> getAllSubtasks(Map<Integer, Epic> epics) {
        Map<Integer, Subtask> currentSubtask = new HashMap<>();
        for (Epic task : epics.values()) {
            currentSubtask.putAll(task.getSubtasks());
        }
        return currentSubtask;
    }

    @Override
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


            updateTask(epics.get(epicId).getType(), epicId, epics.get(epicId).getName(),
                    epics.get(epicId).getDescription(), TaskProgress.NEW, 0);

        } else if (doneCount == epics.get(epicId).getSubtasks().size()) {
            updateTask(epics.get(epicId).getType(), epicId, epics.get(epicId).getName(),
                    epics.get(epicId).getDescription(), TaskProgress.DONE, 0);
        } else {
            updateTask(epics.get(epicId).getType(), epicId, epics.get(epicId).getName(),
                    epics.get(epicId).getDescription(), TaskProgress.IN_PROGRESS, 0);
        }
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }


    @Override
    public Task getTask(int id) {
        if (!Validation.taskValidation(id, tasks)) {
            return null;
        }

        historyManager.addTask(tasks.get(id));
        return tasks.get(id);
    }

    @Override
    public Subtask getSubtask(int id) {

        if (!Validation.subTaskValidation(id, epics)) {
            return null;
        }
        int epicId = getAllSubtasks(epics).get(id).getEpicId();

        if (!Validation.subTaskValidationByEpic(epicId, id, epics)) {
            return null;
        }
        historyManager.addTask(epics.get(epicId).getSubtasks().get(id));
        return epics.get(epicId).getSubtasks().get(id);
    }

    @Override
    public Epic getEpic(int id) {

        if (!Validation.epicValidation(id, epics)) {
            return null;
        }
        historyManager.addTask(epics.get(id));
        return epics.get(id);
    }


}


