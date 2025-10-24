package http;

import com.google.gson.Gson;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import taskmanager.http.HttpTaskServer;
import taskmanager.http.gson.GsonUtil;
import taskmanager.manager.InMemoryTaskManager;
import taskmanager.manager.TaskManager;
import taskmanager.model.Epic;
import taskmanager.model.Subtask;
import taskmanager.model.TaskProgress;
import taskmanager.model.TaskType;
import taskmanager.model.dto.EpicDTO;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class HttpTaskManagerEpicsTest {

    TaskManager manager = new InMemoryTaskManager();
    HttpTaskServer taskServer = new HttpTaskServer(manager);
    Gson gson = GsonUtil.createGson();

    public HttpTaskManagerEpicsTest() throws IOException {
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
    public void testAddEpic() throws IOException, InterruptedException {
        EpicDTO epic = new EpicDTO(0, "Эпик 1", "Описание эпика", TaskProgress.NEW, TaskType.EPIC,
                LocalDateTime.now(), 120, LocalDateTime.now());
        String epicJson = gson.toJson(epic);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(epicJson))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode());

        List<Epic> epicsFromManager = manager.getEpics().values().stream().toList();

        assertNotNull(epicsFromManager, "Эпики не возвращаются");
        assertEquals(1, epicsFromManager.size(), "Некорректное количество эпиков");
        assertEquals("Эпик 1", epicsFromManager.get(0).getName(), "Некорректное имя эпика");
    }

    @Test
    public void testGetAllEpics() throws IOException, InterruptedException {
        manager.createTask(TaskType.EPIC, "Тестовый эпик", "Описание", 0,
                TaskProgress.NEW, "2005-12-12T00:00", 120, "2005-12-12T02:00");

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        EpicDTO[] epics = gson.fromJson(response.body(), EpicDTO[].class);
        assertNotNull(epics, "Эпики не должны быть null");
        assertEquals(1, epics.length, "Должен быть один эпик");
        assertEquals("Тестовый эпик", epics[0].getName(), "Некорректное имя эпика");
    }

    @Test
    public void testGetAllEpicsWhenEmpty() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(204, response.statusCode());
    }

    @Test
    public void testGetEpicById() throws IOException, InterruptedException {
        manager.createTask(TaskType.EPIC, "Тестовый эпик", "Описание", 0,
                TaskProgress.NEW, "2005-12-12T00:00", 120, "2005-12-12T02:00");

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics/0");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        EpicDTO epic = gson.fromJson(response.body(), EpicDTO.class);
        assertNotNull(epic, "Эпик не должен быть null");
        assertEquals("Тестовый эпик", epic.getName(), "Некорректное имя эпика");
        assertEquals(0, epic.getId(), "Некорректный ID эпика");
    }

    @Test
    public void testGetEpicSubtasks() throws IOException, InterruptedException {
        // Создаем эпик и подзадачи
        manager.createTask(TaskType.EPIC, "Эпик", "Описание", 0,
                TaskProgress.NEW, "2005-12-12T00:00", 120, "2005-12-12T02:00");
        manager.createTask(TaskType.SUBTASK, "Подзадача 1", "Описание", 0,
                TaskProgress.NEW, "2005-12-13T00:00", 120, "2005-12-13T02:00");
        manager.createTask(TaskType.SUBTASK, "Подзадача 2", "Описание", 0,
                TaskProgress.NEW, "2005-12-14T00:00", 120, "2005-12-14T02:00");

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics/0/subtasks");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        Subtask[] subtasks = gson.fromJson(response.body(), Subtask[].class);
        assertNotNull(subtasks, "Подзадачи не должны быть null");
        assertEquals(2, subtasks.length, "Должно быть две подзадачи");
    }

    @Test
    public void testGetEpicSubtasksWhenEmpty() throws IOException, InterruptedException {
        manager.createTask(TaskType.EPIC, "Эпик", "Описание", 0,
                TaskProgress.NEW, "2005-12-12T00:00", 120, "2005-12-12T02:00");

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics/0/subtasks");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(404, response.statusCode());
        assertTrue(response.body().contains("пуст"), "Сообщение об ошибке должно указывать на пустой список");
    }

    @Test
    public void testUpdateEpic() throws IOException, InterruptedException {
        manager.createTask(TaskType.EPIC, "Исходный эпик", "Описание", 0,
                TaskProgress.NEW, "2005-12-12T00:00", 120, "2005-12-12T02:00");

        EpicDTO updatedEpic = new EpicDTO(0, "Обновленный эпик", "Новое описание",
                TaskProgress.IN_PROGRESS, TaskType.EPIC, LocalDateTime.now(), 120, LocalDateTime.now());
        String epicJson = gson.toJson(updatedEpic);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics/0");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(epicJson))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode());
        assertEquals("Эпик изменен", response.body());

        Epic epicFromManager = manager.getEpic(0);
        assertEquals("Обновленный эпик", epicFromManager.getName(), "Имя эпика не обновилось");
        assertEquals("Новое описание", epicFromManager.getDescription(), "Описание эпика не обновилось");
    }

    @Test
    public void testDeleteEpic() throws IOException, InterruptedException {
        manager.createTask(TaskType.EPIC, "Эпик для удаления", "Описание", 0,
                TaskProgress.NEW, "2005-12-12T00:00", 120, "2005-12-12T02:00");

        assertNotNull(manager.getEpic(0), "Эпик должен существовать перед удалением");

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics/0");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .DELETE()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode());
        assertEquals("Эпик удален", response.body());

        assertThrows(Exception.class, () -> manager.getEpic(0), "Эпик должен быть удален");
    }

    @Test
    public void testDeleteEpicWithSubtasks() throws IOException, InterruptedException {
        // Создаем эпик с подзадачами
        manager.createTask(TaskType.EPIC, "Эпик", "Описание", 0,
                TaskProgress.NEW, "2005-12-12T00:00", 120, "2005-12-12T02:00");
        manager.createTask(TaskType.SUBTASK, "Подзадача 1", "Описание", 0,
                TaskProgress.NEW, "2005-12-13T00:00", 120, "2005-12-13T02:00");
        manager.createTask(TaskType.SUBTASK, "Подзадача 2", "Описание", 0,
                TaskProgress.NEW, "2005-12-14T00:00", 120, "2005-12-14T02:00");

        assertEquals(2, manager.getAllSubtasks().size(), "Должно быть 2 подзадачи перед удалением");

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics/0");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .DELETE()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode());

        assertThrows(Exception.class, () -> manager.getEpic(0), "Эпик должен быть удален");
        assertEquals(0, manager.getAllSubtasks().size(), "Все подзадачи должны быть удалены вместе с эпиком");
    }
}