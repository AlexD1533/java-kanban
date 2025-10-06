package taskmanager.model;

import java.time.LocalDateTime;
import java.util.Map;

public class Epic extends Task {
    private LocalDateTime endTime;
    private Map<Integer, Subtask> subtasks;

    public Epic(int id, String name, String description, TaskType type, TaskProgress status, Map<Integer, Subtask> subtasks,  String startTime, long minutesForDuration, String endTime) {
        super(id, name, description, type, status,startTime, minutesForDuration);
        this.subtasks = subtasks;
        this.endTime = LocalDateTime.parse(endTime);
    }

    @Override
    public String toString() {
        return this.getType() + "{" +
                "id=" + this.getId() +
                ", name='" + this.getName() + '\'' +
                ", description='" + this.getDescription() + '\'' +
                ", status=" + this.getStatus() +
                ", startTime=" + this.getStartTime() +
                ", duration=" + this.getDuration() + " minutes" +
                ", endTime=" + this.endTime +
                '}';
    }

    public Map<Integer, Subtask> getSubtasks() {
        return Map.copyOf(subtasks);
    }

    @Override
    public LocalDateTime getEndTime() {
        return endTime;
    }
}
