package imangazaliev.scripto.js;

import java.lang.reflect.Method;

import imangazaliev.scripto.Scripto;

/**
 * Вызывает JavaScript функции
 */
public class JavaScriptFunction {

    private Scripto scripto;
    private final String proxyId;
    final String jsFunction;

    public JavaScriptFunction(Scripto scripto, String jsVariableName, Method method, Object[] args, String proxyId) {
        this.scripto = scripto;
        this.proxyId = proxyId;
        this.jsFunction = buildJavaScriptFunctionCall(jsVariableName, method, args);
    }

    String getJsFunction() {
        return jsFunction;
    }

    /**
     * Строит строку вызова JS-функции
     *
     * @param method - название функции
     * @param args   - аогументы функции
     * @return JS-код вызова функции с аргументами
     */
    private String buildJavaScriptFunctionCall(String jsVariableName, Method method, Object[] args) {
        //если функция имеет callback убираем его из аргументов
        JavaScriptArguments arguments = new JavaScriptArguments(scripto, args);

        String functionTemplate = "%s(%s);";
        String functionCall = String.format(functionTemplate, getFunctionName(method), arguments.getFormattedArguments());

        //если аннотация есть на методе, то аннотацию на классе мы ее не учитываем
        jsVariableName = (method.isAnnotationPresent(JsVariableName.class)) ? getObjectName(method) : jsVariableName;
        //если аннотация есть на классе или методе, присоединяем имя переменной через точку
        functionCall = (jsVariableName != null) ? String.format("%s.%s", jsVariableName, functionCall) : functionCall;
        return functionCall;
    }

    private String getObjectName(Method method) {
        return method.isAnnotationPresent(JsVariableName.class) ? method.getAnnotation(JsVariableName.class).value() : null;
    }

    private String getFunctionName(Method method) {
        return method.isAnnotationPresent(JsFunctionName.class) ? method.getAnnotation(JsFunctionName.class).value() : method.getName();
    }

    /**
     * Формирует JS-код вызова функции и запускает его
     *
     * @param callCode - код коллбека
     */
    public void callJavaScriptFunction(String callCode) {
        String jsCall = "javascript:"
                + "(function(){"
                + "	 try {"
                + "		var response = " + jsFunction
                +       proxyId + ".onCallbackResponse('" + callCode + "', response);"
                + "	 } catch (err) {"
                +       proxyId + ".onCallbackError('" + callCode + "', err.message);"
                + "	 }"
                + "})();";
        scripto.getWebView().loadUrl(jsCall);
    }

}
