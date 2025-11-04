package onegame.net.util;

import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.SerializationFeature;

public class JsonHelper {
    private static final JsonMapper mapper = JsonMapper.builder()
        .enable(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT)
        .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
        .build();

    public static String toJson(Object obj) {
        if (obj == null) return null;
        try {
            return mapper.writeValueAsString(obj);
        } catch (Exception e) {
            System.err.println("[JsonHelper] Errore di serializzazione: " + e.getMessage());
            return null;
        }
    }

    public static <T> T fromJson(String json, Class<T> clazz) {
        if (json == null || json.isEmpty()) return null;
        try {
            return mapper.readValue(json, clazz);
        } catch (Exception e) {
            System.err.println("[JsonHelper] Errore di deserializzazione: " + e.getMessage());
            return null;
        }
    }

    public static <T> T fromObject(Object obj, Class<T> clazz) {
        if (obj == null) return null;
        return fromJson(obj.toString(), clazz);
    }
}
