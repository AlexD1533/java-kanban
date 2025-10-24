package http;

import com.google.gson.Gson;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import taskmanager.http.HttpTaskServer;
import taskmanager.http.gson.GsonUtil;
import taskmanager.http.gson.ListEpicDTOToken;
import taskmanager.manager.InMemoryTaskManager;
import taskmanager.manager.TaskManager;
import taskmanager.model.TaskProgress;
import taskmanager.model.TaskType;
import taskmanager.model.dto.EpicDTO;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class HttpTaskManagerPrioritizedTest {

    TaskManager manager = new InMemoryTaskManager();
    HttpTaskServer taskServer = new HttpTaskServer(manager);
    Gson gson = GsonUtil.createGson();

    public HttpTaskManagerPrioritizedTest() throws IOException {
    }

    @BeforeEach
    public void setUp() {
        manager.deleteAllTasks();
        taskServer.start();
    }

    @AfterEach
    public void shutDown() {
        taskServer.stop();
    }

    @Test
    public void testGetPrioritizedTasks() throws IOException, InterruptedException {
        manager.createTask(TaskType.TASK, "Задача 2", "Описание", 0,
                TaskProgress.NEW, "2005-12-13T00:00", 120, "2005-12-13T02:00");
        manager.createTask(TaskType.TASK, "Задача 1", "Описание", 0,
                TaskProgress.NEW, "2005-12-12T00:00", 120, "2005-12-12T02:00");
        manager.createTask(TaskType.TASK, "Задача 3", "Описание", 0,
                TaskProgress.NEW, "2005-12-14T00:00", 120, "2005-12-14T02:00");

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/prioritized");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        EpicDTO[] prioritized = gson.fromJson(response.body(), EpicDTO[].class);
        assertNotNull(prioritized, "Приоритизированные задачи не должны быть null");
        assertEquals(3, prioritized.length, "Должно быть 3 приоритизированные задачи");

        assertEquals("Задача 1", prioritized[0].getName(), "Первая задача должна быть Задача 1");
        assertEquals("Задача 2", prioritized[1].getName(), "Вторая задача должна быть Задача 2");
        assertEquals("Задача 3", prioritized[2].getName(), "Третья задача должна быть Задача 3");
    }

    @Test
    public void testGetPrioritizedTasksWithDifferentTypes() throws IOException, InterruptedException {

        manager.createTask(TaskType.TASK, "Задача", "Описание", 0,
                TaskProgress.NEW, "2005-12-12T00:00", 120, "2005-12-12T02:00");
        manager.createTask(TaskType.EPIC, "Эпик", "Описание", 0,
                TaskProgress.NEW, "2005-12-13T00:00", 120, "2005-12-13T02:00");
        manager.createTask(TaskType.SUBTASK, "Подзадача", "Описание", 1,
                TaskProgress.NEW, "2005-12-14T00:00", 120, "2005-12-14T02:00");

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/prioritized");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        System.out.println("response " + response);

        System.out.println(manager.getAllTasks());
        System.out.println(manager.getPrioritizedTasks());


        List<EpicDTO> prioritized = gson.fromJson(response.body(), new ListEpicDTOToken().getType());
        assertNotNull(prioritized, "Приоритизированные задачи не должны быть null");
        assertEquals(3, prioritized.size(), "Должно быть 3 приоритизированные задачи");

        System.out.println(prioritized);

        assertEquals("Задача", prioritized.get(0).getName(), "Первая задача должна быть Задача");
        assertEquals("Эпик", prioritized.get(1).getName(), "Вторая задача должна быть Эпик");
        assertEquals("Подзадача", prioritized.get(2).getName(), "Третья задача должна быть Подзадача");
    }

    @Test
    public void testGetPrioritizedTasksWhenEmpty() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/prioritized");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        EpicDTO[] prioritized = gson.fromJson(response.body(), EpicDTO[].class);
        assertNotNull(prioritized, "Приоритизированные задачи не должны быть null");
        assertEquals(0, prioritized.length, "Список приоритизированных задач должен быть пуст");
    }

    @Test
    public void testPrioritizedTasksOrder() throws IOException, InterruptedException {
        manager.createTask(TaskType.TASK, "Поздняя задача", "Описание", 0,
                TaskProgress.NEW, "2005-12-15T00:00", 120, "2005-12-15T02:00");
        manager.createTask(TaskType.TASK, "Ранняя задача", "Описание", 0,
                TaskProgress.NEW, "2005-12-10T00:00", 120, "2005-12-10T02:00");
        manager.createTask(TaskType.TASK, "Средняя задача", "Описание", 0,
                TaskProgress.NEW, "2005-12-12T00:00", 120, "2005-12-12T02:00");

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/prioritized");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        EpicDTO[] prioritized = gson.fromJson(response.body(), EpicDTO[].class);

        assertEquals("Ранняя задача", prioritized[0].getName(), "Первая должна быть самая ранняя задача");
        assertEquals("Средняя задача", prioritized[1].getName(), "Вторая должна быть средняя задача");
        assertEquals("Поздняя задача", prioritized[2].getName(), "Третья должна быть поздняя задача");
    }
}