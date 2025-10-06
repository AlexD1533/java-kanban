package taskmanager.manager;

import taskmanager.model.*;


import java.time.LocalDateTime;
import java.util.*;

public interface TaskManager {
  Optional<LocalDateTime> updateEpicEndTime(int epicId);

 TreeSet<Task> getPrioritizedTasks();

 TreeMap<Integer, Task> getAllTasks();

 Optional<LocalDateTime> updateEpicStartTime(int epicId);
   void updateEpic (int epicId);

    void printTasksByType(TaskType type);

    Map<Integer, Subtask> getAllSubtasks(Map<Integer, Epic> epics);

    void addTask(int id, Task task);

    void addEpic(int id, Epic epic);

    void addSubtask(int id, Subtask subtask);


    void createTask(TaskType type, String name, String description, int epicId, TaskProgress status, String startTime, long minutesForDuration, String endTime);

    void deleteTasksById(TaskType type, int id);

    void printAllTasks();

    boolean deleteAllTasks();

    Task getTask(int id);

    Subtask getSubtask(int id);

    Epic getEpic(int id);



    void updateTask(TaskType type, int id, String name, String description, TaskProgress status, int epicId, String startTime, long minutesForDuration, String endTime);

    void getEpicTasks(int id);

    void updateEpicTaskStatus(int epicId);

    List<Task> getHistory();
}
