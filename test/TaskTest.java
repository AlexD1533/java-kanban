import org.junit.jupiter.api.Test;
import taskmanager.model.*;

import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;

class TaskTest {
    @Test
    public void checkEqualsTasksAndChildes() {

        Task task1 = new Task(1, "Задача 1", "Выполнить работу", TaskType.TASK, TaskProgress.NEW);
        Task task2 = new Task(1, "Задача 1", "Выполнить работу", TaskType.TASK, TaskProgress.NEW);
        assertEquals(task1, task2, "Задачи не совпадают");

        Task task3 = new Epic(1, "Эпик 1", "Выполнить работу", TaskType.EPIC, TaskProgress.NEW, new HashMap<>());
        Task task4 = new Epic(1, "Эпик 1", "Выполнить работу", TaskType.EPIC, TaskProgress.NEW, new HashMap<>());
        assertEquals(task3, task4, "Эпики не совпадают");

        Task task5 = new Subtask(1, "Подзадача 1", "Выполнить работу", TaskType.SUBTASK, 1, TaskProgress.NEW);
        Task task6 = new Subtask(1, "Подзадача 1", "Выполнить работу", TaskType.SUBTASK, 1, TaskProgress.NEW);
        assertEquals(task5, task6, "Подзадачи не совпадают");
    }

    @Test
    public void shouldNotCastEpicToSubtask() {
        Epic epic1 = new Epic(1, "Эпик 1", "Выполнить работу", TaskType.EPIC, TaskProgress.NEW, new HashMap<>());
        assertThrows(ClassCastException.class, () -> {
            Subtask currentSubtask = (Subtask) (Task) epic1;
            epic1.getSubtasks().put(currentSubtask.getId(), currentSubtask);
        }, "Классы нельзя привести, ошибка");
    }

    @Test
    public void shouldNotCastSubtaskToEpic() {
        Subtask subtask = new Subtask(1, "Подзадача 1", "Выполнить работу", TaskType.SUBTASK, 1, TaskProgress.NEW);
        assertThrows(ClassCastException.class, () -> {
            Epic currentEpic = (Epic) (Task) subtask;

        }, "Классы нельзя привести, ошибка");
    }
}