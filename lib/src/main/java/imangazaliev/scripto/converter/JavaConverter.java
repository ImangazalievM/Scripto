package imangazaliev.scripto.converter;

import com.google.gson.Gson;

import imangazaliev.scripto.ScriptoException;

public class JavaConverter {

    private Gson gson;

    public JavaConverter() {
        this(new Gson());
    }

    public JavaConverter(Gson gson) {
        this.gson = gson;
    }

    public <T> T toObject(String json, Class<T> type) {
            return gson.fromJson(json, type);
    }



}
