package com.example.onharu.global.util;

import com.google.gson.Gson;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import lombok.experimental.UtilityClass;

@UtilityClass
public class GsonUtil {

    private static final Gson gson = new Gson();

    public byte[] toJson(String title, String body) {
        Map<String, Object> payload = new HashMap<>();
        payload.put("title", title);
        payload.put("body", body);

        return gson.toJson(payload).getBytes(StandardCharsets.UTF_8);
    }
}
