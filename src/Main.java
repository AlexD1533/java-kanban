public class Main {

    public static void main(String[] args) {
        System.out.println("Поехали!");

TaskManager taskManager = new TaskManager();

        taskManager.createTask(TaskType.TASK, "Задача 1", "Выполнить работу", 0, TaskProgress.NEW);
        taskManager.createTask(TaskType.TASK, "Задача 2", "Выполнить работу", 0, TaskProgress.NEW);

        taskManager.createTask(TaskType.EPIC, "Эпик 1", "Выполнить работу", 0, TaskProgress.NEW);
        taskManager.createTask(TaskType.SUBTASK, "Задача 1", "Выполнить работу", 2, TaskProgress.NEW);
        taskManager.createTask(TaskType.SUBTASK, "Задача 2", "Выполнить работу", 2, TaskProgress.NEW);

        taskManager.createTask(TaskType.SUBTASK, "Задача 2", "Выполнить работу", 1, TaskProgress.NEW);

        taskManager.printAllTasks();


        taskManager.updateTask(TaskType.SUBTASK, 4, "work", "blablabla", TaskProgress.IN_PROGRESS, 2);
        taskManager.printAllTasks();
        System.out.println();
        taskManager.getEpicTasks(2);
        System.out.println();

        taskManager.updateTask(TaskType.SUBTASK, 3, "work1", "blablabla22222", TaskProgress.DONE , 2);
        taskManager.updateTask(TaskType.SUBTASK, 4, "work2", "blablabla333333", TaskProgress.NEW, 2);
        taskManager.getEpicTasks(2);


        System.out.println();
        taskManager.printAllTasks();


    }
}
