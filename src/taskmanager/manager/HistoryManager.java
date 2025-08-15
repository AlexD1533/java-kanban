package taskmanager.manager;

import taskmanager.model.Task;

import java.util.List;

public interface HistoryManager <T extends Task> {

    void addTask(T task);


    List<T> getHistory();

}
