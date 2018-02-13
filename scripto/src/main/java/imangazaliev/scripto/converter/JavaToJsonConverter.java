package imangazaliev.scripto.converter;

import com.google.gson.Gson;

public class JavaToJsonConverter {

    private Gson gson;

    public JavaToJsonConverter() {
        this(new Gson());
    }

    public JavaToJsonConverter(Gson gson) {
        this.gson = gson;
    }

    public String convertToString(Object object, Class<?> type) {
        return gson.toJson(object, type);
    }

}
