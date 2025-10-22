package taskmanager.http;

import com.sun.net.httpserver.HttpServer;
import taskmanager.http.handlers.*;
import taskmanager.manager.Managers;
import taskmanager.manager.TaskManager;

import java.io.IOException;
import java.net.InetSocketAddress;

public class HttpTaskServer {
    private static final int PORT = 8080;

    public static void main(String[] args) throws IOException {

        TaskManager manager = Managers.getFileBackedTaskManager();
        HttpServer httpServer = HttpServer.create(new InetSocketAddress(PORT), 0);
httpServer.createContext("/tasks", new TaskHandler(manager));
httpServer.createContext("/subtasks", new SubtaskHandler(manager));
httpServer.createContext("/epics", new EpicsHandler(manager));
httpServer.createContext("/history", new HistoryHandler(manager));
httpServer.createContext("/prioritized", new PrioritizedHandler(manager));

        httpServer.start();
    }
}
