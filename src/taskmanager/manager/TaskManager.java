package taskmanager.manager;

import taskmanager.model.*;
import java.util.*;
import java.util.stream.Stream;

public interface TaskManager {


    boolean checkIntersections(Task t1, Task t2);

    Optional<Boolean> checkIntersectionsByList(Task t1);

    List<Task> getPrioritizedTasks();

    TreeMap<Integer, Task> getAllTasks();


    Stream<Task> allTasksStream();


    Stream<Subtask> getEpicSubtasks(int epicId);

    Map<Integer, Subtask> getAllSubtasks(Map<Integer, Epic> epics);

    void addTask(int id, Task task);

    void addEpic(int id, Epic epic);

    void addSubtask(int id, Subtask subtask);


    void createTask(TaskType type, String name, String description, int epicId, TaskProgress status, String startTime, long minutesForDuration, String endTime);

    void deleteTasksById(TaskType type, int id);

    void printAllTasks();

    boolean deleteAllTasks();

    Optional<Task> getTask(int id);

    Optional<Subtask> getSubtask(int id);

    Optional<Epic> getEpic(int id);


    void updateTask(TaskType type, int id, String name, String description, TaskProgress status, int epicId, String startTime, long minutesForDuration, String endTime);

    void printEpicSubtasks(int id);

    void updateEpicTaskStatus(int epicId);

    List<Task> getHistory();
}
