package taskmanager.manager;

import org.junit.jupiter.api.Test;
import taskmanager.model.TaskProgress;
import taskmanager.model.TaskType;

import static org.junit.jupiter.api.Assertions.*;

class ManagersTest {

  TaskManager taskManager = Managers.getDefault();
  @Test
          public void shouldReturnReadyManagersExamples () {
      taskManager.createTask(TaskType.TASK, "Задача 1", "Выполнить работу", 0, TaskProgress.NEW);
      taskManager.createTask(TaskType.EPIC, "Эпик 1", "Выполнить работу", 0, TaskProgress.NEW);
      taskManager.createTask(TaskType.SUBTASK, "подзадача 1", "Выполнить работу", 1, TaskProgress.NEW);

      System.out.println("Test manager print");
      taskManager.printAllTasks();

      assertNotNull(taskManager.getTask(0));
      assertNotNull(taskManager.getEpic(1));
      assertNotNull(taskManager.getSubtask(2));
      assertNotNull(taskManager.getHistoryManager().getHistory());


  }
}