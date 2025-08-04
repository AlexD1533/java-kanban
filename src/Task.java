public class Task {


    private int id;
    private String name;
    private String description;
    private TaskProgress status = TaskProgress.NEW;

    private TaskType type;

    @Override
    public String toString() {
        return type + "{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", status=" + status +
                "," +
                '}';
    }

    public Task(int id, String name, String description, TaskType type) {
        this.id = id;
        this.name = name;
        this.description = description;
this.type = type;
    }

    public Task(int id, String name, String description, TaskType type, TaskProgress status) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.status = status;
        this.type = type;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public TaskProgress getStatus() {
        return status;
    }

    public void setStatus(TaskProgress status) {
        this.status = status;
    }
    public TaskType getType() {
        return type;
    }

    public void setType(TaskType type) {
        this.type = type;
    }
}
