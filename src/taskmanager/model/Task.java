package taskmanager.model;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;

public class Task {

    private final int id;
    private final String name;
    private final String description;
    private TaskProgress status;
    private final TaskType type;
    protected LocalDateTime startTime;
    private final Duration duration;
    private final String defaultTime = "2005-12-12T02:00";
    private final long defaultDuration = 0;


    public Task(int id, String name, String description, TaskType type, TaskProgress status, String startTime, long minutesForDuration) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.status = status;
        this.type = type;
        this.startTime = LocalDateTime.parse(startTime);
        this.duration = Duration.ofMinutes(minutesForDuration);
    }

    public Task(int id, String name, String description, TaskType type, TaskProgress status) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.status = status;
        this.type = type;
        this.startTime = LocalDateTime.parse(defaultTime);
        this.duration = Duration.ofMinutes(defaultDuration);
    }


    public long getDuration() {
        return duration.toMinutes();
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public LocalDateTime getEndTime() {
        return startTime.plus(duration);
    }


    public TaskProgress getStatus() {
        return status;
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

    public TaskType getType() {
        return type;
    }

    @Override
    public String toString() {
        return type + "{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", status=" + status +
                ", startTime=" + startTime +
                ", duration=" + duration.toMinutes() + " minutes" +
                ", endTime=" + this.getEndTime() +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return id == task.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
