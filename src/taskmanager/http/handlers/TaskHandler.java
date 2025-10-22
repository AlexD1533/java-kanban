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
            case UNKNOWN -> writeResponse(exchange, "Path not found", 404);
        }
    }

    private void handleGetAll(HttpExchange exchange) throws IOException {
        List<Task> allTask = new ArrayList<>(manager.getTasks().values());
        System.out.println("handleGet: " + allTask);
        if (allTask.isEmpty()) {
            writeResponse(exchange, "Список пуст", 404);
            return;
        }
        String response = gson.toJson(allTask);
        writeResponse(exchange, response, 200);
    }

    private void handleGetId(HttpExchange exchange) throws IOException {
        try {
            int id = getTaskId(exchange);
            Task task = manager.getTask(id);
            String response = gson.toJson(task);
            writeResponse(exchange, response, 200);
        } catch (NumberFormatException | NotFoundException e) {
            writeResponse(exchange, e.getMessage(), 404);
        }
    }

    private void handleCreate(HttpExchange exchange) throws IOException {

        String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
        Task task = gson.fromJson(body, Task.class);
        try {
            manager.createTask(TaskType.TASK, task.getName(), task.getDescription(), 0, task.getStatus(),
                    task.getStartTime().toString(), task.getDuration(), task.getEndTime().toString());
            writeResponse(exchange, "Задача создана", 201);
        } catch (ManagerSaveException e) {
            writeResponse(exchange, e.getMessage(), 406);
        }
    }

    private void handleUpdate(HttpExchange exchange) throws IOException {
        try {
            int id = getTaskId(exchange);
            String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
            Task task = gson.fromJson(body, Task.class);
            manager.updateTask(TaskType.TASK, id, task.getName(), task.getDescription(), task.getStatus(),
                    0, task.getStartTime().toString(), task.getDuration(), task.getEndTime().toString());
            writeResponse(exchange, "Задача изменена", 201);
        } catch (NumberFormatException | NotFoundException e) {
            writeResponse(exchange, e.getMessage(), 404);
        } catch (ManagerSaveException e) {
            writeResponse(exchange, e.getMessage(), 406);
        }
    }

    private void handleDeleteById(HttpExchange exchange) throws IOException {
        try {
            int id = getTaskId(exchange);
            manager.deleteTasksById(TaskType.TASK, id);
            writeResponse(exchange, "Задача удалена", 201);
        } catch (NumberFormatException | NotFoundException e) {
            writeResponse(exchange, e.getMessage(), 404);
        }
    }
}

