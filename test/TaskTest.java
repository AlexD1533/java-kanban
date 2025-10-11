import org.junit.jupiter.api.Test;
import taskmanager.model.*;

import java.time.LocalDateTime;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;

class TaskTest {

    @Test
    public void checkEqualsTasksAndChildes() {
        // Тестируем обычные задачи с временными параметрами
        Task task1 = new Task(1, "Задача 1", "Выполнить работу", TaskType.TASK,
                TaskProgress.NEW, "2005-12-12T00:00", 120);
        Task task2 = new Task(1, "Задача 1", "Выполнить работу", TaskType.TASK,
                TaskProgress.NEW, "2005-12-12T00:00", 120);
        assertEquals(task1, task2, "Задачи не совпадают");

        // Тестируем эпики с временными параметрами
        Task task3 = new Epic(1, "Эпик 1", "Выполнить работу", TaskType.EPIC,
                TaskProgress.NEW, new HashMap<>());
        Task task4 = new Epic(1, "Эпик 1", "Выполнить работу", TaskType.EPIC,
                TaskProgress.NEW, new HashMap<>());
        assertEquals(task3, task4, "Эпики не совпадают");

        // Тестируем подзадачи с временными параметрами
        Task task5 = new Subtask(1, "Подзадача 1", "Выполнить работу", TaskType.SUBTASK,
                1, TaskProgress.NEW, "2005-12-14T00:00", 120);
        Task task6 = new Subtask(1, "Подзадача 1", "Выполнить работу", TaskType.SUBTASK,
                1, TaskProgress.NEW, "2005-12-14T00:00", 120);
        assertEquals(task5, task6, "Подзадачи не совпадают");
    }

    @Test
    public void shouldNotCastEpicToSubtask() {
        Epic epic1 = new Epic(1, "Эпик 1", "Выполнить работу", TaskType.EPIC,
                TaskProgress.NEW, new HashMap<>());
        assertThrows(ClassCastException.class, () -> {
            Subtask currentSubtask = (Subtask) (Task) epic1;
            epic1.getSubtasks().put(currentSubtask.getId(), currentSubtask);
        }, "Классы нельзя привести, ошибка");
    }

    @Test
    public void shouldNotCastSubtaskToEpic() {
        Subtask subtask = new Subtask(1, "Подзадача 1", "Выполнить работу", TaskType.SUBTASK,
                1, TaskProgress.NEW, "2005-12-14T00:00", 120);
        assertThrows(ClassCastException.class, () -> {
            Epic currentEpic = (Epic) (Task) subtask;
        }, "Классы нельзя привести, ошибка");
    }

    @Test
    public void testTaskConstructorAndGetters() {
        LocalDateTime startTime = LocalDateTime.parse("2005-12-12T00:00");
        Task task = new Task(1, "Задача", "Описание", TaskType.TASK,
                TaskProgress.IN_PROGRESS, "2005-12-12T00:00", 120);

        assertEquals(1, task.getId(), "ID не совпадает");
        assertEquals("Задача", task.getName(), "Имя не совпадает");
        assertEquals("Описание", task.getDescription(), "Описание не совпадает");
        assertEquals(TaskType.TASK, task.getType(), "Тип не совпадает");
        assertEquals(TaskProgress.IN_PROGRESS, task.getStatus(), "Статус не совпадает");
        assertEquals(startTime, task.getStartTime(), "Время начала не совпадает");
        assertEquals(120, task.getDuration(), "Продолжительность не совпадает");
        assertEquals(startTime.plusMinutes(120), task.getEndTime(), "Время окончания не совпадает");
    }

    @Test
    public void testEpicConstructorAndGetters() {
        HashMap<Integer, Subtask> subtasks = new HashMap<>();
        Subtask subtask = new Subtask(2, "Подзадача", "Описание", TaskType.SUBTASK,
                1, TaskProgress.NEW, "2005-12-13T00:00", 60);
        subtasks.put(2, subtask);

        Epic epic = new Epic(1, "Эпик", "Описание эпика", TaskType.EPIC,
                TaskProgress.NEW, subtasks);

        assertEquals(1, epic.getId(), "ID не совпадает");
        assertEquals("Эпик", epic.getName(), "Имя не совпадает");
        assertEquals("Описание эпика", epic.getDescription(), "Описание не совпадает");
        assertEquals(TaskType.EPIC, epic.getType(), "Тип не совпадает");
        assertEquals(TaskProgress.NEW, epic.getStatus(), "Статус не совпадает");
        assertEquals(subtasks, epic.getSubtasks(), "Подзадачи не совпадают");
        assertEquals(1, epic.getSubtasks().size(), "Количество подзадач не совпадает");
        assertEquals(LocalDateTime.parse("2005-12-13T00:00"), epic.getStartTime(), "Время начала не совпадает");
        assertEquals(60, epic.getDuration(), "Продолжительность не совпадает");

    }

    @Test
    public void testSubtaskConstructorAndGetters() {
        Subtask subtask = new Subtask(1, "Подзадача", "Описание", TaskType.SUBTASK,
                2, TaskProgress.DONE, "2005-12-14T00:00", 90);

        assertEquals(1, subtask.getId(), "ID не совпадает");
        assertEquals("Подзадача", subtask.getName(), "Имя не совпадает");
        assertEquals("Описание", subtask.getDescription(), "Описание не совпадает");
        assertEquals(TaskType.SUBTASK, subtask.getType(), "Тип не совпадает");
        assertEquals(2, subtask.getEpicId(), "EpicId не совпадает");
        assertEquals(TaskProgress.DONE, subtask.getStatus(), "Статус не совпадает");
        assertEquals(LocalDateTime.parse("2005-12-14T00:00"), subtask.getStartTime(), "Время начала не совпадает");
        assertEquals(90, subtask.getDuration(), "Продолжительность не совпадает");
        assertEquals(LocalDateTime.parse("2005-12-14T01:30"), subtask.getEndTime(), "Время окончания не совпадает");
    }

    @Test
    public void testTaskEqualityById() {
        Task task1 = new Task(1, "Задача 1", "Описание 1", TaskType.TASK,
                TaskProgress.NEW, "2005-12-12T00:00", 120);
        Task task2 = new Task(1, "Задача 2", "Описание 2", TaskType.TASK,
                TaskProgress.DONE, "2005-12-13T00:00", 60);

        // Две задачи с одинаковым ID должны быть равны, даже если другие поля разные
        assertEquals(task1, task2, "Задачи с одинаковым ID должны быть равны");
        assertEquals(task1.hashCode(), task2.hashCode(), "HashCode должен совпадать для одинаковых ID");
    }

    @Test
    public void testTaskInequality() {
        Task task1 = new Task(1, "Задача", "Описание", TaskType.TASK,
                TaskProgress.NEW, "2005-12-12T00:00", 120);
        Task task2 = new Task(2, "Задача", "Описание", TaskType.TASK,
                TaskProgress.NEW, "2005-12-12T00:00", 120);

        assertNotEquals(task1, task2, "Задачи с разными ID не должны быть равны");
        assertNotEquals(task1.hashCode(), task2.hashCode(), "HashCode должен различаться для разных ID");
    }


}