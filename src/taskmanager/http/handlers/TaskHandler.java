package taskmanager.http.handlers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import taskmanager.http.Endpoint;
import taskmanager.manager.TaskManager;
import taskmanager.model.Task;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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
            case UNKNOWN -> {
                return;
            }
        }
    }


    private static class LocalDateTimeAdapter extends TypeAdapter<LocalDateTime> {
        DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

        @Override
        public void write(JsonWriter jsonWriter, LocalDateTime localDateTime) throws IOException {
            if (localDateTime == null) {
                jsonWriter.nullValue();
            } else {
                jsonWriter.value(localDateTime.format(formatter));
            }

        }

        @Override
        public LocalDateTime read(JsonReader jsonReader) throws IOException {
            String value = jsonReader.nextString();

            return value != null ? LocalDateTime.parse(value, formatter) : null;
        }
    }


    private static class DurationAdapter extends TypeAdapter<Duration> {
        DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

        @Override
        public void write(JsonWriter jsonWriter, Duration duration) throws IOException {
            if (duration == null) {
                jsonWriter.nullValue();
            } else {
                jsonWriter.value(duration.toMinutes());
            }

        }

        @Override
        public Duration read(JsonReader jsonReader) throws IOException {
            long minutes = jsonReader.nextLong();

            return Duration.ofMinutes(minutes);
        }
    }


    private void handleGetAll(HttpExchange exchange) throws IOException {

      //  GsonBuilder gsonBuilder = new GsonBuilder();

        Gson gson = new GsonBuilder()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .registerTypeAdapter(Duration.class, new DurationAdapter())
                .setPrettyPrinting().create();

        List<Task> allTask = new ArrayList<>(manager.getTasks().values());
        System.out.println("handleGet: " + allTask);

        if (allTask.isEmpty()) {
            sendNotFound(exchange);
            System.out.println("list is empty");
            return;
        }

      String response = gson.toJson(allTask);
        sendText(exchange, response);

    }

    private void handleGetId(HttpExchange exchange) {
    }

    private void handleCreate(HttpExchange exchange) {
    }

    private void handleUpdate(HttpExchange exchange) {
    }

    private void handleDeleteById(HttpExchange exchange) {
    }

}
