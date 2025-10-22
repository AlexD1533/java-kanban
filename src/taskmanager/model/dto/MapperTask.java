package taskmanager.model.dto;

import taskmanager.model.Task;

public class MapperTask {

    public static TaskDTO toDto(Task task) {
        return new TaskDTO(task.getId(), task.getName(), task.getDescription(),
                task.getStatus(), task.getType(), task.getStartTime(), task.getDuration(),task.getEndTime());
    }
}
