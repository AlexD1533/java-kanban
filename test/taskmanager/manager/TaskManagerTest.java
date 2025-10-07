package taskmanager.manager;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import taskmanager.model.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
abstract class TaskManagerTest<T extends TaskManager> {
protected abstract T createTaskManager();


    @Test
    @Order(1)
    public void createNewTasks() {
        TaskManager taskManager = createTaskManager();
        taskManager.deleteAllTasks();

        // Создаем задачи с временными параметрами
        taskManager.createTask(TaskType.TASK, "Задача 1", "Выполнить работу", 0,
                TaskProgress.NEW, "2005-12-12T00:00", 120, "2005-12-12T02:00");
        taskManager.createTask(TaskType.EPIC, "Эпик 1", "Выполнить работу", 0,
                TaskProgress.NEW, "2005-12-13T00:00", 120, "2005-12-13T02:00");
        taskManager.createTask(TaskType.SUBTASK, "подзадача 1", "Выполнить работу", 1,
                TaskProgress.NEW, "2005-12-14T00:00", 120, "2005-12-14T02:00");

        taskManager.printAllTasks();
        taskManager.getEpicTasks(1);

        assertNotNull(taskManager.getTask(0), "Такого объекта не существует");
        assertNotNull(taskManager.getEpic(1), "Такого объекта не существует");
        assertNotNull(taskManager.getSubtask(2), "Такого объекта не существует");

        // Создаем задачи для сравнения с временными параметрами
        Task task = new Task(0, "Задача 1", "Выполнить работу", TaskType.TASK,
                TaskProgress.NEW, "2005-12-12T00:00", 120);
        Task task1 = new Task(3, "Задача 2", "Выполнить работу", TaskType.TASK,
                TaskProgress.NEW, "2005-12-15T00:00", 120);

        assertEquals(task.getName(), taskManager.getTask(0).getName(), "Имена задач не совпадают");
        assertEquals(task.getDescription(), taskManager.getTask(0).getDescription(), "Описания не совпадают");

        taskManager.createTask(task1.getType(), task1.getName(), task1.getDescription(), 0,
                task1.getStatus(), task1.getStartTime().toString(), task1.getDuration(),
                task1.getEndTime().toString());

        assertEquals(task1.getType(), taskManager.getTask(3).getType(), "Типы не совпадают");
        assertEquals(task1.getName(), taskManager.getTask(3).getName(), "Имена не совпадают");
        assertEquals(task1.getDescription(), taskManager.getTask(3).getDescription(), "Описания не совпадают");
        assertEquals(task1.getStatus(), taskManager.getTask(3).getStatus(), "Статусы не совпадают");

        // Epic с подзадачами
        HashMap<Integer, Subtask> subtasks = new HashMap<>();
        Subtask existingSubtask = new Subtask(2, "подзадача 1", "Выполнить работу",
                TaskType.SUBTASK, 1, TaskProgress.NEW, "2005-12-14T00:00", 120);
        subtasks.put(2, existingSubtask);

        Epic epic = new Epic(1, "Эпик 1", "Выполнить работу", TaskType.EPIC,
                TaskProgress.NEW, subtasks, "2005-12-13T00:00", 120, "2005-12-13T02:00");
        Epic epic1 = new Epic(4, "Эпик 2", "Выполнить работу", TaskType.EPIC,
                TaskProgress.NEW, new HashMap<>(), "2005-12-16T00:00", 120, "2005-12-16T02:00");

        assertEquals(epic.getName(), taskManager.getEpic(1).getName(), "Имена эпиков не совпадают");

        taskManager.createTask(epic1.getType(), epic1.getName(), epic1.getDescription(), 0,
                epic1.getStatus(), epic1.getStartTime().toString(), epic1.getDuration(),
                epic1.getEndTime().toString());

        assertEquals(epic1.getType(), taskManager.getEpic(4).getType(), "Типы эпиков не совпадают");
        assertEquals(epic1.getName(), taskManager.getEpic(4).getName(), "Имена эпиков не совпадают");
        assertEquals(epic1.getDescription(), taskManager.getEpic(4).getDescription(), "Описания эпиков не совпадают");
        assertEquals(epic1.getStatus(), taskManager.getEpic(4).getStatus(), "Статусы эпиков не совпадают");

        // Subtask с временными параметрами
        Subtask subtask = new Subtask(2, "подзадача 1", "Выполнить работу",
                TaskType.SUBTASK, 1, TaskProgress.NEW, "2005-12-14T00:00", 120);
        Subtask subtask1 = new Subtask(5, "Подзадача 2", "Выполнить работу",
                TaskType.SUBTASK, 1, TaskProgress.NEW, "2005-12-17T00:00", 120);

        assertEquals(subtask.getName(), taskManager.getSubtask(2).getName(), "Имена подзадач не совпадают");
        assertEquals(subtask.getEpicId(), taskManager.getSubtask(2).getEpicId(), "EpicId не совпадают");

        taskManager.createTask(subtask1.getType(), subtask1.getName(), subtask1.getDescription(),
                subtask1.getEpicId(), subtask1.getStatus(), subtask1.getStartTime().toString(),
                subtask1.getDuration(), subtask1.getEndTime().toString());

        assertEquals(subtask1.getType(), taskManager.getSubtask(5).getType(), "Типы подзадач не совпадают");
        assertEquals(subtask1.getName(), taskManager.getSubtask(5).getName(), "Имена подзадач не совпадают");
        assertEquals(subtask1.getDescription(), taskManager.getSubtask(5).getDescription(), "Описания подзадач не совпадают");
        assertEquals(subtask1.getStatus(), taskManager.getSubtask(5).getStatus(), "Статусы подзадач не совпадают");
        assertEquals(subtask1.getEpicId(), taskManager.getSubtask(5).getEpicId(), "EpicId подзадач не совпадают");

        assertTrue(taskManager.deleteAllTasks());
        taskManager.printAllTasks();
        System.out.println();
    }

