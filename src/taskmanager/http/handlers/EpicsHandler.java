package taskmanager.http.handlers;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import taskmanager.http.Endpoint;
import taskmanager.manager.TaskManager;
import taskmanager.manager.exceptions.ManagerSaveException;
import taskmanager.manager.exceptions.NotFoundException;
import taskmanager.model.Epic;
import taskmanager.model.TaskType;
import taskmanager.model.dto.EpicDTO;
import taskmanager.model.dto.MapperEpic;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class EpicsHandler extends BaseHttpHandler implements HttpHandler {
    private final String BASE_PATH_ENDPOINT = "epics";
    private final TaskManager manager;

    public EpicsHandler(TaskManager manager) {
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
            case UNKNOWN -> sendNotFound(exchange, "Path not found");

        }
    }

    private void handleGetAll(HttpExchange exchange) throws IOException {
        List<Epic> allEpics = new ArrayList<>(manager.getEpics().values());

        List<EpicDTO> allEpicsDTO = allEpics.stream()
                        .map(MapperEpic::toDto)
                                        .toList();

        System.out.println("handleGet: " + allEpics);
        if (allEpicsDTO.isEmpty()) {
            sendNotFound(exchange, "Список пуст");
            return;
        }
        String response = gson.toJson(allEpicsDTO);
        sendText(exchange, response);

    }

    private void handleGetId(HttpExchange exchange) throws IOException {
        try {
            int id = getTaskId(exchange);
            EpicDTO epic = MapperEpic.toDto(manager.getEpic(id));
            System.out.println(epic);
            String response = gson.toJson(epic);
            sendText(exchange, response);
        } catch (NumberFormatException | NotFoundException e) {
            sendNotFound(exchange, e.getMessage());
        }
    }

    private void handleCreate(HttpExchange exchange) throws IOException {

        String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
        EpicDTO epic = gson.fromJson(body, EpicDTO.class);
        try {
            manager.createTask(TaskType.EPIC, epic.getName(), epic.getDescription(), 0, epic.getStatus(),
                    epic.getStartTime().toString(), epic.getDuration(), epic.getEndTime().toString());
            sendText(exchange, "Эпик создан");
        } catch (ManagerSaveException e) {
            sendHasInteractions(exchange, e.getMessage());
        }
    }

    private void handleUpdate(HttpExchange exchange) throws IOException {
        try {
            int id = getTaskId(exchange);
            String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
            EpicDTO epic = gson.fromJson(body, EpicDTO.class);
            manager.updateTask(epic.getType(), id, epic.getName(), epic.getDescription(), epic.getStatus(),
                    0, epic.getStartTime().toString(), epic.getDuration(), epic.getEndTime().toString());
            sendText(exchange, "Эпик изменен");
        } catch (NumberFormatException | NotFoundException | ManagerSaveException e) {
            sendNotFound(exchange, e.getMessage());
        }

    }

    private void handleDeleteById(HttpExchange exchange) throws IOException {
        try {
            int id = getTaskId(exchange);
            manager.deleteTasksById(TaskType.EPIC, id);
            sendText(exchange, "Эпик удален");
        } catch (NumberFormatException | NotFoundException e) {
            sendNotFound(exchange, e.getMessage());
        }
    }
}

