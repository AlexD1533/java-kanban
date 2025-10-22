package taskmanager.http.handlers;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import taskmanager.http.Endpoint;
import taskmanager.manager.TaskManager;
import taskmanager.manager.exceptions.NotFoundException;
import taskmanager.model.dto.MapperEpic;
import taskmanager.model.dto.EpicDTO;

import java.io.IOException;
import java.util.List;

public class HistoryHandler extends BaseHttpHandler implements HttpHandler {

    private final String BASE_PATH_ENDPOINT = "history";
    private final TaskManager manager;

    public HistoryHandler(TaskManager manager) {
        this.manager = manager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {

        String requestPath = exchange.getRequestURI().toString();
        String requestMethod = exchange.getRequestMethod();
        Endpoint endpoint = getEndpoint(BASE_PATH_ENDPOINT, requestPath, requestMethod);

        System.out.println("endpoint: " + endpoint);

        switch (endpoint) {

            case GET_ALL -> handleGetHistory(exchange);
            case UNKNOWN -> writeResponse(exchange, "Path not found", 404);
        }
    }

    private void handleGetHistory(HttpExchange exchange) throws IOException {
        try {
            List<EpicDTO> history = manager.getHistory().stream()
                    .map(MapperEpic::toDto)
                    .toList();
            String response = gson.toJson(history);
            writeResponse(exchange, response, 200);
        } catch (NotFoundException e) {
            writeResponse(exchange, e.getMessage(), 404);
        }
    }
}