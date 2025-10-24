package taskmanager.http;

import com.sun.net.httpserver.HttpServer;
import taskmanager.http.handlers.*;
import taskmanager.manager.Managers;
import taskmanager.manager.TaskManager;

import java.io.IOException;
import java.net.InetSocketAddress;

public class HttpTaskServer {
    private static final int PORT = 8080;
    private final TaskManager manager;
    private final HttpServer httpServer;

    public HttpTaskServer(TaskManager manager) throws IOException {
        this.manager = manager;
        this.httpServer = HttpServer.create(new InetSocketAddress(PORT), 0);
    }

    public void start() {

        httpServer.createContext("/tasks", new TaskHandler(manager));
        httpServer.createContext("/subtasks", new SubtaskHandler(manager));
        httpServer.createContext("/epics", new EpicsHandler(manager));
        httpServer.createContext("/history", new HistoryHandler(manager));
        httpServer.createContext("/prioritized", new PrioritizedHandler(manager));
        httpServer.start();
    }

    public void stop() {

        httpServer.stop(1);
    }

    public static void main(String[] args) throws IOException {
        HttpTaskServer httpServer = new HttpTaskServer(Managers.getFileBackedTaskManager());
        httpServer.start();
    }
}
