package taskmanager.http.handlers;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import taskmanager.http.Endpoint;
import taskmanager.manager.TaskManager;
import taskmanager.manager.exceptions.ManagerSaveException;
import taskmanager.manager.exceptions.NotFoundException;
import taskmanager.model.Subtask;
import taskmanager.model.Task;
import taskmanager.model.TaskType;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class SubtaskHandler extends BaseHttpHandler implements HttpHandler {
    private final String BASE_PATH_ENDPOINT = "subtasks";
    private final TaskManager manager;

    public SubtaskHandler(TaskManager manager) {
        this.manager = manager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {

        String requestPath = exchange.getRequestURI().toString();
        String requestMethod = exchange.getRequestMethod();
        Endpoint endpoint = getEndpoint(BASE_PATH_ENDPOINT, requestPath, requestMethod);

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
        List<Task> allTask = new ArrayList<>(manager.getAllSubtasks().values());
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
            Subtask subtask = manager.getSubtask(id);
            String response = gson.toJson(subtask);
            writeResponse(exchange, response, 200);
        } catch (NumberFormatException | NotFoundException e) {
            writeResponse(exchange, e.getMessage(), 404);
        }
    }

    private void handleCreate(HttpExchange exchange) throws IOException {

        String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
        Subtask subtask = gson.fromJson(body, Subtask.class);
        try {
            manager.createTask(TaskType.SUBTASK, subtask.getName(), subtask.getDescription(), subtask.getEpicId(), subtask.getStatus(),
                    subtask.getStartTime().toString(), subtask.getDuration(), subtask.getEndTime().toString());
            writeResponse(exchange, "Подзадача создана", 201);
        } catch (ManagerSaveException e) {
            writeResponse(exchange, e.getMessage(), 406);
        }
    }

    private void handleUpdate(HttpExchange exchange) throws IOException {
        try {
            int id = getTaskId(exchange);
            String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
            Subtask subtask = gson.fromJson(body, Subtask.class);
            manager.updateTask(TaskType.SUBTASK, id, subtask.getName(), subtask.getDescription(), subtask.getStatus(),
                    subtask.getEpicId(), subtask.getStartTime().toString(), subtask.getDuration(), subtask.getEndTime().toString());
            writeResponse(exchange, "Подзадача изменена", 201);
        } catch (NumberFormatException | NotFoundException e) {
            writeResponse(exchange, e.getMessage(), 404);
        } catch (ManagerSaveException e) {
            writeResponse(exchange, e.getMessage(), 406);
        }

    }

    private void handleDeleteById(HttpExchange exchange) throws IOException {
        try {
            int id = getTaskId(exchange);
            manager.deleteTasksById(TaskType.SUBTASK, id);
            writeResponse(exchange, "Подзадача удалена", 201);
        } catch (NumberFormatException | NotFoundException e) {
            writeResponse(exchange, e.getMessage(), 404);
        }
    }



}



