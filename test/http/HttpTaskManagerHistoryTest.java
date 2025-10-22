package http;

import com.google.gson.Gson;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import taskmanager.http.HttpTaskServer;
import taskmanager.http.gson.GsonUtil;
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

import static org.junit.jupiter.api.Assertions.*;

public class HttpTaskManagerHistoryTest {

    TaskManager manager = new InMemoryTaskManager();
    HttpTaskServer taskServer = new HttpTaskServer(manager);
    Gson gson = GsonUtil.createGson();

    public HttpTaskManagerHistoryTest() throws IOException {
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
    public void testGetHistory() throws IOException, InterruptedException {
        manager.createTask(TaskType.TASK, "Задача 1", "Описание", 0,
                TaskProgress.NEW, "2005-12-12T00:00", 120, "2005-12-12T02:00");
        manager.createTask(TaskType.EPIC, "Эпик 1", "Описание", 0,
                TaskProgress.NEW, "2005-12-13T00:00", 120, "2005-12-13T02:00");
        manager.createTask(TaskType.SUBTASK, "Подзадача 1", "Описание", 1,
                TaskProgress.NEW, "2005-12-14T00:00", 120, "2005-12-14T02:00");

        manager.getTask(0);
        manager.getEpic(1);
        manager.getSubtask(2);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/history");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        EpicDTO[] history = gson.fromJson(response.body(), EpicDTO[].class);
        assertNotNull(history, "История не должна быть null");
        assertEquals(3, history.length, "Должно быть 3 задачи в истории");

        List<String> historyNames = List.of(history[0].getName(), history[1].getName(), history[2].getName());
        assertTrue(historyNames.contains("Задача 1"), "История должна содержать Задача 1");
        assertTrue(historyNames.contains("Эпик 1"), "История должна содержать Эпик 1");
        assertTrue(historyNames.contains("Подзадача 1"), "История должна содержать Подзадача 1");
    }

    @Test
    public void testGetHistoryWithDuplicates() throws IOException, InterruptedException {
        manager.createTask(TaskType.TASK, "Задача", "Описание", 0,
                TaskProgress.NEW, "2005-12-12T00:00", 120, "2005-12-12T02:00");

        manager.getTask(0);
        manager.getTask(0);
        manager.getTask(0);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/history");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        EpicDTO[] history = gson.fromJson(response.body(), EpicDTO[].class);
        assertNotNull(history, "История не должна быть null");
        assertEquals(1, history.length, "Дубликаты не должны появляться в истории");
        assertEquals("Задача", history[0].getName(), "История должна содержать Задача");
    }

    @Test
    public void testGetHistoryOrder() throws IOException, InterruptedException {

        manager.createTask(TaskType.TASK, "Задача 1", "Описание", 0,
                TaskProgress.NEW, "2005-12-12T00:00", 120, "2005-12-12T02:00");
        manager.createTask(TaskType.TASK, "Задача 2", "Описание", 0,
                TaskProgress.NEW, "2005-12-13T00:00", 120, "2005-12-13T02:00");
        manager.createTask(TaskType.TASK, "Задача 3", "Описание", 0,
                TaskProgress.NEW, "2005-12-14T00:00", 120, "2005-12-14T02:00");

        manager.getTask(0);
        manager.getTask(1);
        manager.getTask(2);
        manager.getTask(0);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/history");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        EpicDTO[] history = gson.fromJson(response.body(), EpicDTO[].class);

        assertEquals("Задача 2", history[0].getName(), "Первая задача в истории должна быть Задача 2");
        assertEquals("Задача 3", history[1].getName(), "Вторая задача в истории должна быть Задача 3");
        assertEquals("Задача 1", history[2].getName(), "Последняя задача в истории должна быть Задача 1");
    }

    @Test
    public void testGetEmptyHistory() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/history");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(404, response.statusCode());

    }

    @Test
    public void testHistoryAfterDeletion() throws IOException, InterruptedException {

        manager.createTask(TaskType.TASK, "Задача 1", "Описание", 0,
                TaskProgress.NEW, "2005-12-12T00:00", 120, "2005-12-12T02:00");
        manager.createTask(TaskType.TASK, "Задача 2", "Описание", 0,
                TaskProgress.NEW, "2005-12-13T00:00", 120, "2005-12-13T02:00");


        manager.getTask(0);
        manager.getTask(1);


        manager.deleteTasksById(TaskType.TASK, 0);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/history");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        EpicDTO[] history = gson.fromJson(response.body(), EpicDTO[].class);
        assertNotNull(history, "История не должна быть null");
        assertEquals(1, history.length, "Должна остаться одна задача в истории после удаления");
        assertEquals("Задача 2", history[0].getName(), "В истории должна остаться Задача 2");
    }

    @Test
    public void testHistoryWithMixedTaskTypes() throws IOException, InterruptedException {

        manager.createTask(TaskType.TASK, "Задача", "Описание", 0,
                TaskProgress.NEW, "2005-12-12T00:00", 120, "2005-12-12T02:00");
        manager.createTask(TaskType.EPIC, "Эпик", "Описание", 0,
                TaskProgress.NEW, "2005-12-13T00:00", 120, "2005-12-13T02:00");
        manager.createTask(TaskType.SUBTASK, "Подзадача", "Описание", 1,
                TaskProgress.NEW, "2005-12-14T00:00", 120, "2005-12-14T02:00");


        manager.getEpic(1);
        manager.getTask(0);
        manager.getSubtask(2);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/history");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        EpicDTO[] history = gson.fromJson(response.body(), EpicDTO[].class);
        assertEquals(3, history.length, "Должно быть 3 задачи в истории");

        List<String> historyNames = List.of(history[0].getName(), history[1].getName(), history[2].getName());
        assertTrue(historyNames.contains("Задача"), "История должна содержать Задача");
        assertTrue(historyNames.contains("Эпик"), "История должна содержать Эпик");
        assertTrue(historyNames.contains("Подзадача"), "История должна содержать Подзадача");
    }
}