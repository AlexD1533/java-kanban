package taskmanager.manager;

import org.junit.jupiter.api.Test;
import taskmanager.model.*;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryTaskManagerTest {

    TaskManager taskManager = new InMemoryTaskManager();

    @Test
    public void createNewTasks() {

        taskManager.createTask(TaskType.TASK, "Задача 1", "Выполнить работу", 0, TaskProgress.NEW);
        taskManager.createTask(TaskType.EPIC, "Эпик 1", "Выполнить работу", 0, TaskProgress.NEW);
        taskManager.createTask(TaskType.SUBTASK, "подзадача 1", "Выполнить работу", 1, TaskProgress.NEW);

        assertNotNull(taskManager.getTask(0), "Такого объекта не существует");
        assertNotNull(taskManager.getEpic(1), "Такого объекта не существует");
        assertNotNull(taskManager.getSubtask(2), "Такого объекта не существует");

        Task task = new Task(0, "Задача 2", "Выполнить работу", TaskType.TASK, TaskProgress.NEW);
        Task task1 = new Task(3, "Задача 2", "Выполнить работу", TaskType.TASK, TaskProgress.NEW);
        assertEquals(taskManager.getTask(0), task, "Список не пуст");
        assertEquals(taskManager.getTask(0), task, "Объекты не равны, ошибка");

        taskManager.createTask(task1.getType(), task1.getName(), task1.getDescription(), 0, task1.getStatus());
        assertEquals(task1.getType(), taskManager.getTask(3).getType(), "Объект изменился, объекты не равны");
        assertEquals(task1.getName(), taskManager.getTask(3).getName(), "Объект изменился, объекты не равны");
        assertEquals(task1.getDescription(), taskManager.getTask(3).getDescription(), "Объект изменился, объекты не равны");
        assertEquals(task1.getStatus(), taskManager.getTask(3).getStatus(), "Объект изменился, объекты не равны");

        Epic epic = new Epic(1, "Эпик 2", "Выполнить работу", TaskType.EPIC, TaskProgress.NEW);
        assertEquals(taskManager.getEpic(1), epic, "Объекты не равны, ошибка");
        Epic epic1 = new Epic(4, "Эпик 2", "Выполнить работу", TaskType.EPIC, TaskProgress.NEW);

        taskManager.createTask(epic1.getType(), epic1.getName(), epic1.getDescription(), 0, epic1.getStatus());
        assertEquals(epic1.getType(), taskManager.getEpic(4).getType(), "Объект изменился, объекты не равны");
        assertEquals(epic1.getName(), taskManager.getEpic(4).getName(), "Объект изменился, объекты не равны");
        assertEquals(epic1.getDescription(), taskManager.getEpic(4).getDescription(), "Объект изменился, объекты не равны");
        assertEquals(epic1.getStatus(), taskManager.getEpic(4).getStatus(), "Объект изменился, объекты не равны");
        assertEquals(epic1.getSubtasks(), taskManager.getEpic(4).getSubtasks(), "Объект изменился, объекты не равны");

        Subtask subtask = new Subtask(2, "Подзадача 2", "Выполнить работу", TaskType.SUBTASK, 1, TaskProgress.NEW);
        assertEquals(taskManager.getSubtask(2), subtask, "Объекты не равны, ошибка");
        Subtask subtask1 = new Subtask(1, "Подзадача 2", "Выполнить работу", TaskType.SUBTASK, 1, TaskProgress.NEW);

        taskManager.createTask(subtask1.getType(), subtask1.getName(), subtask1.getDescription(), subtask1.getEpicId(), subtask1.getStatus());
        assertEquals(subtask1.getType(), taskManager.getSubtask(5).getType(), "Объект изменился, объекты не равны");
        assertEquals(subtask1.getName(), taskManager.getSubtask(5).getName(), "Объект изменился, объекты не равны");
        assertEquals(subtask1.getDescription(), taskManager.getSubtask(5).getDescription(), "Объект изменился, объекты не равны");
        assertEquals(subtask1.getStatus(), taskManager.getSubtask(5).getStatus(), "Объект изменился, объекты не равны");
        assertEquals(subtask1.getEpicId(), taskManager.getSubtask(5).getEpicId(), "Объект изменился, объекты не равны");

        assertTrue(taskManager.deleteAllTasks());
        taskManager.printAllTasks();
        taskManager.deleteAllTasks();
        System.out.println();
    }

    @Test
    public void shouldSaveOldParametersHistory() {
        taskManager.createTask(TaskType.TASK, "Задача 1", "Выполнить работу", 0, TaskProgress.NEW);
        taskManager.createTask(TaskType.EPIC, "Эпик 1", "Выполнить работу", 0, TaskProgress.NEW);
        taskManager.createTask(TaskType.SUBTASK, "подзадача 1", "Выполнить работу", 1, TaskProgress.NEW);
        System.out.println();
        taskManager.printAllTasks();

        taskManager.getTask(0);
        taskManager.getEpic(1);
        taskManager.getSubtask(2);

        System.out.println(taskManager.getHistory());
        Task oldTask = taskManager.getTask(0);
        taskManager.updateTask(TaskType.TASK, 0, "задача hello", "Выполнить работу", TaskProgress.IN_PROGRESS, 0);
        taskManager.getTask(0);
        System.out.println();
        System.out.println(taskManager.getHistory());
        assertEquals(oldTask.getName(), taskManager.getHistory().get(0).getName(),
                "Старая версия объекта не сохранилась");

        System.out.println();
        taskManager.printAllTasks();

        Epic oldEpic = taskManager.getEpic(1);
        taskManager.updateTask(TaskType.EPIC, 1, "Эпик hello", "Выполнить работу", TaskProgress.IN_PROGRESS, 0);
        taskManager.getEpic(1);
        System.out.println();
        System.out.println(taskManager.getHistory());
        assertEquals(oldEpic.getName(), taskManager.getHistory().get(1).getName(),
                "Старая версия объекта не сохранилась");

        Subtask oldSubtask = taskManager.getSubtask(2);

        taskManager.updateTask(TaskType.SUBTASK, 2, "Подзадача hello", "Выполнить работу", TaskProgress.IN_PROGRESS, 1);
        taskManager.getSubtask(2);
        System.out.println();
        System.out.println(taskManager.getHistory());
        assertEquals(oldSubtask.getName(), taskManager.getHistory().get(2).getName(),
                "Старая версия объекта не сохранилась");
    }

    @Test
    public void shouldReturnStatusEpic() {

        taskManager.deleteAllTasks();
        taskManager.createTask(TaskType.TASK, "Задача 1", "Выполнить работу", 0, TaskProgress.NEW);
        taskManager.createTask(TaskType.EPIC, "Эпик 1", "Выполнить работу", 0, TaskProgress.NEW);
        taskManager.createTask(TaskType.SUBTASK, "подзадача 1", "Выполнить работу", 1, TaskProgress.NEW);
        taskManager.updateTask(TaskType.SUBTASK, 2, "Подзадача hello", "Выполнить работу", TaskProgress.IN_PROGRESS, 1);
        assertEquals(taskManager.getEpic(1).getStatus(), TaskProgress.IN_PROGRESS, "Статусы не совпадают");
        taskManager.updateTask(TaskType.SUBTASK, 2, "Подзадача hello", "Выполнить работу", TaskProgress.DONE, 1);
        assertEquals(taskManager.getEpic(1).getStatus(), TaskProgress.DONE, "Статусы не совпадают");

    }
}