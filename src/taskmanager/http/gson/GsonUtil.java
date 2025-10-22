package taskmanager.http.gson;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import taskmanager.http.gson.adapter.DurationAdapter;
import taskmanager.http.gson.adapter.LocalDateTimeAdapter;

import java.time.Duration;
import java.time.LocalDateTime;

public class GsonUtil {

    public static Gson createGson() {
        return new GsonBuilder()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .registerTypeAdapter(Duration.class, new DurationAdapter())
                .setPrettyPrinting().create();
    }
}
