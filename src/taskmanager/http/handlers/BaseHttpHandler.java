package taskmanager.http.handlers;

import com.sun.net.httpserver.HttpExchange;
import taskmanager.http.Endpoint;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class BaseHttpHandler {

    protected Endpoint getEndpoint(String BASE_PATH_ENDPOINT, String requestPath, String requestMethod) {

        System.out.println(BASE_PATH_ENDPOINT + " " + requestPath + " " + requestMethod );


        String[] pathParts = requestPath.split("/");
        System.out.println("parts: " + Arrays.toString(pathParts) + " parts length " + pathParts.length);

        if (requestMethod.equals("GET") && pathParts[1].equals(BASE_PATH_ENDPOINT) && pathParts.length == 2) {
            return Endpoint.GET_ALL;
        } else if (requestMethod.equals("GET") && pathParts[1].equals(BASE_PATH_ENDPOINT) && pathParts.length == 3) {
            return Endpoint.GET_BY_ID;
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