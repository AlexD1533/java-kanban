package taskmanager.http.handlers;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import taskmanager.http.Endpoint;
import taskmanager.manager.TaskManager;
import taskmanager.manager.exceptions.ManagerSaveException;
import taskmanager.manager.exceptions.NotFoundException;
import taskmanager.model.Task;
import taskmanager.model.TaskType;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class TaskHandler extends BaseHttpHandler implements HttpHandler {

    private final String BASE_PATH_ENDPOINT = "tasks";
    private final TaskManager manager;

    public TaskHandler(TaskManager manager) {
        this.manager = manager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {

        String requestPath = exchange.getRequestURI().toString();
        String requestMethod = exchange.getRequestMethod();
        Endpoint endpoint = getEndpoint(BASE_PATH_ENDPOINT, requestPath, requestMethod);

        System.out.println("endpoint: " + endpoint);

        switch (endpoint) {

            case GET_ALL -> handleGetAll(exchange);
            case GET_BY_ID -> handleGetId(exchange);
            case POST_CREATE -> handleCreate(exchange);
            case POST_UPDATE -> handleUpdate(exchange);
            case DELETE -> handleDeleteById(exchange);
            case UNKNOWN -> sendNotFound(exchange, "Path not found");

        }
    }

    private void handleGetAll(HttpExchange exchange) throws IOException {

        List<Task> allTask = new ArrayList<>(manager.getTasks().values());
        System.out.println("handleGet: " + allTask);

        if (allTask.isEmpty()) {
            sendNotFound(exchange, "list is empty");
            return;
        }
        String response = gson.toJson(allTask);
        sendText(exchange, response);

    }

    private void handleGetId(HttpExchange exchange) throws IOException {
        try {
            int id = getTaskId(exchange);
            Task task = manager.getTask(id);
            String response = gson.toJson(task);
            sendText(exchange, response);
        } catch (NumberFormatException | NotFoundException e) {
            sendNotFound(exchange, e.getMessage());
        }
    }

    private void handleCreate(HttpExchange exchange) throws IOException {

        String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
        Task task = gson.fromJson(body, Task.class);
        try {
            manager.createTask(TaskType.TASK, task.getName(), task.getDescription(), 0, task.getStatus(),
                    task.getStartTime().toString(), task.getDuration(), task.getEndTime().toString());
            sendText(exchange, "Задача создана");
        } catch (ManagerSaveException e) {
            sendHasInteractions(exchange, e.getMessage());
        }
    }

    private void handleUpdate(HttpExchange exchange) throws IOException {
        try {
            int id = getTaskId(exchange);
            String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
            Task task = gson.fromJson(body, Task.class);
            manager.updateTask(task.getType(), id, task.getName(), task.getDescription(), task.getStatus(),
                    0, task.getStartTime().toString(), task.getDuration(), task.getEndTime().toString());
            sendText(exchange, task.toString());
        } catch (NumberFormatException | NotFoundException | ManagerSaveException e) {
            sendNotFound(exchange, e.getMessage());
        }

    }

    private void handleDeleteById(HttpExchange exchange) throws IOException {
        try {
            int id = getTaskId(exchange);
            manager.deleteTasksById(TaskType.TASK, id);
            sendText(exchange, "Задача удалена");
        } catch (NumberFormatException | NotFoundException e) {
            sendNotFound(exchange, e.getMessage());
        }
    }
}

