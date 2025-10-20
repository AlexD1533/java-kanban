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
import java.util.Optional;

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
            case UNKNOWN -> {
                break;
            }
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

        Optional<Integer> taskId = getTaskId(exchange);
        if (taskId.isPresent()) {
            int id = taskId.get();
            if (manager.getTask(id).isPresent()) {
                Task task = manager.getTask(id).get();
                String response = gson.toJson(task);
                sendText(exchange, response);
            } else {
                sendNotFound(exchange, "Task not found");
            }

        } else {
            sendNotFound(exchange, "ошибка ID");
        }
    }

    private void handleCreate(HttpExchange exchange) throws IOException {

        String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
        Task task = gson.fromJson(body, Task.class);
        try {
            manager.createTask(TaskType.TASK, task.getName(), task.getDescription(), 0, task.getStatus(),
                    task.getStartTime().toString(), task.getDuration(), task.getEndTime().toString());
            sendText(exchange, task.toString());

        } catch (ManagerSaveException e) {
            sendHasInteractions(exchange, e.getMessage());
        }
    }

    private void handleUpdate(HttpExchange exchange) throws IOException {
        Optional<Integer> taskId = getTaskId(exchange);
        if (taskId.isPresent()) {
            int id = taskId.get();
            System.out.println(id);
            String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);

            Task task = gson.fromJson(body, Task.class);

            try {
                System.out.println("TASK " + task);
                manager.updateTask(task.getType(), id, task.getName(), task.getDescription(), task.getStatus(),
                        0, task.getStartTime().toString(), task.getDuration(), task.getEndTime().toString());
                sendText(exchange, task.toString());
            } catch (NotFoundException | ManagerSaveException e) {
                sendNotFound(exchange, e.getMessage());
            }
        } else {
            sendNotFound(exchange, "ошибка ID");
        }
    }


    private void handleDeleteById(HttpExchange exchange) throws IOException {
        Optional<Integer> taskId = getTaskId(exchange);
        if (taskId.isPresent()) {
            int id = taskId.get();
            try {
                manager.deleteTasksById(TaskType.TASK, id);
                sendText(exchange, "Задача удалена");
            } catch (NotFoundException e) {
                sendNotFound(exchange, e.getMessage());
            }
        } else {
            sendNotFound(exchange, "ошибка id");
        }
    }
}

