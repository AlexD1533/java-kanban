package taskmanager.manager;

import taskmanager.model.Task;

import java.util.ArrayList;
import java.util.List;

public class InMemoryHistoryManager <T extends Task> implements HistoryManager<T>{

  private final List<Task> history = new ArrayList<>(10);


    @Override
    public void addTask(T task) {
        if (history.size() == 10) {
            history.remove(0);
        }
history.add(task);
    }


    @Override
    public List<Task> getHistory() {
return history;
    }
}
