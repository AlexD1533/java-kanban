package taskmanager.http.handlers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import taskmanager.http.Endpoint;
import taskmanager.http.gson.ListSubtaskToken;
import taskmanager.manager.TaskManager;
import taskmanager.manager.exceptions.ManagerSaveException;
import taskmanager.manager.exceptions.NotFoundException;
import taskmanager.model.Epic;
import taskmanager.model.Subtask;
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
            case GET_SUBTASKS -> handleGetSubtasks(exchange);
            case POST_CREATE -> handleCreate(exchange);
            case POST_UPDATE -> handleUpdate(exchange);
            case DELETE -> handleDeleteById(exchange);
            case UNKNOWN -> writeResponse(exchange, "Path not found", 404);
        }
    }

    private void handleGetSubtasks(HttpExchange exchange) throws IOException {
        try {
            int id = getTaskId(exchange);
            List<Subtask> subtasks = manager.getEpicSubtasks(id);
            String response = gson.toJson(subtasks, new ListSubtaskToken().getType());
            writeResponse(exchange, response, 200);

        } catch (NumberFormatException | NotFoundException e) {
            writeResponse(exchange, e.getMessage(), 404);
        }
    }

    private void handleGetAll(HttpExchange exchange) throws IOException {
        List<Epic> allEpics = new ArrayList<>(manager.getEpics().values());

        List<EpicDTO> allEpicsDTO = allEpics.stream()
                .map(MapperEpic::toDto)
                .toList();

        System.out.println("handleGet: " + allEpics);
        if (allEpicsDTO.isEmpty()) {
            writeResponse(exchange, "Список пуст", 404);
            return;
        }
        String response = gson.toJson(allEpicsDTO);
        writeResponse(exchange, response, 200);
    }

    private void handleGetId(HttpExchange exchange) throws IOException {
        try {
            int id = getTaskId(exchange);
            EpicDTO epic = MapperEpic.toDto(manager.getEpic(id));
            System.out.println(epic);
            String response = gson.toJson(epic);
            writeResponse(exchange, response, 200);
        } catch (NumberFormatException | NotFoundException e) {
            writeResponse(exchange, e.getMessage(), 404);
        }
    }

    private void handleCreate(HttpExchange exchange) throws IOException {

        String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
        EpicDTO epic = gson.fromJson(body, EpicDTO.class);
        try {
            manager.createTask(TaskType.EPIC, epic.getName(), epic.getDescription(), 0, epic.getStatus(),
                    epic.getStartTime().toString(), epic.getDuration(), epic.getEndTime().toString());
            writeResponse(exchange, "Эпик создан", 201);
        } catch (ManagerSaveException e) {
            writeResponse(exchange, e.getMessage(), 406);
        }
    }

    private void handleUpdate(HttpExchange exchange) throws IOException {
        try {
            int id = getTaskId(exchange);
            String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
            EpicDTO epic = gson.fromJson(body, EpicDTO.class);
            manager.updateTask(TaskType.EPIC, id, epic.getName(), epic.getDescription(), epic.getStatus(),
                    0, epic.getStartTime().toString(), epic.getDuration(), epic.getEndTime().toString());
            writeResponse(exchange, "Эпик изменен", 201);
        } catch (NumberFormatException | NotFoundException e) {
            writeResponse(exchange, e.getMessage(), 404);
        } catch (ManagerSaveException e) {
            writeResponse(exchange, e.getMessage(), 406);
        }
    }

    private void handleDeleteById(HttpExchange exchange) throws IOException {
        try {
            int id = getTaskId(exchange);
            manager.deleteTasksById(TaskType.EPIC, id);
            writeResponse(exchange, "Эпик удален", 201);
        } catch (NumberFormatException | NotFoundException e) {
            writeResponse(exchange, e.getMessage(), 404);
        }
    }
}

