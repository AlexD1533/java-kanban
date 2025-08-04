import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TaskManager {
    private static int counter = 0;

    private final static Map<Integer, Task> tasks = new HashMap<>();
    private final static Map<Integer, Epic> epics = new HashMap<>();

    public void createTask(TaskType type, String name, String description, int epicId) {
        int id = counter++;
        switch (type) {
            case TASK:
                tasks.put(id, new Task(id, name, description, type));
                System.out.println("Задача создана: " + id + " " + name);
                break;
            case EPIC:
                epics.put(id, new Epic(id, name, description, type));
                System.out.println("Эпик создан: " + id + " " + name);
                break;
            case SUBTASK:
                if (!epics.containsKey(epicId) ) {
                    System.out.println("Такого эпика нет");
                    break;
                }
                    epics.get(epicId).getSubtasks().put(id, new Subtask(id, name, description, epicId, type));
                    System.out.println("Подзадача создана: " + id + " " + name + " в эпике №" + epicId);
                    break;
            default:
                System.out.println("Неправильный тип задачи");
                break;
        }

    }

    public void printTasksByType(TaskType type) {


        switch (type) {
            case TASK:
                for (Task t : tasks.values()) {

                        System.out.println(t);

                }
                break;
            case EPIC:
                for (Epic t : epics.values()) {
                        System.out.println(t);
                }
                break;
            case SUBTASK:
                for (Epic t : epics.values()) {
                        System.out.println(t.getSubtasks());

                }
                break;
            default:
                System.out.println("Неправильный тип задачи");
                break;

        }
    }



    public void deleteTasksByType(TaskType type) {

        switch (type) {
            case TASK:
                tasks.clear();
                break;
            case EPIC:

                epics.clear();
                break;
            case SUBTASK:
                for (Epic t : epics.values()) {
                    t.getSubtasks().clear();
                }
                break;
            default:
                System.out.println("Неправильный тип задачи");
                break;
        }

    }

    public void printAllTasks() {
            for (Task t : tasks.values()) {
                    System.out.println(t);
                }
        for (Epic t : epics.values()) {
            System.out.println(t);
        }

        for (Epic t : epics.values()) {
            System.out.println(t.getSubtasks());

        }
        }


    public void deleteAllTasks() {
        tasks.clear();
        epics.clear();
        for (Epic t : epics.values()) {
            t.getSubtasks().clear();
        }
        System.out.println("Все задачи удалены");
    }

    public void findById(int id) {
        System.out.println(tasks.get(id));

    }

    public void updateTask(int id, String name, String description, TaskProgress status, int epicId) {
        TaskType type = tasks.get(id).getType();

        switch (type) {
            case TASK:
                tasks.put(id, new Task(id, name, description, type, status));
                System.out.println("Задача обновлена: " + id + " " + name);
                break;
            case EPIC:
                epics.put(id, new Epic(id, name, description, type, status));
                System.out.println("Эпик обновлен: " + id + " " + name);
                break;
            case SUBTASK:

                epics.get(epicId).getSubtasks().put(id, new Subtask(id, name, description, type, epicId, status));
                System.out.println("Подзадача обновлена: " + id + " " + name + " в эпике №" + epicId);
                break;
            default:
                System.out.println("Неправильный тип задачи");
                break;
        }
    }

    public void getEpicTasks(int id) {

                System.out.println(epics.get(id));
            }
        }
    }
}

