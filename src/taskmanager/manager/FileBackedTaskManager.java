package taskmanager.manager;

import taskmanager.manager.exceptions.ManagerSaveException;
import taskmanager.model.*;

import java.io.*;
import java.time.LocalDateTime;
import java.util.*;

public class FileBackedTaskManager extends InMemoryTaskManager {

    private final File dataFile;

    public FileBackedTaskManager(File dataFile) {
        this.dataFile = dataFile;
    }

    public static void main(String[] args) {
        FileBackedTaskManager taskManager = FileBackedTaskManager.loadFromFile(new File("SavedDataCSV.txt"));
        System.out.println("Поехали!");

        taskManager.createTask(TaskType.TASK, "Задача 1", "Выполнить работу", 0, TaskProgress.NEW, "2005-12-12T00:00", 60, LocalDateTime.now().toString());
        taskManager.createTask(TaskType.EPIC, "Эпик 1", "Выполнить работу", 0, TaskProgress.NEW, "2005-12-12T00:00", 0, "2005-12-12T00:00");
        taskManager.createTask(TaskType.SUBTASK, "подзадача 1", "Выполнить работу", 1, TaskProgress.NEW, "2005-12-12T01:00", 120, LocalDateTime.now().toString());
        taskManager.createTask(TaskType.SUBTASK, "подзадача 111", "Выполнить работу", 1, TaskProgress.NEW, "2005-12-12T03:01", 120, LocalDateTime.now().toString());
        taskManager.updateTask(TaskType.SUBTASK, 2, "подзадача hello", "Выполнить работу", TaskProgress.DONE, 1, "2005-12-13T00:00", 120, LocalDateTime.now().toString());
        taskManager.updateTask(TaskType.SUBTASK, 2, "подзадача hello", "Выполнить работу", TaskProgress.DONE, 1, "2005-12-13T00:00", 120, LocalDateTime.now().toString());
        taskManager.updateTask(TaskType.SUBTASK, 3, "подзадача hello", "Выполнить работу", TaskProgress.DONE, 1, "2005-12-12T03:01", 120, LocalDateTime.now().toString());

        taskManager.printAllTasks();

    }

    public String toString(Task task) {
        if (task == null) {
            System.out.println("Объект равен нулю, преобразование в строку невозможно");
            return null;
        }
        String record;
        String emptyLine = "no_epic_id";
        String endTime = task.getEndTime().toString();
        if (task instanceof Subtask subtask) {
            record = String.format("%d,%s,%s,%s,%s,%s,%s,%s,%s", subtask.getId(), subtask.getType(), subtask.getName(),
                    subtask.getStatus(), subtask.getDescription(), subtask.getEpicId(), subtask.getStartTime(),
                    subtask.getDuration(), endTime);
        } else if (task instanceof Epic epic) {
            record = String.format("%d,%s,%s,%s,%s,%s,%s,%s,%s", epic.getId(), epic.getType(), epic.getName(),
                    epic.getStatus(), epic.getDescription(), emptyLine, epic.getStartTime(), epic.getDuration(), endTime);
        } else {
            record = String.format("%d,%s,%s,%s,%s,%s,%s,%s,%s", task.getId(), task.getType(), task.getName(),
                    task.getStatus(), task.getDescription(), emptyLine, task.getStartTime(), task.getDuration(), endTime);
        }
        return record;
    }


    public static Task fromString(String value) {
        if (value.isBlank()) {
            System.out.println("SavedDataCSV.txt: файл пустой");
            return null;
        }
        String[] res = value.split(",");
        if (res.length < 9) {
            System.out.println("Ошибка формата строки");
            return null;
        }

        TaskType type = TaskType.valueOf(res[1]);
        TaskProgress progress = TaskProgress.valueOf(res[3]);
        int id = Integer.parseInt(res[0]);
        long durationMinutes = Long.parseLong(res[7]);
        String endTime = res[8];

        return switch (type) {
            case EPIC -> new Epic(id, res[2], res[4], type,
                    progress, new HashMap<>());
            case TASK -> new Task(id, res[2], res[4], type,
                    progress, res[6], durationMinutes);
            case SUBTASK -> {
                int epicId = Integer.parseInt(res[5]);
                yield new Subtask(id, res[2], res[4], type,
                        epicId,
                        progress, res[6], durationMinutes);
            }
        };
    }

    public void save() {
        try (
                FileWriter fileWriter = new FileWriter(dataFile)
        ) {

            fileWriter.write("id,type,name,status,description,epic,startTime,duration,endTime" + "\n");
            for (Task task : getAllTasks().values()) {
                fileWriter.write(toString(task) + "\n");
            }
            System.out.println("Изменения сохранены в файл");
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка сохранения в файл");
        }
    }


    public static FileBackedTaskManager loadFromFile(File file) {
        FileBackedTaskManager manager = new FileBackedTaskManager(file);
        try (
                BufferedReader reader = new BufferedReader(new FileReader(file))
        ) {
            List<Subtask> loadSubtasks = new ArrayList<>();

            reader.readLine();
            while (reader.ready()) {
                String record = reader.readLine();
                Task newTask = fromString(record);

                if (newTask instanceof Epic epic) {
                    System.out.println("Загрузка эпика " + epic.getId());
                    manager.addEpic(epic.getId(), epic);
                } else if (newTask instanceof Subtask subtask) {
                    loadSubtasks.add(subtask);
                } else {
                    System.out.println("Загрузка задачи " + newTask.getId());
                    manager.addTask(newTask.getId(), newTask);
                }
            }

            for (Subtask subtask : loadSubtasks) {
                if (getEpics().get(subtask.getEpicId()) == null) {
                    System.out.println("SavedDataCSV.txt: Эпика для субтаска:" + subtask.getId() + " не существует.");
                } else {
                    System.out.println("Загрузка подзадачи " + subtask.getId());
                    manager.addSubtask(subtask.getId(), subtask);

                }
            }
            System.out.println("Загрузка из файла завершена");

        } catch (IOException e) {
            System.out.println("Ошибка загрузки из файла");
            e.printStackTrace();
        }
        return manager;
    }

    @Override
    public TreeMap<Integer, Task> getAllTasks() {
        return super.getAllTasks();
    }

    @Override
    public void deleteTasksById(TaskType type, int id) {
        super.deleteTasksById(type, id);
        save();
    }


    @Override
    public void addTask(int id, Task task) {
        super.addTask(id, task);
        save();
    }

    @Override
    public void addEpic(int id, Epic epic) {
        super.addEpic(id, epic);
        save();
    }

    @Override
    public void addSubtask(int id, Subtask subtask) {
        super.addSubtask(id, subtask);
        save();
    }

    @Override
    public boolean deleteAllTasks() {
        boolean res = super.deleteAllTasks();
        save();
        return res;
    }


    @Override
    public void createTask(TaskType type, String name, String description, int epicId, TaskProgress status, String startTime, long minutesForDuration, String endTime) {
        super.createTask(type, name, description, epicId, status, startTime, minutesForDuration, endTime);
        save();
    }

    @Override
    public void updateTask(TaskType type, int id, String name, String description, TaskProgress status, int epicId, String startTime, long minutesForDuration, String endTime) {
        super.updateTask(type, id, name, description, status, epicId, startTime, minutesForDuration, endTime);
        save();
    }

    @Override
    public Optional<Subtask> getSubtask(int id) {
        return super.getSubtask(id);
    }
}
