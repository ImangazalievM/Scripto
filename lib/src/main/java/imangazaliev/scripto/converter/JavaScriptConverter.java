package imangazaliev.scripto.converter;

import com.google.gson.Gson;

public class JavaScriptConverter {

    private Gson gson;

    public JavaScriptConverter() {
        this(new Gson());
    }

    public JavaScriptConverter(Gson gson) {
        this.gson = gson;
    }

    public String convertToString(Object object, Class<?> type) {
        return gson.toJson(object, type);
    }

}
