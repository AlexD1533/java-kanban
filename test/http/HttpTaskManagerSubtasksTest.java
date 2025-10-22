package http;

import com.google.gson.Gson;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import taskmanager.http.HttpTaskServer;
import taskmanager.http.gson.GsonUtil;
import taskmanager.http.gson.ListSubtaskToken;
import taskmanager.manager.InMemoryTaskManager;
import taskmanager.manager.TaskManager;
import taskmanager.model.Subtask;
import taskmanager.model.TaskProgress;
import taskmanager.model.TaskType;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class HttpTaskManagerSubtasksTest {

    TaskManager manager = new InMemoryTaskManager();
    HttpTaskServer taskServer = new HttpTaskServer(manager);
    Gson gson = GsonUtil.createGson();

    public HttpTaskManagerSubtasksTest() throws IOException {
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
    public void testAddSubtask() throws IOException, InterruptedException {
        // Сначала создаем эпик
        manager.createTask(TaskType.EPIC, "Эпик для подзадачи", "Описание эпика", 0,
                TaskProgress.NEW, "2005-12-12T00:00", 120, "2005-12-12T02:00");

        Subtask subtask = new Subtask(1, "Подзадача 1", "Описание подзадачи", TaskType.SUBTASK,
                0, TaskProgress.NEW, "2005-12-13T00:00", 120);
        String subtaskJson = gson.toJson(subtask);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(subtaskJson))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode());

        List<Subtask> subtasksFromManager = manager.getAllSubtasks().values().stream().toList();

        assertNotNull(subtasksFromManager, "Подзадачи не возвращаются");
        assertEquals(1, subtasksFromManager.size(), "Некорректное количество подзадач");
        assertEquals("Подзадача 1", subtasksFromManager.get(0).getName(), "Некорректное имя подзадачи");
        assertEquals(0, subtasksFromManager.get(0).getEpicId(), "Некорректный EpicId");
    }

    @Test
    public void testGetAllSubtasks() throws IOException, InterruptedException {
        // Создаем эпик и подзадачу
        manager.createTask(TaskType.EPIC, "Эпик", "Описание", 0,
                TaskProgress.NEW, "2005-12-12T00:00", 120, "2005-12-12T02:00");
        manager.createTask(TaskType.SUBTASK, "Тестовая подзадача", "Описание", 0,
                TaskProgress.NEW, "2005-12-13T00:00", 120, "2005-12-13T02:00");

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        List<Subtask> subtasks = gson.fromJson(response.body(), new ListSubtaskToken().getType());
        assertNotNull(subtasks, "Подзадачи не должны быть null");
        assertEquals(1, subtasks.size(), "Должна быть одна подзадача");
        assertEquals("Тестовая подзадача", subtasks.get(0).getName(), "Некорректное имя подзадачи");
    }

    @Test
    public void testGetAllSubtasksWhenEmpty() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(404, response.statusCode());
        assertEquals("Список пуст", response.body());
    }

    @Test
    public void testGetSubtaskById() throws IOException, InterruptedException {
        manager.createTask(TaskType.EPIC, "Эпик", "Описание", 0,
                TaskProgress.NEW, "2005-12-12T00:00", 120, "2005-12-12T02:00");
        manager.createTask(TaskType.SUBTASK, "Тестовая подзадача", "Описание", 0,
                TaskProgress.NEW, "2005-12-13T00:00", 120, "2005-12-13T02:00");

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks/1");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        Subtask subtask = gson.fromJson(response.body(), Subtask.class);
        assertNotNull(subtask, "Подзадача не должна быть null");
        assertEquals("Тестовая подзадача", subtask.getName(), "Некорректное имя подзадачи");
        assertEquals(1, subtask.getId(), "Некорректный ID подзадачи");
        assertEquals(0, subtask.getEpicId(), "Некорректный EpicId");
    }

    @Test
    public void testGetSubtaskByIdNotFound() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks/999");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(404, response.statusCode());
        assertTrue(response.body().contains("не существует"), "Сообщение об ошибке не соответствует ожидаемому");
    }

    @Test
    public void testUpdateSubtask() throws IOException, InterruptedException {

        manager.createTask(TaskType.EPIC, "Эпик", "Описание", 0,
                TaskProgress.NEW, "2005-12-12T00:00", 120, "2005-12-12T02:00");
        manager.createTask(TaskType.SUBTASK, "Исходная подзадача", "Описание", 0,
                TaskProgress.NEW, "2005-12-13T00:00", 120, "2005-12-13T02:00");

        Subtask updatedSubtask = new Subtask(1, "Обновленная подзадача", "Новое описание", TaskType.SUBTASK,
                0, TaskProgress.IN_PROGRESS, "2005-12-13T00:00", 120);
        String subtaskJson = gson.toJson(updatedSubtask);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks/1");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(subtaskJson))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode());
        assertEquals("Подзадача изменена", response.body());

        Subtask subtaskFromManager = manager.getSubtask(1);
        assertEquals("Обновленная подзадача", subtaskFromManager.getName(), "Имя подзадачи не обновилось");
        assertEquals("Новое описание", subtaskFromManager.getDescription(), "Описание подзадачи не обновилось");
        assertEquals(TaskProgress.IN_PROGRESS, subtaskFromManager.getStatus(), "Статус подзадачи не обновился");
    }

    @Test
    public void testDeleteSubtask() throws IOException, InterruptedException {
        // Создаем эпик и подзадачу
        manager.createTask(TaskType.EPIC, "Эпик", "Описание", 0,
                TaskProgress.NEW, "2005-12-12T00:00", 120, "2005-12-12T02:00");
        manager.createTask(TaskType.SUBTASK, "Подзадача для удаления", "Описание", 0,
                TaskProgress.NEW, "2005-12-13T00:00", 120, "2005-12-13T02:00");

        assertNotNull(manager.getSubtask(1), "Подзадача должна существовать перед удалением");

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks/1");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .DELETE()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode());
        assertEquals("Подзадача удалена", response.body());

        assertThrows(Exception.class, () -> manager.getSubtask(1), "Подзадача должна быть удалена");
    }

    @Test
    public void testCreateSubtaskWithInvalidEpic() throws IOException, InterruptedException {
        Subtask subtask = new Subtask(0, "Подзадача", "Описание", TaskType.SUBTASK,
                999, TaskProgress.NEW, "2005-12-13T00:00", 120);
        String subtaskJson = gson.toJson(subtask);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(subtaskJson))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(406, response.statusCode());
        assertTrue(response.body().contains("Эпика"), "Сообщение об ошибке должно указывать на проблему с эпиком");
    }
}