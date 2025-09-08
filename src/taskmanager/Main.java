package taskmanager;

import taskmanager.manager.*;
import taskmanager.model.*;


public class Main {

    public static void main(String[] args) {
        System.out.println("Поехали!");
        TaskManager taskManager = Managers.getDefault();

        taskManager.createTask(TaskType.TASK, "Задача 1", "Выполнить работу", 0, TaskProgress.NEW);
        taskManager.createTask(TaskType.TASK, "Задача 2", "Выполнить работу", 0, TaskProgress.NEW);
        System.out.println();
        taskManager.createTask(TaskType.EPIC, "Эпик 1", "Выполнить работу", 0, TaskProgress.NEW);
        System.out.println();
        taskManager.createTask(TaskType.SUBTASK, "подзадача 1", "Выполнить работу", 2, TaskProgress.NEW);
        taskManager.printAllTasks();
        System.out.println();

        taskManager.updateTask(TaskType.TASK, 1, "подзадача hello", "Выполнить работу", TaskProgress.IN_PROGRESS, 0);
        taskManager.updateTask(TaskType.SUBTASK, 3, "подзадача hello", "Выполнить работу", TaskProgress.DONE, 2);
        System.out.println();
        taskManager.printAllTasks();
        System.out.println();

        taskManager.printAllTasks();
        System.out.println();

        taskManager.getTask(0);
        taskManager.getTask(1);
        taskManager.getEpic(2);
        taskManager.getSubtask(3);

        taskManager.getTask(0);
        taskManager.getTask(1);
        taskManager.getEpic(2);
        taskManager.getTask(0);
        taskManager.getTask(1);
        taskManager.getEpic(2);
        taskManager.getSubtask(3);
        taskManager.getSubtask(3);
        taskManager.getSubtask(3);
        taskManager.getSubtask(3);
        taskManager.getSubtask(3);
        System.out.println("History");

        for (Task task : taskManager.getHistory()) {
            System.out.println(task);
        }

        taskManager.deleteTasksById(TaskType.TASK, 1);
        System.out.println();

        System.out.println("History after delete task");

        for (Task task : taskManager.getHistory()) {
            System.out.println(task);
        }
    }
}
