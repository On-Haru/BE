package com.example.onharu.global.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import lombok.experimental.UtilityClass;

@UtilityClass
public class GsonUtil {

    private static final DateTimeFormatter ISO_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    private static final Gson gson = new GsonBuilder()
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeSerializer())
            .create();

    public byte[] toJson(Long scheduleId, String title, String body,
            LocalDateTime scheduledDateTime) {
        Map<String, Object> payload = new HashMap<>();
        payload.put("scheduleId", scheduleId);
        payload.put("title", title);
        payload.put("body", body);
        payload.put("scheduledDateTime", scheduledDateTime);

        return gson.toJson(payload).getBytes(StandardCharsets.UTF_8);
    }

    private static class LocalDateTimeSerializer implements JsonSerializer<LocalDateTime> {

        @Override
        public JsonElement serialize(LocalDateTime src, Type typeOfSrc,
                JsonSerializationContext context) {
            if (src == null) {
                return null;
            }
            return new JsonPrimitive(src.format(ISO_FORMATTER));
        }
    }
}
