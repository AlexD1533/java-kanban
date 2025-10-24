package taskmanager.http.handlers;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import taskmanager.http.Endpoint;
import taskmanager.http.gson.GsonUtil;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

abstract class BaseHttpHandler {

    protected final Gson gson = GsonUtil.createGson();

    protected void startEndpoint(HttpExchange exchange, Endpoint endpoint) throws IOException {

        switch (endpoint) {

            case GET_ALL -> handleGetAll(exchange);
            case GET_BY_ID -> handleGetId(exchange);
            case POST_CREATE -> handleCreate(exchange);
            case POST_UPDATE -> handleUpdate(exchange);
            case DELETE -> handleDeleteById(exchange);
            case UNKNOWN -> writeResponse(exchange, "Path not found", 404);
        }
    }

    protected int getTaskId(HttpExchange exchange) {

        String[] paramPath = exchange.getRequestURI().toString().split("/");
        if (paramPath[2] == null || paramPath[2].isBlank()) {
            throw new NumberFormatException("Ошибка формата ID");
        } else {
            return Integer.parseInt(paramPath[2]);
        }
    }

    protected Endpoint getEndpoint(String basePathEndpoint, String requestPath, String requestMethod) {

        String[] pathParts = requestPath.split("/");
        if (requestMethod.equals("GET") && pathParts[1].equals(basePathEndpoint) && pathParts.length == 2) {
            return Endpoint.GET_ALL;
        } else if (requestMethod.equals("GET") && pathParts[1].equals(basePathEndpoint) && pathParts.length == 3) {
            return Endpoint.GET_BY_ID;
        } else if (requestMethod.equals("GET") && pathParts[1].equals(basePathEndpoint) && pathParts.length == 4 &&
                pathParts[3].equals("subtasks")) {
            return Endpoint.GET_SUBTASKS;
        } else if (requestMethod.equals("DELETE") && pathParts[1].equals(basePathEndpoint) && pathParts.length == 3) {
            return Endpoint.DELETE;
        } else if (requestMethod.equals("POST") && pathParts[1].equals(basePathEndpoint) && pathParts.length == 2) {
            return Endpoint.POST_CREATE;
        } else if (requestMethod.equals("POST") && pathParts[1].equals(basePathEndpoint) && pathParts.length == 3) {
            return Endpoint.POST_UPDATE;
        } else {
            return Endpoint.UNKNOWN;
        }
    }

    protected void writeResponse(HttpExchange h, String text, int statusCode) throws IOException {

        System.out.println("writeResponse " + text);
        byte[] resp = text.getBytes(StandardCharsets.UTF_8);
        h.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
        h.sendResponseHeaders(statusCode, resp.length);
        h.getResponseBody().write(resp);
        h.close();
    }

    protected void writeResponseNoContent(HttpExchange h) throws IOException {

        h.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
        h.sendResponseHeaders(204, -1);
        h.close();
    }

    protected abstract void handleGetAll(HttpExchange exchange) throws IOException;

    protected abstract void handleGetId(HttpExchange exchange) throws IOException;

    protected abstract void handleCreate(HttpExchange exchange) throws IOException;

    protected abstract void handleUpdate(HttpExchange exchange) throws IOException;

    protected abstract void handleDeleteById(HttpExchange exchange) throws IOException;
}