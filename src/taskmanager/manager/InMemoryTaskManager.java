package taskmanager.manager;

import taskmanager.manager.exceptions.ManagerSaveException;
import taskmanager.manager.exceptions.NotFoundException;
import taskmanager.model.*;
import taskmanager.util.Validation;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class InMemoryTaskManager implements TaskManager {
    private static int counter;
    private static final Map<Integer, Task> tasks = new HashMap<>();
    private static final Map<Integer, Epic> epics = new HashMap<>();
    private final HistoryManager historyManager = Managers.getDefaultHistory();

    @Override
    public Map<Integer, Task> getTasks() {
        return Map.copyOf(tasks);
    }
@Override
    public Map<Integer, Epic> getEpics() {
        return Map.copyOf(epics);
    }

    private final TreeSet<Task> priorityTasks = new TreeSet<>(
            Comparator.comparing(Task::getStartTime)
                    .thenComparing(Task::getId));

    @Override
    public boolean checkIntersections(Task t1, Task t2) {
        if (t1 == null || t2 == null) return false;
        if (t1.getStartTime().isBefore(t2.getEndTime()) && t1.getEndTime().isAfter(t2.getStartTime())) {
            return true;
        }
        return false;
    }

    @Override
    public boolean checkIntersectionsByList(Task t1) {

        return getPrioritizedTasks().stream()
                .filter(task -> task.getId() != t1.getId())
                .filter(task -> !(task instanceof Epic))
                .anyMatch(task -> checkIntersections(t1, task));
    }

    @Override
    public List<Task> getPrioritizedTasks() {

        priorityTasks.addAll(getAllTasks().values());
        return List.copyOf(priorityTasks);
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
    public void addTask(int id, Task task) {

        tasks.put(id, task);
        System.out.println("Задача создана: " + id + " ");

    }

    @Override
    public void addEpic(int id, Epic epic) {
        epics.put(id, epic);
        System.out.println("Эпик создан: " + id + " ");
    }

    @Override
    public void addSubtask(int id, Subtask subtask) {
            int epicId = subtask.getEpicId();
            Map<Integer, Subtask> current = new HashMap<>(epics.get(epicId).getSubtasks());
            current.put(id, subtask);

            addEpic(epicId, new Epic(epicId, epics.get(epicId).getName(),
                    epics.get(epicId).getDescription(), epics.get(epicId).getType(),
                    epics.get(epicId).getStatus(), current));
            updateEpicTaskStatus(epicId);
            System.out.println("Подзадача создана: " + id + " " + subtask.getName() + " в эпике №" + epicId);
    }

    @Override
    public Optional<Integer> getMaxId() {
        if (!getAllTasks().isEmpty()) {
            int maxId = getAllTasks().descendingKeySet().getFirst();
            return Optional.of(maxId);
        } else {
            return Optional.empty();
        }
    }

    @Override
    public void createTask(TaskType type, String name, String description, int epicId, TaskProgress status, String startTime, long minutesForDuration, String endTime) {
        if (!Validation.inputValidation(name, startTime, endTime)) {
            return;
        }
        if (getMaxId().isPresent()) {
            counter = getMaxId().get() + 1;
        } else {
            counter = 0;
        }

        int id = counter;

            switch (type) {
                case TaskType.TASK:

                    Task task = new Task(id, name, description, type, status, startTime, minutesForDuration);
                    if (!checkIntersectionsByList(task)) {
                        addTask(id, task);
                        break;
                    } else {
                        System.out.println("задачи пересекаются по времени");
                        throw new ManagerSaveException("Нельзя добавить задачу, задачи пересекаются по времени");
                    }

                case TaskType.EPIC:
                    TaskProgress defaultStatus = TaskProgress.NEW;
                    addEpic(id, new Epic(id, name, description, type, defaultStatus, new HashMap<>()));

                    break;
                case TaskType.SUBTASK:
                    if (!epics.containsKey(epicId)) {
                        break;
                    }
Subtask subtask = new Subtask(id, name, description, type, epicId, status, startTime, minutesForDuration);
                    if (!checkIntersectionsByList(subtask)) {
                        addSubtask(id, subtask);
                    } else {

            System.out.println("задачи пересекаются по времени");
            throw new ManagerSaveException("Нельзя добавить задачу, задачи пересекаются по времени");
        }
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
                if (!tasks.containsKey(id)) {
                    throw new NotFoundException("Задачи с id " + id + " не существует");
                } else {
                    tasks.remove(id);
                    historyManager.remove(id);
                    break;
                }
            case TaskType.EPIC:
                if (!epics.containsKey(id)) {
                    throw new NotFoundException("Эпика с id " + id + " не существует");
                }
                List<Integer> idEpicList = new ArrayList<>(epics.get(id).getSubtasks().keySet());

                epics.remove(id);

                historyManager.remove(id);
                for (Integer key : idEpicList) {
                    historyManager.remove(key);
                }
                break;
            case TaskType.SUBTASK:
                if (!getAllSubtasks().containsKey(id)) {
                    throw new NotFoundException("Подзадачи с id " + id + " не существует");

                }
                int epicId = getAllSubtasks().get(id).getEpicId();
                if (!epics.containsKey(epicId)) {
                    throw new NotFoundException("Эпика с id " + id + " не существует");
                }
                Map<Integer, Subtask> current = new HashMap<>(epics.get(epicId).getSubtasks());
                current.remove(id);
                addEpic(epicId, new Epic(epicId, epics.get(epicId).getName(),
                        epics.get(epicId).getDescription(), epics.get(epicId).getType(),
                        epics.get(epicId).getStatus(), current));

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
                    if (!tasks.containsKey(id)) {
                        throw new NotFoundException("Задачи с id " + id + " не существует");
                    }

                    System.out.println("Обновление задачи: " + id + " " + name);
                    Task task = new Task(id, name, description, type, status, startTime, minutesForDuration);

            if (!checkIntersectionsByList(task)) {
                addTask(id, task);
                break;

            } else {
                    System.out.println("задачи пересекаются по времени");
                    throw new ManagerSaveException("Нельзя добавить задачу, задачи пересекаются по времени");
                }

                case TaskType.EPIC:
                    if (!epics.containsKey(id)) {
                            throw new NotFoundException("Эпика с id " + id + " не существует");
                        }

                    System.out.println("Обновление эпика: " + id + " " + name);
                    addEpic(id, new Epic(id, name, description, type, status, epics.get(id).getSubtasks()));
                    break;
                case TaskType.SUBTASK:

                    if (!epics.containsKey(epicId) || !epics.get(epicId).getSubtasks().containsKey(id)) {
                            throw new NotFoundException("Задачи с id " + id + " не существует");
                    }

                    System.out.println("Обновление подзадачи: " + id + " " + name + " в эпике: " + epicId);
                    Subtask subtask = new Subtask(id, name, description, type, epicId, status, startTime, minutesForDuration);
                    if (!checkIntersectionsByList(subtask)) {
                        addSubtask(id, subtask);
                    } else {

                        System.out.println("задачи пересекаются по времени");
                        throw new ManagerSaveException("Нельзя добавить задачу, задачи пересекаются по времени");
                    }
                    break;

                default:
                    System.out.println("Неправильный тип задачи");
                    break;
            }
    }

    @Override
    public void printEpicSubtasks(int id) {

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
    public Stream<Subtask> getEpicSubtasks(int epicId) {
        return epics.get(epicId).getSubtasks().values().stream();
    }

    @Override
    public Map<Integer, Subtask> getAllSubtasks() {
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

        if (!tasks.containsKey(id)) {
          throw new NotFoundException("Задачи с id " + id + " не существует");
        }
        historyManager.addTask(tasks.get(id));
        return tasks.get(id);
    }

    @Override
    public Subtask getSubtask(int id) {

        if (!getAllSubtasks().containsKey(id)) {
            throw new NotFoundException("Подзадачи с id " + id + " не существует");
        }
        historyManager.addTask(getAllSubtasks().get(id));
        return getAllSubtasks().get(id);
    }

    @Override
    public Epic getEpic(int id) {

        if (!epics.containsKey(id)) {
            throw new NotFoundException("Эпика с id " + id + " не существует");
        }
        historyManager.addTask(epics.get(id));
        return epics.get(id);
    }


}


