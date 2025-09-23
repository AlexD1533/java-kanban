package taskmanager.manager;

import taskmanager.manager.exceptions.ManagerSaveException;
import taskmanager.model.*;

import java.io.*;
import java.nio.file.Path;
import java.util.*;

public class FileBackedTaskManager extends InMemoryTaskManager {

    private final static File dataFile = new File("SavedDataCSV.txt");

    public static void main(String[] args) {
        FileBackedTaskManager taskManager = new FileBackedTaskManager();
        System.out.println("Поехали!");


        taskManager.createTask(TaskType.TASK, "Задача 1", "Выполнить работу", 0, TaskProgress.NEW);
        taskManager.createTask(TaskType.TASK, "Задача 2", "Выполнить работу", 0, TaskProgress.NEW);
        System.out.println();
        taskManager.createTask(TaskType.EPIC, "Эпик 1", "Выполнить работу", 0, TaskProgress.NEW);
        System.out.println();
        taskManager.createTask(TaskType.SUBTASK, "подзадача 1", "Выполнить работу", 2, TaskProgress.NEW);
        taskManager.createTask(TaskType.SUBTASK, "подзадача 2", "Выполнить работу", 2, TaskProgress.NEW);
        taskManager.createTask(TaskType.SUBTASK, "подзадача 3", "Выполнить работу", 2, TaskProgress.NEW);
        taskManager.printAllTasks();
        System.out.println();

        taskManager.updateTask(TaskType.TASK, 1, "подзадача hello", "Выполнить работу", TaskProgress.IN_PROGRESS, 0);
        taskManager.updateTask(TaskType.SUBTASK, 3, "подзадача hello", "Выполнить работу", TaskProgress.DONE, 2);
        System.out.println();
        taskManager.printAllTasks();
        System.out.println();

        taskManager.printAllTasks();
        System.out.println();

        taskManager.getTask(0);
        taskManager.getTask(1);
        taskManager.getEpic(2);
        taskManager.getSubtask(3);

        taskManager.getTask(0);
        taskManager.getTask(1);
        taskManager.getEpic(2);
        taskManager.getTask(0);
        taskManager.getTask(1);
        taskManager.getEpic(2);
        taskManager.getSubtask(3);
        taskManager.getSubtask(3);
        taskManager.getSubtask(3);
        taskManager.getSubtask(3);
        taskManager.getSubtask(4);
        taskManager.getSubtask(5);
        taskManager.getSubtask(3);
        System.out.println("History");

        for (Task task : taskManager.getHistory()) {
            System.out.println(task);
        }
        taskManager.deleteTasksById(TaskType.TASK, 1);
        taskManager.updateTask(TaskType.SUBTASK, 3, "подзадача hello", "Выполнить работу", TaskProgress.DONE, 2);
        taskManager.getSubtask(3);
        taskManager.getEpic(2);
        System.out.println();
        System.out.println("History after delete task");

        taskManager.deleteTasksById(TaskType.EPIC, 2);

        for (Task task : taskManager.getHistory()) {
            System.out.println(task);
        }

    }


    public String toString(Task task) {
        if (task == null) {
            System.out.println("Объект равен нулю, преобразование в строку невозможно");
            return null;
        }
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

        switch (type) {
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
                FileWriter fileWriter = new FileWriter(dataFile);
        ) {

            fileWriter.write("id,type,name,status,description,epic" + "\n");
            for (Task task : getAllTasks().values()) {
                fileWriter.write(toString(task) + "\n");
            }
            System.out.println("Изменения сохранены в файл");
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка сохранения в файл");
        }
    }


    public void loadFromFile(File file) {
        try (
                BufferedReader reader = new BufferedReader(new FileReader(file))
        ) {
            List<Subtask> loadSubtasks = new ArrayList<>();

            String header = reader.readLine();
            while (reader.ready()) {
                String record = reader.readLine();
                Task newTask = fromString(record);

                if (newTask instanceof Epic) {
                    Epic epic = (Epic) newTask;
                    System.out.println("Загрузка эпика " + epic.getId());
                    addEpic(epic.getId(), epic);
                } else if (newTask instanceof Subtask) {
                    Subtask subtask = (Subtask) newTask;
                    loadSubtasks.add(subtask);
                } else {
                    System.out.println("Загрузка задачи " + newTask.getId());
                    addTask(newTask.getId(), newTask);
                }
            }

            for (Subtask subtask : loadSubtasks) {
                if (getEpics().get(subtask.getEpicId()) == null) {
                    System.out.println("SavedDataCSV.txt: Эпика для субтаска:" + subtask.getId() + " не существует.");
                } else {
                    System.out.println("Загрузка подзадачи " + subtask.getId());
                    addSubtask(subtask.getId(), subtask);

                }
            }
            System.out.println("Загрузка из файла завершена");

        } catch (IOException e) {
            System.out.println("Ошибка загрузки из файла");
            e.printStackTrace();
        }
    }


    public TreeMap<Integer, Task> getAllTasks() {
        TreeMap<Integer, Task> res = new TreeMap<>();

        if (getTasks().isEmpty()) {
            System.out.println("Задач нет");
        }
        if (getEpics().isEmpty()) {
            System.out.println("Эпиков нет");
        }
        res.putAll(getTasks());
        res.putAll(getEpics());

        for (Epic task : getEpics().values()) {
            if (!task.getSubtasks().isEmpty()) {
                res.putAll(task.getSubtasks());
            }
        }
        return res;
    }

    @Override
    public void createTask(TaskType type, String name, String description, int epicId, TaskProgress status) {
        super.createTask(type, name, description, epicId, status);
        save();
    }

    @Override
    public void deleteTasksById(TaskType type, int id) {
        super.deleteTasksById(type, id);
        save();
    }

    @Override
    public void printAllTasks() {
        super.printAllTasks();
    }

    @Override
    public boolean deleteAllTasks() {
        boolean res = super.deleteAllTasks();
        save();
        return res;
    }

    @Override
    public void updateTask(TaskType type, int id, String name, String description, TaskProgress status, int epicId) {
        super.updateTask(type, id, name, description, status, epicId);
        save();
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