    @Test
    @Order(2)
    public void shouldReturnStatusEpic() {
        TaskManager taskManager = createTaskManager();
        taskManager.deleteAllTasks();

        // Создаем задачи с временными параметрами
        taskManager.createTask(TaskType.TASK, "Задача 1", "Выполнить работу", 0,
                TaskProgress.NEW, "2005-12-12T00:00", 120, "2005-12-12T02:00");
        taskManager.createTask(TaskType.EPIC, "Эпик 1", "Выполнить работу", 0,
                TaskProgress.NEW, "2005-12-13T00:00", 120, "2005-12-13T02:00");
        taskManager.createTask(TaskType.SUBTASK, "подзадача 1", "Выполнить работу", 1,
                TaskProgress.NEW, "2005-12-14T00:00", 120, "2005-12-14T02:00");

        taskManager.updateTask(TaskType.SUBTASK, 2, "Подзадача hello", "Выполнить работу",
                TaskProgress.IN_PROGRESS, 1, "2005-12-14T00:00", 120, "2005-12-14T02:00");
        assertEquals(TaskProgress.IN_PROGRESS, taskManager.getEpic(1).getStatus(), "Статусы не совпадают");

        taskManager.updateTask(TaskType.SUBTASK, 2, "Подзадача hello", "Выполнить работу",
                TaskProgress.DONE, 1, "2005-12-14T00:00", 120, "2005-12-14T02:00");
        assertEquals(TaskProgress.DONE, taskManager.getEpic(1).getStatus(), "Статусы не совпадают");
    }

    @Test
    @Order(3)
    public void testGetAllTasks() {
        TaskManager taskManager = createTaskManager();
        taskManager.deleteAllTasks();

        taskManager.createTask(TaskType.TASK, "Задача 1", "Описание 1", 0,
                TaskProgress.NEW, "2005-12-12T00:00", 120, "2005-12-12T02:00");
        taskManager.createTask(TaskType.EPIC, "Эпик 1", "Описание 2", 0,
                TaskProgress.NEW, "2005-12-13T00:00", 120, "2005-12-13T02:00");
        taskManager.createTask(TaskType.SUBTASK, "Подзадача 1", "Описание 3", 1,
                TaskProgress.NEW, "2005-12-14T00:00", 120, "2005-12-14T02:00");

        Map<Integer, Task> allTasks = taskManager.getAllTasks();
        assertEquals(3, allTasks.size(), "Неверное количество задач");
        assertTrue(allTasks.containsKey(0), "Задача не найдена");
        assertTrue(allTasks.containsKey(1), "Эпик не найден");
        assertTrue(allTasks.containsKey(2), "Подзадача не найдена");
    }

    @Test
    @Order(4)
    public void testDeleteTaskById() {
        TaskManager taskManager = createTaskManager();
        taskManager.deleteAllTasks();

        taskManager.createTask(TaskType.TASK, "Задача 1", "Описание", 0,
                TaskProgress.NEW, "2005-12-12T00:00", 120, "2005-12-12T02:00");
        taskManager.createTask(TaskType.EPIC, "Эпик 1", "Описание", 0,
                TaskProgress.NEW, "2005-12-13T00:00", 120, "2005-12-13T02:00");
        taskManager.createTask(TaskType.SUBTASK, "Подзадача 1", "Описание", 1,
                TaskProgress.NEW, "2005-12-14T00:00", 120, "2005-12-14T02:00");

        // Удаляем задачу
        taskManager.deleteTasksById(TaskType.TASK, 0);
        assertNull(taskManager.getTask(0), "Задача не удалена");

        // Удаляем подзадачу
        taskManager.deleteTasksById(TaskType.SUBTASK, 2);
        assertNull(taskManager.getSubtask(2), "Подзадача не удалена");

        // Удаляем эпик (должны удалиться и его подзадачи)
        taskManager.deleteTasksById(TaskType.EPIC, 1);
        assertNull(taskManager.getEpic(1), "Эпик не удален");
    }

