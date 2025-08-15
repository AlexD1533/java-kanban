package taskmanager.manager;

import taskmanager.model.Task;

import java.util.ArrayList;
import java.util.List;

public class InMemoryHistoryManager <T extends Task> implements HistoryManager<T>{

  private final List<T> history = new ArrayList<>();


    @Override
    public void addTask(T task) {
history.add(task);
    }


    @Override
    public List<T> getHistory() {
return history;
    }
}
