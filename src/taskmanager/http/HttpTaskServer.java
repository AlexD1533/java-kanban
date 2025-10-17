package taskmanager.http;

import com.sun.net.httpserver.HttpServer;
import taskmanager.http.handlers.TaskHandler;

import java.io.IOException;
import java.net.InetSocketAddress;

public class HttpTaskServer {

    private static final int PORT = 8080;

    public static void main(String[] args) throws IOException {
        HttpServer httpServer = HttpServer.create(new InetSocketAddress(PORT), 0);
httpServer.createContext("/tasks", new TaskHandler());

    }
}
