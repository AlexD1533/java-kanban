package taskmanager.manager;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import taskmanager.model.*;

import java.io.File;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.TreeMap;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class FileBackedTaskManagerTest {

    FileBackedTaskManager taskManager = new FileBackedTaskManager(new File("SavedDataCSV.txt"));

    @Test
    @Order(3)
    public void saveAndLoadEmptyFile() {
        System.out.println();
        System.out.println("Удаляем все задачи");
        taskManager.deleteAllTasks();
        System.out.println("Сохраняем изменения в файл");
        taskManager.save();

        assertEquals(0, taskManager.getAllTasks().size(), "Ошибка сохранения пустого файла");
        taskManager.printAllTasks();
        System.out.println();
        System.out.println("Загружаем содержимое файла");
        taskManager.loadFromFile(new File("SavedDataCSV.txt"));
        taskManager.printAllTasks();
        assertEquals(0, taskManager.getAllTasks().size(), "Ошибка загрузки пустого файла");
    }

    @Test
    @Order(4)
    public void saveAndLoadFiles() {
        TreeMap<Integer, Task> mapBeforeDownload = new TreeMap<>();
        TreeMap<Integer, Task> mapAfterDownload = new TreeMap<>();

        System.out.println();
        System.out.println("Удаляем все задачи");
        taskManager.deleteAllTasks();

        // Создаем задачи с временными параметрами
        taskManager.createTask(TaskType.TASK, "Задача 1", "Выполнить работу", 0,
                TaskProgress.NEW, "2005-12-12T00:00", 120, "2005-12-12T02:00");
        taskManager.createTask(TaskType.EPIC, "Эпик 1", "Выполнить работу", 0,
                TaskProgress.NEW, "2005-12-13T00:00", 120, "2005-12-13T02:00");
        taskManager.createTask(TaskType.SUBTASK, "подзадача 1", "Выполнить работу", 1,
                TaskProgress.NEW, "2005-12-14T00:00", 120, "2005-12-14T02:00");

        taskManager.printAllTasks();

        System.out.println("Сохраняем изменения в файл");
        taskManager.save();
        mapBeforeDownload = taskManager.getAllTasks();

        taskManager.loadFromFile(new File("SavedDataCSV.txt"));
        mapAfterDownload = taskManager.getAllTasks();

        assertEquals(mapBeforeDownload.size(), mapAfterDownload.size(),
                "Ошибка сохранения и загрузки нескольких файлов. Размеры данных отличаются");
    }

}