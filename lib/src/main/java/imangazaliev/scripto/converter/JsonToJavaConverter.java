package imangazaliev.scripto.converter;

import com.google.gson.Gson;

public class JsonToJavaConverter {

    private Gson gson;

    public JsonToJavaConverter() {
        this(new Gson());
    }

    public JsonToJavaConverter(Gson gson) {
        this.gson = gson;
    }

    public <T> T toObject(String json, Class<T> type) {
            return gson.fromJson(json, type);
    }



}
