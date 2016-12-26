package imangazaliev.scripto.java;

import android.util.Log;
import android.webkit.JavascriptInterface;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Iterator;

import imangazaliev.scripto.Scripto;
import imangazaliev.scripto.ScriptoException;
import imangazaliev.scripto.utils.ScriptoLogUtils;
import imangazaliev.scripto.utils.ScriptoUtils;

/**
 * Интерфейс для вызова методов из JavaScript
 */
public class JavaInterface {

    private Scripto scripto;
    private Object interfaceObject;
    private String tag;
    private boolean annotationProtectionEnabled;

    public JavaInterface(Scripto scripto, String tag, Object jsInterface) {
        this(scripto, tag, jsInterface, new JavaInterfaceConfig());
    }

    public JavaInterface(Scripto scripto, String tag, Object jsInterface, JavaInterfaceConfig config) {
        this.scripto = scripto;
        this.interfaceObject = jsInterface;
        this.tag = tag;
        this.annotationProtectionEnabled = config.isAnnotationProtectionEnabled();
    }

    @JavascriptInterface
    public void call(final String methodName, final String jsonArgs) {
        ScriptoUtils.runOnUi(new Runnable() {
            @Override
            public void run() {
                callMethod(methodName, jsonArgs);
            }
        });
    }

    @JavascriptInterface
    public void callWithCallback(final String methodName, final String jsonArgs, final String callbackCode) {
        ScriptoUtils.runOnUi(new Runnable() {
            @Override
            public void run() {
                Object response = callMethod(methodName, jsonArgs);
                String responseJson = response == null ? "null" : scripto.getJavaToJsonConverter().convertToString(response, response.getClass());
                String responseCall = String.format("Scripto.removeCallBack('%s', '%s')", callbackCode, responseJson);
                scripto.getWebView().loadUrl("javascript:" + responseCall);
            }
        });
    }

    private Object callMethod(String methodName, String jsonArgs) {
        //получаем аргументы метода
        JavaArguments args = new JavaArguments(jsonArgs);
        //получаем метод по имени и типам аргументов
        Method method = searchMethodByName(methodName, args);

        //конвертируем JSON в объекты
        Object[] convertedArgs;
        try {
            convertedArgs = convertArgs(args.getArgs(), method.getParameterTypes());
        } catch (RuntimeException e) {
            ScriptoLogUtils.logError(e, String.format("Method '%s' call error", methodName));
            return onJsonArgumentsConversionError(methodName, jsonArgs, e);
        }

        //вызываем метод и передаем ему аргументы
        try {
            ScriptoLogUtils.logMessage(String.format("Method '%s' call success", methodName));
            return getMethodCallResponse(methodName, method, convertedArgs);
        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
            ScriptoException error = new ScriptoException("Method " + methodName + " call error", e);
            if (scripto.getErrorHandler() != null) {
                scripto.getErrorHandler().onError(error);
                return null;
            } else {
                throw error;
            }
        }
    }

    /**
     * Конветирует из JSON в объекты
     */
    private Object[] convertArgs(Object[] argsObjects, Class<?>[] parameterTypes) {
        Object[] convertedArgs = new Object[argsObjects.length];
        for (int i = 0; i < argsObjects.length; i++) {
            convertedArgs[i] = scripto.getJsonToJavaConverter().toObject(String.valueOf(argsObjects[i]), parameterTypes[i]);
        }
        return convertedArgs;
    }

    private Object onJsonArgumentsConversionError(String methodName, String json, Exception e) {
        String errorMessage = String.format("JSON conversion error. Interface: %s, method: %s, json: %s", tag, methodName, json);
        JavaScriptSecureException error = new JavaScriptSecureException(errorMessage, e);
        if (scripto.getErrorHandler() != null) {
            scripto.getErrorHandler().onError(error);
            return null;
        } else {
            throw error;
        }
    }

    /**
     * Пытается найти метод по имени. Если находятся два метода с одинаковыми именами и количеством параметров выбрасывает исключение
     */
    private Method searchMethodByName(String methodName, JavaArguments args) {
        ArrayList<Method> methodsForSearch = new ArrayList<>();
        Method[] declaredMethods = interfaceObject.getClass().getDeclaredMethods();

        for (Method declaredMethod : declaredMethods) {
            if (declaredMethod.getName().equals(methodName)) {
                methodsForSearch.add(declaredMethod);
            }
        }

        //ищем метод по количеству аргументов, убираем лишние
        for (Iterator<Method> iterator = methodsForSearch.iterator(); iterator.hasNext(); ) {
            Method method = iterator.next();
            if (method.getParameterTypes().length != args.getArgs().length) {
                iterator.remove();
            }
        }

        //Если найден только один метод, то возвращаем его
        if (methodsForSearch.size() == 1) {
            return methodsForSearch.get(0);
        } else if (methodsForSearch.size() > 1) {
            //Если найдено больше одного метода, выбрасываем исключения из-за неопределенности
            throw new ScriptoException("Found more than one method");
        } else {
            //метод не найден
            throw new ScriptoException(String.format("Method '%s' with arguments '%s' not found", methodName, args.getRaw()));
        }
    }

    private Object getMethodCallResponse(String methodName, Method method, Object[] convertedArgs) throws IllegalAccessException, InvocationTargetException {
        //проверяем защиту аннотацией
        if (!annotationProtectionEnabled) {
            //если защита аннотацией отключена просто вызываем метод
            return method.invoke(interfaceObject, convertedArgs);
        } else if (annotationProtectionEnabled && ScriptoUtils.hasSecureAnnotation(method)) {
            //если защита аннотацией включена и аннотация присутствует, вызываем метод
            return method.invoke(interfaceObject, convertedArgs);
        } else {
            return onMethodProtectionError(methodName);
        }
    }

    private Object onMethodProtectionError(String methodName) {
        JavaScriptSecureException error = new JavaScriptSecureException("Method " + methodName + " not annotated with @JavaScriptSecure annotation");
        if (scripto.getErrorHandler() != null) {
            scripto.getErrorHandler().onError(error);
            return null;
        } else {
            throw error;
        }
    }

}
