package imangazaliev.scripto.utils;

import android.os.Handler;
import android.os.Looper;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import imangazaliev.scripto.java.ScriptoFunctionCall;

public class ScriptoUtils {

    public static <T> void validateScriptInterface(Class<T> service) {
        if (!service.isInterface()) {
            throw new IllegalArgumentException("API declarations must be interfaces.");
        }
        // Prevent script interfaces from extending other interfaces. This not only avoids a bug in
        // Android (http://b.android.com/58753) but it forces composition of API declarations which is
        // the recommended pattern.
        if (service.getInterfaces().length > 0) {
            throw new IllegalArgumentException("Script interfaces must not extend other interfaces.");
        }
    }

    public static void checkNotNull(Object object, String message) {
        if (object == null) {
            throw new NullPointerException(message);
        }
    }

    public static boolean isPrimitiveWrapper(Class<?> type) {
        return (Boolean.class == type)
                || (Byte.class == type)
                || (Character.class == type)
                || (Double.class == type)
                || (Float.class == type)
                || (Integer.class == type)
                || (Long.class == type)
                || (Short.class == type);
    }

    public static boolean hasNull(Object[] objects) {
        for (Object object : objects) {
            if (object == null) {
                return true;
            }
        }
        return false;
    }

    public static Class<?> getCallResponseType(Method method) {
        Type returnType = method.getGenericReturnType();
        //метод обязательно должен возвращать ScriptoFunctionCall
        if (!(method.getReturnType().isAssignableFrom(ScriptoFunctionCall.class))) {
            throw new IllegalArgumentException("Call return type must be parameterized as ScriptoFunctionCall<Foo>");
        }

        if (!(returnType instanceof ParameterizedType)) {
            throw new IllegalArgumentException("Call return type must be parameterized as ScriptoFunctionCall<Foo>");
        }
        return (Class<?>) ((ParameterizedType) returnType).getActualTypeArguments()[0];
    }

    public static void runOnUi(Runnable task) {
        new Handler(Looper.getMainLooper()).post(task);
    }

}
