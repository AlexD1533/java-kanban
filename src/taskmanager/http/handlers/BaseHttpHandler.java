package taskmanager.http.handlers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import com.sun.net.httpserver.HttpExchange;
import taskmanager.http.Endpoint;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Optional;

public class BaseHttpHandler {

    protected final Gson gson;

    public BaseHttpHandler() {
        this.gson = createGson();
    }

    private Gson createGson() {
        return  new GsonBuilder()
                .registerTypeAdapter(LocalDateTime.class, new TaskHandler.LocalDateTimeAdapter())
                .registerTypeAdapter(Duration.class, new TaskHandler.DurationAdapter())
                .setPrettyPrinting().create();
    }

    Optional<Integer> getTaskId(HttpExchange exchange) {
        String[] paramPath = exchange.getRequestURI().toString().split("/");
        try {
            if (paramPath[paramPath.length - 1] == null || paramPath[paramPath.length - 1].isBlank()) {
                return Optional.empty();
            } else {
                return Optional.of(Integer.parseInt(paramPath[paramPath.length - 1]));
            }
        } catch (NumberFormatException e) {
            return Optional.empty();

        }
    }


    protected static class LocalDateTimeAdapter extends TypeAdapter<LocalDateTime> {
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


    protected static class DurationAdapter extends TypeAdapter<Duration> {
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


    protected Endpoint getEndpoint(String BASE_PATH_ENDPOINT, String requestPath, String requestMethod) {

        System.out.println(BASE_PATH_ENDPOINT + " " + requestPath + " " + requestMethod );


        String[] pathParts = requestPath.split("/");
        System.out.println("parts: " + Arrays.toString(pathParts) + " parts length " + pathParts.length);

        if (requestMethod.equals("GET") && pathParts[1].equals(BASE_PATH_ENDPOINT) && pathParts.length == 2) {
            return Endpoint.GET_ALL;
        } else if (requestMethod.equals("GET") && pathParts[1].equals(BASE_PATH_ENDPOINT) && pathParts.length == 3) {
            return Endpoint.GET_BY_ID;
        }
        else if (requestMethod.equals("DELETE") && pathParts[1].equals(BASE_PATH_ENDPOINT) && pathParts.length == 3) {
            return Endpoint.DELETE;
        } else if (requestMethod.equals("POST") && pathParts[1].equals(BASE_PATH_ENDPOINT) && pathParts.length == 2) {
            return Endpoint.POST_CREATE;
        } else if (requestMethod.equals("POST") && pathParts[1].equals(BASE_PATH_ENDPOINT) && pathParts.length == 3) {
            return Endpoint.POST_UPDATE;
        } else {
            return Endpoint.UNKNOWN;
        }
    }

    protected void sendText(HttpExchange h, String text) throws IOException {

        System.out.println("sendText " + text);

        byte[] resp = text.getBytes(StandardCharsets.UTF_8);
        h.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
        h.sendResponseHeaders(200, resp.length);
        h.getResponseBody().write(resp);
        h.close();
    }

    protected void sendNotFound(HttpExchange h) throws IOException {
        h.sendResponseHeaders(404, 0);
        h.close();
    }

    protected void sendHasInteractions(HttpExchange h, String text) throws IOException {
        byte[] resp = text.getBytes(StandardCharsets.UTF_8);
        h.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
        h.sendResponseHeaders(406, 0);
        h.close();
    }

}