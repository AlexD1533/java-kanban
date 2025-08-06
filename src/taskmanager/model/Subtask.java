package taskmanager.model;

public class Subtask extends Task {
    private final int epicId;

    public Subtask(int id, String name, String description, TaskType type, int epicId, TaskProgress status) {
        super(id, name, description, type, status);
        this.epicId = epicId;
    }
    public int getEpicId() {
        return epicId;
    }
}
