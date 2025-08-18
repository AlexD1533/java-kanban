package taskmanager.model;

import java.util.Objects;

public class Task {

    private final int id;
    private final String name;
    private final String description;
    private TaskProgress status;
    private final TaskType type;

    @Override
    public String toString() {
        return type + "{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", status=" + status +

                '}';
    }

    public Task(int id, String name, String description, TaskType type, TaskProgress status) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.status = status;
        this.type = type;
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

    public TaskProgress getStatus() {
        return status;
    }
    public void setStatus(TaskProgress status) {
        this.status = status;
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
}
