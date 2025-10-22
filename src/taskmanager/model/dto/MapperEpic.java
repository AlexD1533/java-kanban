package taskmanager.model.dto;

import taskmanager.model.Task;

public class MapperEpic {

    public static EpicDTO toDto(Task task) {
        return new EpicDTO(task.getId(), task.getName(), task.getDescription(),
                task.getStatus(), task.getType(), task.getStartTime(), task.getDuration(), task.getEndTime());
    }
}
