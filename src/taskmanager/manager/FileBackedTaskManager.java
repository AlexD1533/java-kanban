package taskmanager.manager;

import taskmanager.model.*;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class FileBackedTaskManager extends InMemoryTaskManager {


    public static void main(String[] args) {
        FileBackedTaskManager taskManager = new FileBackedTaskManager();
        System.out.println("Поехали!");


//      taskManager.createTask(TaskType.TASK, "Задача 1", "Выполнить работу", 0, TaskProgress.NEW);
//      taskManager.createTask(TaskType.TASK, "Задача 2", "Выполнить работу", 0, TaskProgress.NEW);
//      System.out.println();
//      taskManager.createTask(TaskType.EPIC, "Эпик 1", "Выполнить работу", 0, TaskProgress.NEW);
//      System.out.println();
//      taskManager.createTask(TaskType.SUBTASK, "подзадача 1", "Выполнить работу", 2, TaskProgress.NEW);
//      taskManager.createTask(TaskType.SUBTASK, "подзадача 2", "Выполнить работу", 2, TaskProgress.NEW);
//      taskManager.createTask(TaskType.SUBTASK, "подзадача 3", "Выполнить работу", 2, TaskProgress.NEW);
//
//      System.out.println();
//
//      taskManager.save();

        taskManager.load();
        taskManager.printAllTasks();

    }

    private void refreshCounter() {
        if (getAllTasks().isEmpty()) {
            counter = 0;
        } else {
            counter = getAllTasks().lastKey();
        }
    }

    public String toString(Task task) {
        String record = "";
        if (task instanceof Subtask) {
            Subtask subtask = (Subtask) task;
            record = String.format("%d,%s,%s,%s,%s,%s", subtask.getId(), subtask.getName(), subtask.getType(),
                    subtask.getStatus(), subtask.getDescription(), subtask.getEpicId());
        } else if (task instanceof Epic) {
            Epic epic = (Epic) task;
            record = String.format("%d,%s,%s,%s,%s", epic.getId(), epic.getName(), epic.getType(),
                    epic.getStatus(), epic.getDescription());
        } else if (task != null) {
            record = String.format("%d,%s,%s,%s,%s", task.getId(), task.getName(), task.getType(),
                    task.getStatus(), task.getDescription());
        }
        return record;
    }


    public Task fromString(String value) {
        if (value.isEmpty() || value.isBlank()) {
            System.out.println("SavedDataCSV.txt: файл пустой");
            return null;
        }
        String[] res = value.split(",");
        if (res.length < 5) {
            System.out.println("Ошибка формата строки");
            return null;
        }

        TaskType type = TaskType.valueOf(res[2]);
        TaskProgress progress = TaskProgress.valueOf(res[3]);
        int id = Integer.parseInt(res[0]);

        switch(type) {
            case EPIC:
               return new Epic(id, res[1], res[4], type,
                        progress, new HashMap<>());
            case TASK:
                return new Task(id, res[1], res[4], type,
                        progress);
            case SUBTASK:
                int epicId = Integer.parseInt(res[5]);
                return new Subtask(id, res[1], res[4], type,
                       epicId,
                       progress);
            default:
                return null;
        }
    }

    public void save() {
        try (
                FileWriter fileWriter = new FileWriter("SavedDataCSV.txt");
        ) {
            fileWriter.write("id,type,name,status,description,epic" + "\n");
            for (Task task : getAllTasks().values()) {
                fileWriter.write(toString(task) + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void load() {
        try (
                BufferedReader reader = new BufferedReader(new FileReader("SavedDataCSV.txt"))
        ) {
            String header = reader.readLine();
            while (reader.ready()) {
                String record = reader.readLine();
                Task newTask = fromString(record);

                 if (newTask instanceof Epic) {
                    Epic epic = (Epic) newTask;
                    epics.put(epic.getId(), epic);
                } else if (newTask instanceof Subtask) {

                    Subtask subtask = (Subtask) newTask;
                    if (epics.get(subtask.getEpicId()) == null) {
                        System.out.println("SavedDataCSV.txt: Эпика для субтаска:" + subtask.getId() + " не существует.");
                     } else {
                        epics.get(subtask.getEpicId()).getSubtasks().put(subtask.getId(), subtask);
                    }
                } else {
                    tasks.put(newTask.getId(), newTask);
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public TreeMap<Integer, Task> getAllTasks() {
        TreeMap<Integer, Task> res = new TreeMap<>();

        if (tasks.isEmpty()) {
            System.out.println("Задач нет");
        }
        if (epics.isEmpty()) {
            System.out.println("Эпиков нет");
        }
        res.putAll(tasks);
        res.putAll(epics);

        for (Epic task : epics.values()) {
            if (!task.getSubtasks().isEmpty()) {
                res.putAll(task.getSubtasks());
            }
        }
        return res;
    }

    @Override
    public void createTask(TaskType type, String name, String description, int epicId, TaskProgress status) {
        super.createTask(type, name, description, epicId, status);
    }

    @Override
    public void deleteTasksById(TaskType type, int id) {
        super.deleteTasksById(type, id);
    }

    @Override
    public void printAllTasks() {
        super.printAllTasks();
    }

    @Override
    public boolean deleteAllTasks() {
        return super.deleteAllTasks();
    }

    @Override
    public void updateTask(TaskType type, int id, String name, String description, TaskProgress status, int epicId) {
        super.updateTask(type, id, name, description, status, epicId);
    }

    @Override
    public void getEpicTasks(int id) {
        super.getEpicTasks(id);
    }

    @Override
    public void printTasksByType(TaskType type) {
        super.printTasksByType(type);
    }

    @Override
    public Map<Integer, Subtask> getAllSubtasks(Map<Integer, Epic> epics) {
        return super.getAllSubtasks(epics);
    }

    @Override
    public void updateEpicTaskStatus(int epicId) {
        super.updateEpicTaskStatus(epicId);
    }

    @Override
    public List<Task> getHistory() {
        return super.getHistory();
    }

    @Override
    public Task getTask(int id) {
        return super.getTask(id);
    }

    @Override
    public Subtask getSubtask(int id) {
        return super.getSubtask(id);
    }

    @Override
    public Epic getEpic(int id) {
        return super.getEpic(id);
    }
}
