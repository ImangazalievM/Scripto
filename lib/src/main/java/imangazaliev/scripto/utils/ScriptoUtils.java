package imangazaliev.scripto.utils;

import android.os.Handler;
import android.os.Looper;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import imangazaliev.scripto.js.JavaScriptFunctionCall;
import imangazaliev.scripto.java.ScriptoSecure;

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

    public static Class<?> getCallResponseType(Method method) {
        Type returnType = method.getReturnType();
        Type returnGenericType = method.getGenericReturnType();
        //метод обязательно должен возвращать JavaScriptFunctionCall
        if (!(returnGenericType instanceof ParameterizedType)
                || !(method.getReturnType().isAssignableFrom(JavaScriptFunctionCall.class))) {
            throw new IllegalArgumentException("Call return type must be parameterized as JavaScriptFunctionCall<Foo>" + (returnType == null) + " " + (returnGenericType == null));
        }

        return (Class<?>) ((ParameterizedType) returnGenericType).getActualTypeArguments()[0];
    }


    /**
     * Проверяет наличие аннотации для защиты от несанкционированного вызова
     */

    public static boolean hasSecureAnnotation(Method method) {
        return method.isAnnotationPresent(ScriptoSecure.class);
    }

    public static void runOnUi(Runnable task) {
        new Handler(Looper.getMainLooper()).post(task);
    }

}
