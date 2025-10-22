package http;

import com.google.gson.Gson;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import taskmanager.http.HttpTaskServer;
import taskmanager.http.gson.GsonUtil;
import taskmanager.http.gson.ListTaskToken;
import taskmanager.manager.InMemoryTaskManager;
import taskmanager.manager.TaskManager;
import taskmanager.model.Task;
import taskmanager.model.TaskProgress;
import taskmanager.model.TaskType;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class HttpTaskManagerTasksTest {

    TaskManager manager = new InMemoryTaskManager();
    HttpTaskServer taskServer = new HttpTaskServer(manager);
    Gson gson = GsonUtil.createGson();

    public HttpTaskManagerTasksTest() throws IOException {
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
    public void testAddTask() throws IOException, InterruptedException {
        Task task = new Task(0, "Задача 1", "Выполнить работу", TaskType.TASK,
                TaskProgress.NEW, "2005-12-12T00:00", 120);
        String taskJson = gson.toJson(task);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode());

        List<Task> tasksFromManager = manager.getTasks().values().stream().toList();

        assertNotNull(tasksFromManager, "Задачи не возвращаются");
        assertEquals(1, tasksFromManager.size(), "Некорректное количество задач");
        assertEquals("Задача 1", tasksFromManager.get(0).getName(), "Некорректное имя задачи");
    }

    @Test
    public void testGetAllTasks() throws IOException, InterruptedException {
        manager.createTask(TaskType.TASK, "Тестовая задача", "Описание", 0,
                TaskProgress.NEW, "2005-12-12T00:00", 120, "2005-12-12T02:00");

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());

        List<Task> tasks = gson.fromJson(response.body(), new ListTaskToken().getType());
        assertNotNull(tasks, "Задачи не должны быть null");
        assertEquals(1, tasks.size(), "Должна быть одна задача");
        assertEquals("Тестовая задача", tasks.get(0).getName(), "Некорректное имя задачи");
    }

    @Test
    public void testGetAllTasksWhenEmpty() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(404, response.statusCode());
        assertEquals("Список пуст", response.body());
    }

    @Test
    public void testGetTaskById() throws IOException, InterruptedException {
        // Создаем задачу
        manager.createTask(TaskType.TASK, "Тестовая задача", "Описание", 0,
                TaskProgress.NEW, "2005-12-12T00:00", 120, "2005-12-12T02:00");

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/0");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());

        Task task = gson.fromJson(response.body(), Task.class);
        assertNotNull(task, "Задача не должна быть null");
        assertEquals("Тестовая задача", task.getName(), "Некорректное имя задачи");
        assertEquals(0, task.getId(), "Некорректный ID задачи");
    }

    @Test
    public void testGetTaskByIdNotFound() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/999");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(404, response.statusCode());
        assertTrue(response.body().contains("не существует"), "Сообщение об ошибке не соответствует ожидаемому");
    }

    @Test
    public void testGetTaskByIdInvalidId() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/invalid");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(404, response.statusCode());
    }

    @Test
    public void testUpdateTask() throws IOException, InterruptedException {

        manager.createTask(TaskType.TASK, "Исходная задача", "Описание", 0,
                TaskProgress.NEW, "2005-12-12T00:00", 120, "2005-12-12T02:00");


        Task updatedTask = new Task(0, "Обновленная задача", "Новое описание", TaskType.TASK,
                TaskProgress.IN_PROGRESS, "2005-12-12T00:00", 120);
        String taskJson = gson.toJson(updatedTask);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/0");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode());
        assertEquals("Задача изменена", response.body());

        Task taskFromManager = manager.getTask(0);
        assertEquals("Обновленная задача", taskFromManager.getName(), "Имя задачи не обновилось");
        assertEquals("Новое описание", taskFromManager.getDescription(), "Описание задачи не обновилось");
        assertEquals(TaskProgress.IN_PROGRESS, taskFromManager.getStatus(), "Статус задачи не обновился");
    }

    @Test
    public void testUpdateTaskNotFound() throws IOException, InterruptedException {
        Task task = new Task(999, "Несуществующая задача", "Описание", TaskType.TASK,
                TaskProgress.NEW, "2005-12-12T00:00", 120);
        String taskJson = gson.toJson(task);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/999");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(404, response.statusCode());
    }

    @Test
    public void testDeleteTask() throws IOException, InterruptedException {
        // Создаем задачу
        manager.createTask(TaskType.TASK, "Задача для удаления", "Описание", 0,
                TaskProgress.NEW, "2005-12-12T00:00", 120, "2005-12-12T02:00");
        assertNotNull(manager.getTask(0), "Задача должна существовать перед удалением");

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/0");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .DELETE()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode());
        assertEquals("Задача удалена", response.body());

        assertThrows(Exception.class, () -> manager.getTask(0), "Задача должна быть удалена");
    }

    @Test
    public void testDeleteTaskNotFound() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/999");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .DELETE()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(404, response.statusCode());
    }

    @Test
    public void testUnknownEndpoint() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/unknown/path");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(404, response.statusCode());
        assertEquals("Path not found", response.body());
    }

    @Test
    public void testCreateTaskWithTimeConflict() throws IOException, InterruptedException {
        // Создаем первую задачу
        Task firstTask = new Task(0, "Первая задача", "Описание", TaskType.TASK,
                TaskProgress.NEW, "2005-12-12T00:00", 120);
        String firstTaskJson = gson.toJson(firstTask);

        HttpClient client = HttpClient.newHttpClient();

        URI createUrl = URI.create("http://localhost:8080/tasks");
        HttpRequest createRequest = HttpRequest.newBuilder()
                .uri(createUrl)
                .POST(HttpRequest.BodyPublishers.ofString(firstTaskJson))
                .build();

        HttpResponse<String> createResponse = client.send(createRequest, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, createResponse.statusCode());

        Task conflictingTask = new Task(1, "Конфликтующая задача", "Описание", TaskType.TASK,
                TaskProgress.NEW, "2005-12-12T01:00", 60); // Время пересекается
        String conflictingTaskJson = gson.toJson(conflictingTask);

        HttpRequest conflictRequest = HttpRequest.newBuilder()
                .uri(createUrl)
                .POST(HttpRequest.BodyPublishers.ofString(conflictingTaskJson))
                .build();

        HttpResponse<String> conflictResponse = client.send(conflictRequest, HttpResponse.BodyHandlers.ofString());

        assertEquals(406, conflictResponse.statusCode());
        assertTrue(conflictResponse.body().contains("пересекаются"), "Сообщение об ошибке должно указывать на пересечение времени");

    }
}