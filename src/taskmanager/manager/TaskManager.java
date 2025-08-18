package taskmanager.manager;

import taskmanager.model.*;
import taskmanager.util.Validation;

import java.util.Map;

public interface TaskManager {

    Map<Integer, Subtask> getAllSubtasks(Map<Integer, Epic> epics);

    void createTask(TaskType type, String name, String description, int epicId, TaskProgress status);

    void printTasksByType(TaskType type);

    void deleteTasksByType(TaskType type);

    void deleteTasksById(TaskType type, int id);

    void printAllTasks();

    boolean deleteAllTasks();

    HistoryManager<Task> getHistoryManager();

    Task getTask(int id);

    Subtask getSubtask(int id);

    Epic getEpic(int id);

    void updateTask(TaskType type, int id, String name, String description, TaskProgress status, int epicId);

    void getEpicTasks(int id);

    void updateEpicTaskStatus(int epicId);
}
