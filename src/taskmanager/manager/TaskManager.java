package taskmanager.manager;

import taskmanager.model.*;
import java.util.*;
import java.util.stream.Stream;

public interface TaskManager {


    Map<Integer, Task> getTasks();

    Map<Integer, Epic> getEpics();

    boolean checkIntersections(Task t1, Task t2);

    boolean checkIntersectionsByList(Task t1);

    List<Task> getPrioritizedTasks();

    TreeMap<Integer, Task> getAllTasks();


    Stream<Task> allTasksStream();


    Stream<Subtask> getEpicSubtasks(int epicId);



    void addTask(int id, Task task);

    void addEpic(int id, Epic epic);

    void addSubtask(int id, Subtask subtask);


    Optional<Integer> getMaxId();

    void createTask(TaskType type, String name, String description, int epicId, TaskProgress status, String startTime, long minutesForDuration, String endTime);

    void deleteTasksById(TaskType type, int id);

    void printAllTasks();

    boolean deleteAllTasks();

    Task getTask(int id);

    Subtask getSubtask(int id);

    Epic getEpic(int id);


    void updateTask(TaskType type, int id, String name, String description, TaskProgress status, int epicId, String startTime, long minutesForDuration, String endTime);

    void printEpicSubtasks(int id);

    Map<Integer, Subtask> getAllSubtasks();

    void updateEpicTaskStatus(int epicId);

    List<Task> getHistory();
}
