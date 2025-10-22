package taskmanager.model.dto;

import taskmanager.model.TaskProgress;
import taskmanager.model.TaskType;

import java.time.Duration;
import java.time.LocalDateTime;

public class EpicDTO {

    private int id;
    private String name;
    private String description;
    private TaskProgress status;
    private TaskType type;
    protected LocalDateTime startTime;
    private Duration duration;
    private LocalDateTime endTime;

    public EpicDTO(int id, String name, String description, TaskProgress status,
                   TaskType type, LocalDateTime startTime, long duration, LocalDateTime endTime) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.status = status;
        this.type = type;
        this.startTime = startTime;
        this.duration = Duration.ofMinutes(duration);
        this.endTime = endTime;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public TaskProgress getStatus() {
        return status;
    }

    public TaskType getType() {
        return type;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public long getDuration() {
        return duration.toMinutes();
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }
}
