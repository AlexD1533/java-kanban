package taskmanager.manager;

import taskmanager.model.Epic;
import taskmanager.model.Task;
import taskmanager.model.Subtask;
import taskmanager.model.TaskProgress;
import taskmanager.model.TaskType;
import taskmanager.util.Validation;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class InMemoryTaskManager implements TaskManager {
    private static int counter = 0;
    private static final Map<Integer, Task> tasks = new HashMap<>();
    private static final Map<Integer, Epic> epics = new HashMap<>();
    private final HistoryManager historyManager = Managers.getDefaultHistory();

    public Map<Integer, Task> getTasks() {
        return Map.copyOf(tasks);
    }

    public static Map<Integer, Epic> getEpics() {
        return Map.copyOf(epics);
    }


    @Override
    public Optional<Boolean> checkIntersections(Task t1) {
        return Optional.of(getPrioritizedTasks().stream()
                .filter(task -> task.getId() != t1.getId())
                .filter(task -> !(task instanceof Epic))
                .anyMatch(task -> t1.getStartTime().isBefore(task.getEndTime()) && t1.getEndTime().isAfter(task.getStartTime())));
    }

    @Override
    public TreeSet<Task> getPrioritizedTasks() {
        TreeSet<Task> result = new TreeSet<>(
                Comparator.comparing(Task::getStartTime)
                        .thenComparing(Task::getId));
        result.addAll(getAllTasks().values());
        return result;
    }

    @Override
    public TreeMap<Integer, Task> getAllTasks() {
        return allTasksStream()
                .collect(Collectors.toMap(
                        Task::getId,
                        task -> task,
                        (t1, t2) -> t1,
                        TreeMap::new
                ));
    }

    @Override
    public Stream<Task> allTasksStream() {
        return Stream.of(
                tasks.values().stream(),
                epics.values().stream(),
                epics.values().stream().flatMap(e -> e.getSubtasks().values().stream())
        ).flatMap(s -> s);
    }


    @Override
    public Optional<LocalDateTime> updateEpicStartTime(Map<Integer, Subtask> map) {
        return map.values().stream()
                .map(Task::getStartTime)
                .min(LocalDateTime::compareTo);

    }

    @Override
    public Optional<LocalDateTime> updateEpicEndTime(Map<Integer, Subtask> map) {
        return map.values().stream()
                .map(Task::getEndTime)
                .max(LocalDateTime::compareTo);
    }

    @Override
    public void addTask(int id, Task task) {
        boolean isIntersection = checkIntersections(task).orElseThrow(() ->
                new RuntimeException("Ошибка сравнения пересечений"));
        if (!isIntersection) {
            tasks.put(id, task);
            System.out.println("Задача создана: " + id + " ");
        } else {
            System.out.println("Нельзя добавить задачу, задачи пересекаются по времени");
        }
    }

    @Override
    public void addEpic(int id, Epic epic) {
        epics.put(id, epic);
        System.out.println("Эпик создан: " + id + " ");
    }

    @Override
    public void addSubtask(int id, Subtask subtask) {
        boolean isIntersection = checkIntersections(subtask).orElseThrow(() ->
                new RuntimeException("Ошибка сравнения пересечений"));
        if (!isIntersection) {

            int epicId = subtask.getEpicId();
            Map<Integer, Subtask> current = new HashMap<>(epics.get(epicId).getSubtasks());
            current.put(id, subtask);
            LocalDateTime newStartTime = updateEpicStartTime(current).orElse(epics.get(epicId).getStartTime());
            LocalDateTime newEndTime = updateEpicEndTime(current).orElse(epics.get(epicId).getEndTime());
            Duration allTaskDuration = Duration.between(newStartTime, newEndTime);

            addEpic(epicId, new Epic(epicId, epics.get(epicId).getName(),
                    epics.get(epicId).getDescription(), epics.get(epicId).getType(),
                    epics.get(epicId).getStatus(), current, newStartTime.toString(),
                    allTaskDuration.toMinutes(), newEndTime.toString()));
            updateEpicTaskStatus(epicId);

            System.out.println("Подзадача создана: " + id + " " + subtask.getName() + " в эпике №" + epicId);
        } else {
            System.out.println("Нельзя добавить задачу, задачи пересекаются по времени");
        }
    }

    @Override
    public void createTask(TaskType type, String name, String description, int epicId, TaskProgress status, String startTime, long minutesForDuration, String endTime) {
        if (!Validation.inputValidation(name, startTime, endTime)) {
            return;
        }
        int id = counter++;
        switch (type) {
            case TaskType.TASK:
                addTask(id, new Task(id, name, description, type, status, startTime, minutesForDuration));

                break;
            case TaskType.EPIC:
                TaskProgress defaultStatus = TaskProgress.NEW;
                addEpic(id, new Epic(id, name, description, type, defaultStatus, new HashMap<>(), startTime, minutesForDuration, endTime));

                break;
            case TaskType.SUBTASK:
                if (!Validation.epicValidation(epicId, epics)) {
                    break;
                }

                addSubtask(id, new Subtask(id, name, description, type, epicId, status, startTime, minutesForDuration));


                break;
            default:
                System.out.println("Неправильный тип задачи");
                break;
        }
    }

    @Override
    public void deleteTasksById(TaskType type, int id) {

        switch (type) {
            case TaskType.TASK:
                if (!Validation.taskValidation(id, tasks)) {
                    break;
                }
                tasks.remove(id);
                historyManager.remove(id);
                break;
            case TaskType.EPIC:
                if (!Validation.epicValidation(id, epics)) {
                    break;
                }
                List<Integer> idEpicList = new ArrayList<>(epics.get(id).getSubtasks().keySet());

                epics.remove(id);

                historyManager.remove(id);
                for (Integer key : idEpicList) {
                    historyManager.remove(key);
                }
                break;
            case TaskType.SUBTASK:
                if (!Validation.subTaskValidation(id, epics)) {
                    break;
                }
                int epicId = getAllSubtasks(epics).get(id).getEpicId();
                if (!Validation.subTaskValidationByEpic(epicId, id, epics)) {
                    break;
                }
                Map<Integer, Subtask> current = new HashMap<>(epics.get(epicId).getSubtasks());
                current.remove(id);
                addEpic(epicId, new Epic(epicId, epics.get(epicId).getName(),
                        epics.get(epicId).getDescription(), epics.get(epicId).getType(),
                        epics.get(epicId).getStatus(), current, epics.get(epicId).getStartTime().toString(),
                        epics.get(epicId).getDuration(), epics.get(epicId).getEndTime().toString()));

                historyManager.remove(id);
                updateEpicTaskStatus(epicId);
                break;
            default:
                System.out.println("Неправильный тип задачи");
                break;
        }
    }

    @Override
    public void printAllTasks() {
        allTasksStream().forEach(System.out::println);
    }

    @Override
    public boolean deleteAllTasks() {
        tasks.clear();
        epics.clear();
        System.out.println("Все задачи удалены");
        counter = 0;
        return tasks.isEmpty() && epics.isEmpty();
    }

    @Override
    public void updateTask(TaskType type, int id, String name, String description, TaskProgress status, int epicId, String startTime, long minutesForDuration, String endTime) {
        if (!Validation.inputValidation(name, startTime, endTime)) {
            return;
        }
        switch (type) {
            case TaskType.TASK:
                if (!Validation.taskValidation(id, tasks)) {
                    break;
                }
                System.out.println("Обновление задачи: " + id + " " + name);
                addTask(id, new Task(id, name, description, type, status, startTime, minutesForDuration));

                break;
            case TaskType.EPIC:
                if (!Validation.epicValidation(id, epics)) {
                    break;
                }
                System.out.println("Обновление эпика: " + id + " " + name);
                addEpic(id, new Epic(id, name, description, type, status, epics.get(id).getSubtasks(), startTime, minutesForDuration, endTime));
                break;
            case TaskType.SUBTASK:
                if (!Validation.epicValidation(epicId, epics)) {
                    break;
                }
                if (!Validation.subTaskValidationByEpic(epicId, id, epics)) {
                    break;
                }

                addSubtask(id, new Subtask(id, name, description, type, epicId, status, startTime, minutesForDuration));
                System.out.println("Обновление подзадачи: " + id + " " + name + " в эпике: " + epicId);
                break;
            default:
                System.out.println("Неправильный тип задачи");
                break;
        }
    }

    @Override
    public void getEpicTasks(int id) {

        if (!Validation.epicValidation(id, epics)) {
            return;
        }
        System.out.println("Информация о эпике:");
        System.out.println(epics.get(id));
        System.out.println("Подзадачи: ");

        if (!Validation.subTasksEmptyValidationByEpic(id, epics)) {
            return;
        }
        historyManager.addTask(epics.get(id));
        epics.get(id).getSubtasks().values().forEach(System.out::println);
    }


    @Override
    public Map<Integer, Subtask> getAllSubtasks(Map<Integer, Epic> epics) {
        return allTasksStream()
                .filter(t -> t instanceof Subtask)
                .map(t -> (Subtask) t)
                .collect(Collectors.toMap(
                        Subtask::getId,
                        task -> task,
                        (t1, t2) -> t1,
                        HashMap::new
                ));

    }

    @Override
    public void updateEpicTaskStatus(int epicId) {
        if (!Validation.epicValidation(epicId, epics)) {
            return;
        }
        int newCount = 0;
        int doneCount = 0;
        for (Subtask task : epics.get(epicId).getSubtasks().values()) {
            if (task.getStatus() == TaskProgress.NEW) {
                newCount++;
            } else if (task.getStatus() == TaskProgress.DONE) {
                doneCount++;
            }
        }
        if (newCount == epics.get(epicId).getSubtasks().values().size() ||
                epics.get(epicId).getSubtasks().values().isEmpty()) {

            updateTask(epics.get(epicId).getType(), epicId, epics.get(epicId).getName(),
                    epics.get(epicId).getDescription(), TaskProgress.NEW, 0, epics.get(epicId).getStartTime().toString(),
                    epics.get(epicId).getDuration(), epics.get(epicId).getEndTime().toString());

        } else if (doneCount == epics.get(epicId).getSubtasks().size()) {
            updateTask(epics.get(epicId).getType(), epicId, epics.get(epicId).getName(),
                    epics.get(epicId).getDescription(), TaskProgress.DONE, 0, epics.get(epicId).getStartTime().toString(),
                    epics.get(epicId).getDuration(), epics.get(epicId).getEndTime().toString());
        } else {
            updateTask(epics.get(epicId).getType(), epicId, epics.get(epicId).getName(),
                    epics.get(epicId).getDescription(), TaskProgress.IN_PROGRESS, 0, epics.get(epicId).getStartTime().toString(),
                    epics.get(epicId).getDuration(), epics.get(epicId).getEndTime().toString());
        }
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }


    @Override
    public Task getTask(int id) {
        if (!Validation.taskValidation(id, tasks)) {
            return null;
        }

        historyManager.addTask(tasks.get(id));
        return tasks.get(id);
    }

    @Override
    public Subtask getSubtask(int id) {

        if (!Validation.subTaskValidation(id, epics)) {
            return null;
        }
        int epicId = getAllSubtasks(epics).get(id).getEpicId();

        if (!Validation.subTaskValidationByEpic(epicId, id, epics)) {
            return null;
        }
        historyManager.addTask(epics.get(epicId).getSubtasks().get(id));
        return epics.get(epicId).getSubtasks().get(id);
    }

    @Override
    public Epic getEpic(int id) {

        if (!Validation.epicValidation(id, epics)) {
            return null;
        }
        historyManager.addTask(epics.get(id));
        return epics.get(id);
    }


}


