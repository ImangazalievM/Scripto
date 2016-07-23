package imangazaliev.scripto.js;

import android.util.Log;
import android.webkit.JavascriptInterface;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Iterator;

import imangazaliev.scripto.Scripto;
import imangazaliev.scripto.ScriptoException;
import imangazaliev.scripto.utils.ScriptoUtils;

/**
 * Интерфейс для вызова методов из JavaScript
 */
public class ScriptoInterface {

    private Scripto scripto;
    private Object javaScriptInterface;
    private boolean annotationProtectionEnabled;

    public ScriptoInterface(Scripto scripto, Object jsInterface) {
        this(scripto, jsInterface, new ScriptoInterfaceConfig());
    }

    public ScriptoInterface(Scripto scripto, Object jsInterface, ScriptoInterfaceConfig config) {
        this.scripto = scripto;
        this.javaScriptInterface = jsInterface;
    }

    @JavascriptInterface
    public void call(final String methodName, final String jsonArgs) {
        ScriptoUtils.runOnUi(new Runnable() {
            @Override
            public void run() {
                callOnUi(methodName, jsonArgs);
            }
        });
    }

    @JavascriptInterface
    public void callWithCallback(final String methodName, final String jsonArgs, final String callbackCode) {
        ScriptoUtils.runOnUi(new Runnable() {
            @Override
            public void run() {
                Object response = callOnUi(methodName, jsonArgs);
                String responseCall = String.format("Scripto.removeCallBack('%s', '%s')", callbackCode, scripto.getJavaScriptConverter().convertToString(response, response.getClass()));
                scripto.getWebView().loadUrl("javascript:" + responseCall);
            }
        });
    }

    private Object callOnUi(String methodName, String jsonArgs) {
        //получаем аргументы метода
        JavaArguments args = new JavaArguments(jsonArgs);
        //получаем метод по имени и типам аргументов
        Method method = searchMethodByName(methodName, args);
        Object[] convertedArgs = convertArgs(args, method.getParameterTypes());

        //вызываем метод и передаем ему аргументы
        try {
            //проверяем защиту аннотацией
            if (annotationProtectionEnabled && hasSecureAnnotation(method)) {
                //если защита аннотацией включена и аннотация присутствует, вызываем метод
                return method.invoke(javaScriptInterface, convertedArgs);
            } else if (!annotationProtectionEnabled) {
                //если защита аннотацией отключена просто вызываем метод
                return method.invoke(javaScriptInterface, convertedArgs);
            } else {
                Log.e("Scripto", "Method " + methodName + " not annotated with @ScriptoSecure annotation");
            }
        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
            throw new ScriptoException("Method call error", e);
        }
        return null;
    }

    /**
     * Конветирует из JSON в объекты
     */
    private Object[] convertArgs(JavaArguments args, Class<?>[] parameterTypes) {
        Object[] argsObjects = args.getArgs();
        Object[] convertedArgs = new Object[argsObjects.length];
        for (int i = 0; i < argsObjects.length; i++) {
            convertedArgs[i] = scripto.getJavaConverter().toObject(String.valueOf(argsObjects[i]), parameterTypes[i]);
        }
        return convertedArgs;
    }

    /**
     * Пытается найти метод по имени. Если находятся два метода с одинаковыми именами и количеством параметров выбрасывает исключение
     */
    private Method searchMethodByName(String methodName, JavaArguments args) {
        ArrayList<Method> methodsForSearch = new ArrayList<>();
        Method[] declaredMethods = javaScriptInterface.getClass().getDeclaredMethods();

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

    /**
     * Проверяет наличие аннотации для защиты от несанкционированного вызова
     */

    private boolean hasSecureAnnotation(Method method) {
        return method.isAnnotationPresent(ScriptoSecure.class);
    }


}
