public class Task {
    public int getId() {
        return id;
    }

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


    public TaskProgress getStatus() {
        return status;
    }
    public void setStatus(TaskProgress status) {
        this.status = status;
    }
}
