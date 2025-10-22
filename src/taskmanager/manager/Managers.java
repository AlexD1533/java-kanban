package taskmanager.manager;

import java.io.File;

public class Managers {

    public static TaskManager getDefault() {
        return new InMemoryTaskManager();
    }
    public static TaskManager getFileBackedTaskManager() {

        return FileBackedTaskManager.loadFromFile(new File("SavedDataCSV.txt"));
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }


}
