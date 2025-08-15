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

    void deleteAllTasks();

    HistoryManager<Task> getHistoryManager();

    void getTask(int id);

    void getSubtask(int id);

    void getEpic(int id);

    void updateTask(TaskType type, int id, String name, String description, TaskProgress status, int epicId);

    void getEpicTasks(int id);

    void updateEpicTaskStatus(int epicId);
}
