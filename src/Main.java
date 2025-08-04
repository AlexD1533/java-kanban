public class Main {

    public static void main(String[] args) {
        System.out.println("Поехали!");

TaskManager taskManager = new TaskManager();

        taskManager.createTask(TaskType.TASK, "Задача 1", "Выполнить работу", 0);
        taskManager.createTask(TaskType.TASK, "Задача 2", "Выполнить работу", 0);

        taskManager.createTask(TaskType.EPIC, "Эпик 1", "Выполнить работу", 0);
        taskManager.createTask(TaskType.SUBTASK, "Задача 1", "Выполнить работу", 2);
        taskManager.createTask(TaskType.SUBTASK, "Задача 2", "Выполнить работу", 2);

        taskManager.createTask(TaskType.SUBTASK, "Задача 2", "Выполнить работу", 1);

        taskManager.printAllTasks();


        taskManager.editTask(4, "work", "blablabla", TaskProgress.IN_PROGRESS, 2);
        taskManager.printAllTasks();
        System.out.println();
        taskManager.getEpicTasks(2);
    }
}
