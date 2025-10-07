package taskmanager.manager;

import taskmanager.model.*;


import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Stream;

public interface TaskManager {


    Optional<Boolean> checkIntersections(Task t1);

    TreeSet<Task> getPrioritizedTasks();

 TreeMap<Integer, Task> getAllTasks();


 Stream<Task> allTasksStream();

 Optional<LocalDateTime> updateEpicStartTime(Map<Integer, Subtask> map);

 Optional<LocalDateTime> updateEpicEndTime(Map<Integer, Subtask> map);




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
