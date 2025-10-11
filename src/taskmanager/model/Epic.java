package taskmanager.model;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;

public class Epic extends Task {
    private LocalDateTime endTime;
    private Map<Integer, Subtask> subtasks;
    private final String defaultTime = "2005-12-12T02:00";

    public Epic(int id, String name, String description, TaskType type, TaskProgress status, Map<Integer, Subtask> subtasks) {
        super(id, name, description, type, status);
        this.subtasks = subtasks;

        this.startTime = getStartTime();
        this.endTime = getEndTime();
    }

    @Override
    public long getDuration() {
        return Duration.between(startTime, endTime).toMinutes();
    }
    @Override
    public LocalDateTime getStartTime() {
        return updateEpicStartTime(getSubtasks()).orElse(LocalDateTime.parse(defaultTime));
    }

    @Override
    public LocalDateTime getEndTime() {
        return updateEpicEndTime(getSubtasks()).orElse(LocalDateTime.parse(defaultTime));
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



    public Optional<LocalDateTime> updateEpicStartTime(Map<Integer, Subtask> map) {
        return map.values().stream()
                .map(Task::getStartTime)
                .min(LocalDateTime::compareTo);

    }

    public Optional<LocalDateTime> updateEpicEndTime(Map<Integer, Subtask> map) {
        return map.values().stream()
                .map(Task::getEndTime)
                .max(LocalDateTime::compareTo);
    }


}
