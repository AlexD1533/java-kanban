package taskmanager.model;

public class Subtask extends Task {
    private final int epicId;

    public Subtask(int id, String name, String description, TaskType type, int epicId, TaskProgress status,  String startTime, long minutesForDuration) {
        super(id, name, description, type, status, startTime, minutesForDuration);
        this.epicId = epicId;
    }

    public int getEpicId() {
        return epicId;
    }
}
