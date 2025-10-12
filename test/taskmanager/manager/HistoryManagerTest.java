package taskmanager.manager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import taskmanager.model.Task;
import taskmanager.model.TaskProgress;
import taskmanager.model.TaskType;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

abstract class HistoryManagerTest<T extends HistoryManager> {
    protected abstract T createHistoryManager();

    protected HistoryManager historyManager;
    protected Task task1;
    protected Task task2;
    protected Task task3;

    @BeforeEach
    void setUp() {
        historyManager = createHistoryManager();

        // Создаем тестовые задачи
        task1 = new Task(1, "Task 1", "Description 1", TaskType.TASK,
                TaskProgress.NEW, "2023-01-01T10:00", 60);
        task2 = new Task(2, "Task 2", "Description 2", TaskType.TASK,
                TaskProgress.IN_PROGRESS, "2023-01-01T11:00", 60);
        task3 = new Task(3, "Task 3", "Description 3", TaskType.TASK,
                TaskProgress.DONE, "2023-01-01T12:00", 60);
    }

    @Test
    void getHistoryWhenEmpty() {
        List<Task> history = historyManager.getHistory();
        assertTrue(history.isEmpty(), "История должна быть пустой");
    }

    @Test
    void addTaskToHistory() {
        historyManager.addTask(task1);
        List<Task> history = historyManager.getHistory();

        assertEquals(1, history.size(), "История должна содержать 1 задачу");
        assertEquals(task1, history.get(0), "Задача в истории должна совпадать с добавленной");
    }

    // b. Дублирование
    @Test
    void addDuplicateTask() {
        historyManager.addTask(task1);
        historyManager.addTask(task1);
        List<Task> history = historyManager.getHistory();

        assertEquals(1, history.size(), "История должна содержать только одну копию задачи");
        assertEquals(task1, history.get(0), "Задача должна быть той же");
    }

    @Test
    void addMultipleTasks() {
        historyManager.addTask(task1);
        historyManager.addTask(task2);
        historyManager.addTask(task3);

        List<Task> history = historyManager.getHistory();

        assertEquals(3, history.size(), "История должна содержать 3 задачи");
        assertEquals(task1, history.get(0), "Первая задача не совпадает");
        assertEquals(task2, history.get(1), "Вторая задача не совпадает");
        assertEquals(task3, history.get(2), "Третья задача не совпадает");
    }

    @Test
    void addDuplicateInMiddle() {
        historyManager.addTask(task1);
        historyManager.addTask(task2);
        historyManager.addTask(task3);
        historyManager.addTask(task2);

        List<Task> history = historyManager.getHistory();

        assertEquals(3, history.size(), "История должна содержать 3 уникальные задачи");
        assertEquals(task1, history.get(0), "Первая задача не совпадает");
        assertEquals(task3, history.get(1), "Вторая задача должна быть task3");
        assertEquals(task2, history.get(2), "Последняя задача должна быть task2 (перемещена в конец)");
    }

    @Test
    void removeFromBeginning() {
        historyManager.addTask(task1);
        historyManager.addTask(task2);
        historyManager.addTask(task3);

        historyManager.remove(task1.getId());

        List<Task> history = historyManager.getHistory();

        assertEquals(2, history.size(), "История должна содержать 2 задачи");
        assertEquals(task2, history.get(0), "Первой должна быть task2");
        assertEquals(task3, history.get(1), "Второй должна быть task3");
        assertFalse(history.contains(task1), "История не должна содержать удаленную задачу");
    }


    @Test
    void removeFromMiddle() {
        historyManager.addTask(task1);
        historyManager.addTask(task2);
        historyManager.addTask(task3);

        historyManager.remove(task2.getId());

        List<Task> history = historyManager.getHistory();

        assertEquals(2, history.size(), "История должна содержать 2 задачи");
        assertEquals(task1, history.get(0), "Первой должна быть task1");
        assertEquals(task3, history.get(1), "Второй должна быть task3");
        assertFalse(history.contains(task2), "История не должна содержать удаленную задачу");
    }

    @Test
    void removeFromEnd() {
        historyManager.addTask(task1);
        historyManager.addTask(task2);
        historyManager.addTask(task3);

        historyManager.remove(task3.getId());

        List<Task> history = historyManager.getHistory();

        assertEquals(2, history.size(), "История должна содержать 2 задачи");
        assertEquals(task1, history.get(0), "Первой должна быть task1");
        assertEquals(task2, history.get(1), "Второй должна быть task2");
        assertFalse(history.contains(task3), "История не должна содержать удаленную задачу");
    }

    @Test
    void removeNonExistentTask() {
        historyManager.addTask(task1);
        historyManager.addTask(task2);

        historyManager.remove(999);

        List<Task> history = historyManager.getHistory();

        assertEquals(2, history.size(), "История должна остаться неизменной");
        assertTrue(history.contains(task1), "История должна содержать task1");
        assertTrue(history.contains(task2), "История должна содержать task2");
    }

    @Test
    void removeFromSingleElementHistory() {
        historyManager.addTask(task1);

        historyManager.remove(task1.getId());

        List<Task> history = historyManager.getHistory();
        assertTrue(history.isEmpty(), "История должна быть пустой после удаления единственной задачи");
    }
}
