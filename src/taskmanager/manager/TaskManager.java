package taskmanager.manager;

import taskmanager.model.*;


import java.util.List;
import java.util.Map;

public interface TaskManager {

    void printTasksByType(TaskType type);

    Map<Integer, Subtask> getAllSubtasks(Map<Integer, Epic> epics);

    void createTask(TaskType type, String name, String description, int epicId, TaskProgress status);

    void deleteTasksById(TaskType type, int id);

    void printAllTasks();

    boolean deleteAllTasks();

    Task getTask(int id);

    Subtask getSubtask(int id);

    Epic getEpic(int id);

    void updateTask(TaskType type, int id, String name, String description, TaskProgress status, int epicId);

    void getEpicTasks(int id);

    void updateEpicTaskStatus(int epicId);
    List<Task> getHistory();
}
