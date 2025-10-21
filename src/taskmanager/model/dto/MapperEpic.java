package taskmanager.model.dto;

import taskmanager.model.Epic;

public class MapperEpic {

    public static EpicDTO toDto(Epic epic) {
        return new EpicDTO(epic.getId(), epic.getName(), epic.getDescription(),
                epic.getStatus(), epic.getType(), epic.getStartTime(), epic.getDuration(),epic.getEndTime());
    }
}
