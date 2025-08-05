import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Epic extends Task {

    private Map<Integer, Subtask> subtasks = new HashMap<>();

    public Epic(int id, String name, String description, TaskType type, TaskProgress status) {
        super(id, name, description, type, status);

    }
    public Map<Integer, Subtask> getSubtasks() {
        return subtasks;
    }

}
