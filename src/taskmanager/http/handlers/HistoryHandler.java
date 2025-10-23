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

    private final String basePathEndpoint = "history";
    private final TaskManager manager;

    public HistoryHandler(TaskManager manager) {

        this.manager = manager;
    }

    @Override
    protected void startEndpoint(HttpExchange exchange, Endpoint endpoint) throws IOException {

        switch (endpoint) {

            case GET_ALL -> handleGetHistory(exchange);
            case UNKNOWN -> writeResponse(exchange, "Path not found", 404);
        }
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {

        try {
            String requestPath = exchange.getRequestURI().toString();
            String requestMethod = exchange.getRequestMethod();
            Endpoint endpoint = getEndpoint(basePathEndpoint, requestPath, requestMethod);

            System.out.println("endpoint: " + endpoint);
            startEndpoint(exchange, endpoint);
        } catch (Exception e) {
            e.printStackTrace();
            writeResponse(exchange, "Внутрення ошибка сервера", 500);
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
        } catch (Exception e) {
            e.printStackTrace();
            writeResponse(exchange, "Внутрення ошибка сервера", 500);
        }
    }

    @Override
    protected void handleGetAll(HttpExchange exchange) throws IOException {

    }

    @Override
    protected void handleGetId(HttpExchange exchange) throws IOException {

    }

    @Override
    protected void handleCreate(HttpExchange exchange) throws IOException {

    }

    @Override
    protected void handleUpdate(HttpExchange exchange) throws IOException {

    }

    @Override
    protected void handleDeleteById(HttpExchange exchange) throws IOException {

    }


}