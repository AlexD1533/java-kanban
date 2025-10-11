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

        taskManager.createTask(TaskType.TASK, "Задача 1", "Выполнить работу", 0,
                TaskProgress.NEW, "2005-12-12T00:00", 120, "2005-12-12T02:00");
        taskManager.createTask(TaskType.EPIC, "Эпик 1", "Выполнить работу", 0,
                TaskProgress.NEW, "2005-12-13T00:00", 120, "2005-12-13T02:00");
        taskManager.createTask(TaskType.SUBTASK, "подзадача 1", "Выполнить работу", 1,
                TaskProgress.NEW, "2005-12-14T00:00", 120, "2005-12-14T02:00");


        taskManager.printAllTasks();
        taskManager.printEpicSubtasks(1);

        assertTrue(taskManager.getTask(0).isPresent(), "Такого объекта не существует");
        assertTrue(taskManager.getEpic(1).isPresent(), "Такого объекта не существует");
        assertTrue(taskManager.getSubtask(2).isPresent(), "Такого объекта не существует");


        Task task = new Task(0, "Задача 1", "Выполнить работу", TaskType.TASK,
                TaskProgress.NEW, "2005-12-12T00:00", 120);
        Task task1 = new Task(3, "Задача 2", "Выполнить работу", TaskType.TASK,
                TaskProgress.NEW, "2005-12-15T00:00", 120);

        assertEquals(task.getName(), taskManager.getTask(0).get().getName(), "Имена задач не совпадают");
        assertEquals(task.getDescription(), taskManager.getTask(0).get().getDescription(), "Описания не совпадают");

        taskManager.createTask(task1.getType(), task1.getName(), task1.getDescription(), 0,
                task1.getStatus(), task1.getStartTime().toString(), task1.getDuration(),
                task1.getEndTime().toString());

        assertTrue(taskManager.getTask(3).isPresent(), "Такого объекта не существует");

        assertEquals(task1.getType(), taskManager.getTask(3).get().getType(), "Типы не совпадают");
        assertEquals(task1.getName(), taskManager.getTask(3).get().getName(), "Имена не совпадают");
        assertEquals(task1.getDescription(), taskManager.getTask(3).get().getDescription(), "Описания не совпадают");
        assertEquals(task1.getStatus(), taskManager.getTask(3).get().getStatus(), "Статусы не совпадают");


        HashMap<Integer, Subtask> subtasks = new HashMap<>();
        Subtask existingSubtask = new Subtask(2, "подзадача 1", "Выполнить работу",
                TaskType.SUBTASK, 1, TaskProgress.NEW, "2005-12-14T00:00", 120);
        subtasks.put(2, existingSubtask);

        Epic epic = new Epic(1, "Эпик 1", "Выполнить работу", TaskType.EPIC,
                TaskProgress.NEW, subtasks);
        Epic epic1 = new Epic(4, "Эпик 2", "Выполнить работу", TaskType.EPIC,
                TaskProgress.NEW, new HashMap<>());


        assertTrue(taskManager.getEpic(1).isPresent(), "Такого объекта не существует");

        assertEquals(epic.getName(), taskManager.getEpic(1).get().getName(), "Имена эпиков не совпадают");

        taskManager.createTask(epic1.getType(), epic1.getName(), epic1.getDescription(), 0,
                epic1.getStatus(), epic1.getStartTime().toString(), epic1.getDuration(),
                epic1.getEndTime().toString());

        assertTrue(taskManager.getEpic(4).isPresent(), "Такого объекта не существует");

        assertEquals(epic1.getType(), taskManager.getEpic(4).get().getType(), "Типы эпиков не совпадают");
        assertEquals(epic1.getName(), taskManager.getEpic(4).get().getName(), "Имена эпиков не совпадают");
        assertEquals(epic1.getDescription(), taskManager.getEpic(4).get().getDescription(), "Описания эпиков не совпадают");
        assertEquals(epic1.getStatus(), taskManager.getEpic(4).get().getStatus(), "Статусы эпиков не совпадают");

        // Subtask с временными параметрами
        Subtask subtask = new Subtask(2, "подзадача 1", "Выполнить работу",
                TaskType.SUBTASK, 1, TaskProgress.NEW, "2005-12-14T00:00", 120);
        Subtask subtask1 = new Subtask(5, "Подзадача 2", "Выполнить работу",
                TaskType.SUBTASK, 1, TaskProgress.NEW, "2005-12-17T00:00", 120);

        assertTrue(taskManager.getSubtask(2).isPresent(), "Такого объекта не существует");

        assertEquals(subtask.getName(), taskManager.getSubtask(2).get().getName(), "Имена подзадач не совпадают");
        assertEquals(subtask.getEpicId(), taskManager.getSubtask(2).get().getEpicId(), "EpicId не совпадают");

        taskManager.createTask(subtask1.getType(), subtask1.getName(), subtask1.getDescription(),
                subtask1.getEpicId(), subtask1.getStatus(), subtask1.getStartTime().toString(),
                subtask1.getDuration(), subtask1.getEndTime().toString());

        assertTrue(taskManager.getSubtask(5).isPresent(), "Такого объекта не существует");

        assertEquals(subtask1.getType(), taskManager.getSubtask(5).get().getType(), "Типы подзадач не совпадают");
        assertEquals(subtask1.getName(), taskManager.getSubtask(5).get().getName(), "Имена подзадач не совпадают");
        assertEquals(subtask1.getDescription(), taskManager.getSubtask(5).get().getDescription(), "Описания подзадач не совпадают");
        assertEquals(subtask1.getStatus(), taskManager.getSubtask(5).get().getStatus(), "Статусы подзадач не совпадают");
        assertEquals(subtask1.getEpicId(), taskManager.getSubtask(5).get().getEpicId(), "EpicId подзадач не совпадают");

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
        taskManager.createTask(TaskType.SUBTASK, "подзадача 2", "Выполнить работу", 1,
                TaskProgress.NEW, "2005-12-15T00:00", 120, "2005-12-15T02:00");

        taskManager.updateTask(TaskType.SUBTASK, 2, "Подзадача hello", "Выполнить работу",
                TaskProgress.NEW, 1, "2005-12-14T00:00", 120, "2005-12-14T02:00");
        taskManager.updateTask(TaskType.SUBTASK, 3, "Подзадача hello", "Выполнить работу",
                TaskProgress.NEW, 1, "2005-12-15T00:00", 120, "2005-12-15T02:00");

        assertTrue(taskManager.getEpic(1).isPresent(), "Такого объекта не существует");

        assertEquals(TaskProgress.NEW, taskManager.getEpic(1).get().getStatus(), "Статусы не совпадают");


        taskManager.updateTask(TaskType.SUBTASK, 2, "Подзадача hello", "Выполнить работу",
                TaskProgress.IN_PROGRESS, 1, "2005-12-14T00:00", 120, "2005-12-14T02:00");
        taskManager.updateTask(TaskType.SUBTASK, 3, "Подзадача hello", "Выполнить работу",
                TaskProgress.IN_PROGRESS, 1, "2005-12-15T00:00", 120, "2005-12-15T02:00");
        assertEquals(TaskProgress.IN_PROGRESS, taskManager.getEpic(1).get().getStatus(), "Статусы не совпадают");

        taskManager.updateTask(TaskType.SUBTASK, 2, "Подзадача hello", "Выполнить работу",
                TaskProgress.DONE, 1, "2005-12-14T00:00", 120, "2005-12-14T02:00");
        taskManager.updateTask(TaskType.SUBTASK, 3, "Подзадача hello", "Выполнить работу",
                TaskProgress.DONE, 1, "2005-12-15T00:00", 120, "2005-12-15T02:00");
        assertEquals(TaskProgress.DONE, taskManager.getEpic(1).get().getStatus(), "Статусы не совпадают");

        taskManager.updateTask(TaskType.SUBTASK, 2, "Подзадача hello", "Выполнить работу",
                TaskProgress.NEW, 1, "2005-12-14T00:00", 120, "2005-12-14T02:00");
        taskManager.updateTask(TaskType.SUBTASK, 3, "Подзадача hello", "Выполнить работу",
                TaskProgress.DONE, 1, "2005-12-15T00:00", 120, "2005-12-15T02:00");

        assertEquals(TaskProgress.IN_PROGRESS, taskManager.getEpic(1).get().getStatus(), "Статусы не совпадают");
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
        assertFalse(taskManager.getTask(0).isPresent(), "Задача не удалена");

        // Удаляем подзадачу
        taskManager.deleteTasksById(TaskType.SUBTASK, 2);
        assertFalse(taskManager.getSubtask(2).isPresent(), "Подзадача не удалена");

        // Удаляем эпик (должны удалиться и его подзадачи)
        taskManager.deleteTasksById(TaskType.EPIC, 1);
        assertFalse(taskManager.getEpic(1).isPresent(), "Эпик не удален");
    }


    @Test
    @Order(5)
    public void testHistory() {
        TaskManager taskManager = createTaskManager();
        taskManager.deleteAllTasks();

        taskManager.createTask(TaskType.TASK, "Задача 1", "Описание", 0,
                TaskProgress.NEW, "2005-12-12T00:00", 120, "2005-12-12T02:00");
        taskManager.createTask(TaskType.EPIC, "Эпик 1", "Описание", 0,
                TaskProgress.NEW, "2005-12-13T00:00", 120, "2005-12-13T02:00");

        assertTrue(taskManager.getTask(0).isPresent(), "Такого объекта не существует");
        assertTrue(taskManager.getEpic(1).isPresent(), "Такого объекта не существует");


        taskManager.getTask(0).get();
        taskManager.getEpic(1).get();
        taskManager.getTask(0).get();

        List<Task> history = taskManager.getHistory();
        assertEquals(2, history.size(), "История должна содержать 2 уникальные задачи");
        assertEquals(1, history.get(0).getId(), "Первая задача в истории неверна");
        assertEquals(0, history.get(1).getId(), "Вторая задача в истории неверна");
    }

    @Test
    @Order(6)
    public void testGetPrioritizedTasks() {
        TaskManager taskManager = createTaskManager();
        taskManager.deleteAllTasks();

        taskManager.createTask(TaskType.TASK, "Задача 2", "Описание", 0,
                TaskProgress.NEW, "2005-12-13T00:00", 120, "2005-12-13T02:00");
        taskManager.createTask(TaskType.TASK, "Задача 1", "Описание", 0,
                TaskProgress.NEW, "2005-12-12T00:00", 120, "2005-12-12T02:00");
        taskManager.createTask(TaskType.TASK, "Задача 3", "Описание", 0,
                TaskProgress.NEW, "2005-12-14T00:00", 120, "2005-12-14T02:00");

        List<Task> prioritized = taskManager.getPrioritizedTasks();
        assertEquals(3, prioritized.size(), "Неверное количество приоритетных задач");

        assertEquals("Задача 1", prioritized.get(0).getName(), "Первая задача должна быть Задача 1");
        assertEquals("Задача 3", prioritized.get(prioritized.size() - 1).getName(), "Третья задача должна быть Задача 3");
    }

    @Test
    @Order(7)
    public void testIntersections() {

        System.out.println("пересечения");
        TaskManager taskManager = createTaskManager();
        taskManager.deleteAllTasks();

        taskManager.createTask(TaskType.TASK, "Задача 1", "Описание", 0,
                TaskProgress.NEW, "2005-12-12T00:00", 120, "2005-12-12T02:00");
        assertTrue(taskManager.getTask(0).isPresent(), "Такого объекта не существует");

        taskManager.createTask(TaskType.EPIC, "Эпик 1", "Описание", 0,
                TaskProgress.NEW, "2005-12-13T00:00", 120, "2005-12-13T02:00");
        assertTrue(taskManager.getEpic(1).isPresent(), "Эпик должен быть создан");

        taskManager.createTask(TaskType.SUBTASK, "Подзадача 1", "Описание", 1,
                TaskProgress.NEW, "2005-12-12T00:00", 120, "2005-12-14T02:00");
        assertFalse(taskManager.getSubtask(2).isPresent(), "Пересечение с существующей задачей, объект должен быть равен null");


        taskManager.createTask(TaskType.SUBTASK, "Подзадача 2", "Описание", 1,
                TaskProgress.NEW, "2005-12-14T00:00", 120, "2005-12-14T02:00");
        assertTrue(taskManager.getSubtask(3).isPresent(), "Объекты не пересекаются, не должен быть равен null");

        taskManager.createTask(TaskType.TASK, "Задача 2", "Описание", 0,
                TaskProgress.NEW, "2005-12-12T00:00", 120, "2005-12-12T02:00");
        assertFalse(taskManager.getTask(2).isPresent(), "Такого объекта не существует");

        taskManager.createTask(TaskType.TASK, "Задача 3", "Описание", 0,
                TaskProgress.NEW, "2005-12-12T01:00", 60, "2005-12-12T02:00");
        assertFalse(taskManager.getTask(4).isPresent(), "Такого объекта не существует");

        taskManager.createTask(TaskType.TASK, "Задача 4", "Описание", 0,
                TaskProgress.NEW, "2005-12-11T23:00", 120, "2005-12-12T01:00");
        assertFalse(taskManager.getTask(5).isPresent(), "Такого объекта не существует");

        taskManager.createTask(TaskType.TASK, "Задача 5", "Описание", 0,
                TaskProgress.NEW, "2005-12-11T22:00", 60, "2005-12-11T23:00");
        assertTrue(taskManager.getTask(7).isPresent(), "Такого объекта не существует");

        taskManager.createTask(TaskType.TASK, "Задача 6", "Описание", 0,
                TaskProgress.NEW, "2005-12-12T03:00", 60, "2005-12-12T04:00");
        assertTrue(taskManager.getTask(8).isPresent(), "Задача после существующей, не должна быть null");

        taskManager.createTask(TaskType.EPIC, "Эпик 2", "Описание", 0,
                TaskProgress.NEW, "2005-12-12T00:00", 120, "2005-12-12T02:00");
        assertTrue(taskManager.getEpic(9).isPresent(), "Эпик может быть создан в любое время, не должен быть null");

        System.out.println("Тест пересечений завершен");
    }

}