    @Test
    @Order(5)
    public void testUpdateEpicStatusWithMultipleSubtasks() {
        TaskManager taskManager = createTaskManager();
        taskManager.deleteAllTasks();

        taskManager.createTask(TaskType.EPIC, "Эпик", "Описание", 0,
                TaskProgress.NEW, "2005-12-13T00:00", 120, "2005-12-13T02:00");

        // Создаем несколько подзадач
        taskManager.createTask(TaskType.SUBTASK, "Подзадача 1", "Описание", 0,
                TaskProgress.NEW, "2005-12-14T00:00", 120, "2005-12-14T02:00");
        taskManager.createTask(TaskType.SUBTASK, "Подзадача 2", "Описание", 0,
                TaskProgress.NEW, "2005-12-15T00:00", 120, "2005-12-15T02:00");

        taskManager.printAllTasks();

        // Все подзадачи NEW -> эпик NEW
        assertEquals(TaskProgress.NEW, taskManager.getEpic(0).getStatus(), "Эпик должен быть NEW");

        // Одна подзадача IN_PROGRESS -> эпик IN_PROGRESS
        taskManager.updateTask(TaskType.SUBTASK, 1, "Подзадача 1", "Описание",
                TaskProgress.IN_PROGRESS, 0, "2005-12-14T00:00", 120, "2005-12-14T02:00");
        assertEquals(TaskProgress.IN_PROGRESS, taskManager.getEpic(0).getStatus(), "Эпик должен быть IN_PROGRESS");

        // Все подзадачи DONE -> эпик DONE
        taskManager.updateTask(TaskType.SUBTASK, 1, "Подзадача 1", "Описание",
                TaskProgress.DONE, 0, "2005-12-14T00:00", 120, "2005-12-14T02:00");
        taskManager.updateTask(TaskType.SUBTASK, 2, "Подзадача 2", "Описание",
                TaskProgress.DONE, 0, "2005-12-15T00:00", 120, "2005-12-15T02:00");
        assertEquals(TaskProgress.DONE, taskManager.getEpic(0).getStatus(), "Эпик должен быть DONE");
    }

    @Test
    @Order(6)
    public void testHistory() {
        TaskManager taskManager = createTaskManager();
        taskManager.deleteAllTasks();

        taskManager.createTask(TaskType.TASK, "Задача 1", "Описание", 0,
                TaskProgress.NEW, "2005-12-12T00:00", 120, "2005-12-12T02:00");
        taskManager.createTask(TaskType.EPIC, "Эпик 1", "Описание", 0,
                TaskProgress.NEW, "2005-12-13T00:00", 120, "2005-12-13T02:00");

        // Получаем задачи для добавления в историю
        taskManager.getTask(0);
        taskManager.getEpic(1);
        taskManager.getTask(0); // дублирование для проверки

        List<Task> history = taskManager.getHistory();
        assertEquals(2, history.size(), "История должна содержать 2 уникальные задачи");
        assertEquals(1, history.get(0).getId(), "Первая задача в истории неверна");
        assertEquals(0, history.get(1).getId(), "Вторая задача в истории неверна");
    }

    @Test
    @Order(7)
    public void testGetPrioritizedTasks() {
        TaskManager taskManager = createTaskManager();
        taskManager.deleteAllTasks();

        // Создаем задачи в разном порядке времени
        taskManager.createTask(TaskType.TASK, "Задача 2", "Описание", 0,
                TaskProgress.NEW, "2005-12-13T00:00", 120, "2005-12-13T02:00");
        taskManager.createTask(TaskType.TASK, "Задача 1", "Описание", 0,
                TaskProgress.NEW, "2005-12-12T00:00", 120, "2005-12-12T02:00");
        taskManager.createTask(TaskType.TASK, "Задача 3", "Описание", 0,
                TaskProgress.NEW, "2005-12-14T00:00", 120, "2005-12-14T02:00");

        TreeSet<Task> prioritized = taskManager.getPrioritizedTasks();
        assertEquals(3, prioritized.size(), "Неверное количество приоритетных задач");

        // Проверяем сортировку по времени начала
        assertEquals("Задача 1", prioritized.getFirst().getName(), "Первая задача должна быть Задача 1");

        assertEquals("Задача 3", prioritized.getLast().getName(), "Третья задача должна быть Задача 3");
    }
}
