package taskmanager.model;

import java.util.HashMap;
import java.util.Map;

public class Epic extends Task {
    private Map<Integer, Subtask> subtasks = new HashMap<>();

    public Epic(int id, String name, String description, TaskType type, TaskProgress status) {
        super(id, name, description, type, status);
    }

    public Map<Integer, Subtask> getSubtasks() {
        return subtasks;
    }
    public void setSubtasks (Map<Integer, Subtask> subtasks) {
        this.subtasks = subtasks;
    }
}
