public class Subtask extends Task {

    public int getEpicId() {
        return epicId;
    }

    private int epicId;



    public Subtask(int id, String name, String description, int epicId, TaskType type) {
        super(id, name, description, type);
        this.epicId = epicId;
    }

    public Subtask(int id, String name, String description, TaskType type, int epicId, TaskProgress status) {
        super(id, name, description, type, status);
        this.epicId = epicId;
    }
}
