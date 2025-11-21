package com.example.onharu.global.util;

import com.google.gson.Gson;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import lombok.experimental.UtilityClass;

@UtilityClass
public class GsonUtil {

    private static final Gson gson = new Gson();

    public byte[] toJson(Long scheduleId, String title, String body,
            LocalDateTime scheduledDateTime) {
        Map<String, Object> payload = new HashMap<>();
        payload.put("scheduleId", null);
        payload.put("title", title);
        payload.put("body", body);
        payload.put("scheduledDateTime", scheduledDateTime);

        return gson.toJson(payload).getBytes(StandardCharsets.UTF_8);
    }
}
