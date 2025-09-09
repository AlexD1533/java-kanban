package taskmanager.model;
import java.util.Map;

public class Epic extends Task {
    private Map<Integer, Subtask> subtasks;

    public Epic(int id, String name, String description, TaskType type, TaskProgress status, Map<Integer, Subtask> subtasks) {
        super(id, name, description, type, status);
        this.subtasks = subtasks;
    }

    public Map<Integer, Subtask> getSubtasks() {
        return Map.copyOf(subtasks);
    }

}
