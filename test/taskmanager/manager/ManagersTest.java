package taskmanager.manager;

import org.junit.jupiter.api.Test;
import taskmanager.model.TaskProgress;
import taskmanager.model.TaskType;

import static org.junit.jupiter.api.Assertions.*;

class ManagersTest {
    @Test
    public void shouldReturnInitializedTaskManager() {

        TaskManager manager = Managers.getDefault();
        assertNotNull(manager, "Объект не может быть равен нулю");
        assertDoesNotThrow(() -> manager.createTask(TaskType.TASK, "Задача 1", "Выполнить работу", 0, TaskProgress.NEW, "2005-12-14T00:00", 120, "2005-12-14T02:00"));
    }

    @Test
    public void shouldReturnInitializedHistoryManager() {
        HistoryManager historyManager = Managers.getDefaultHistory();
        assertNotNull(historyManager, "Объект не может быть равен нулю");
        assertTrue(historyManager.getHistory().isEmpty(), "Список должен быть пустым");

    }
}