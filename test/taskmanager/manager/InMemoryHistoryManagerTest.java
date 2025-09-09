package taskmanager.manager;

import org.junit.jupiter.api.Test;
import taskmanager.model.Task;
import taskmanager.model.TaskProgress;
import taskmanager.model.TaskType;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryHistoryManagerTest {

    HistoryManager testHistory = Managers.getDefaultHistory();

    Task task1 = new Task(1, "Задача 1", "Выполнить работу", TaskType.TASK, TaskProgress.NEW);
    Task task2 = new Task(1, "Задача Новая версия", "Выполнить работу", TaskType.TASK, TaskProgress.NEW);

    @Test
    public void checkAddTask() {

        testHistory.addTask(task1);
        assertNotNull(testHistory.getHistory(), "История не должна быть пустой");

    }

    @Test
    public void shouldBeDeleteOldVersionAndDouble() {

        testHistory.addTask(task2);
        assertEquals(testHistory.getHistory().size(), 1, "Должен быть один элемент в истории");
        assertEquals(task2.getName(), testHistory.getHistory().get(0).getName(), "Версии должны быть равны");
        assertNotEquals(task1.getName(), testHistory.getHistory().get(0).getName(), "Версии не должны быть равны");

    }

    @Test
    public void shouldBeCheckDeleteAndCreateNullObject() {
        Task task1 = new Task(1, "Задача 1", "Выполнить работу", TaskType.TASK, TaskProgress.NEW);
        testHistory.addTask(task1);
        System.out.println(testHistory.getHistory());
        testHistory.remove(1);
        assertEquals(testHistory.getHistory().size(), 0, "История должна быть пустой");
        System.out.println(testHistory.getHistory());

        testHistory.addTask(null);
        System.out.println(testHistory.getHistory());

        testHistory.remove(3);
        assertEquals(testHistory.getHistory().size(), 0, "История должна быть пустой");

    }

    @Test
    void getHistory() {
    }
}