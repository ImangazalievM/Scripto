package imangazaliev.scripto.js;

import java.lang.reflect.Method;

import imangazaliev.scripto.Scripto;

/**
 * Calls JavaScript functions from Java
 */
class JavaScriptFunction {

    private Scripto scripto;
    private final String proxyId;
    final String jsFunction;

    JavaScriptFunction(Scripto scripto, String jsVariableName, Method method, Object[] args, String proxyId) {
        this.scripto = scripto;
        this.proxyId = proxyId;
        this.jsFunction = buildJavaScriptFunctionCall(jsVariableName, method, args);
    }

    String getJsFunction() {
        return jsFunction;
    }

    /**
     * Constructs a string to JS-function
     *
     * @param method - JS-function name
     * @param args   - JS-function arguments
     * @return JS-code of function call with arguments
     */
    private String buildJavaScriptFunctionCall(String jsVariableName, Method method, Object[] args) {
        //если функция имеет callback убираем его из аргументов
        JavaScriptArguments arguments = new JavaScriptArguments(scripto, args);

        String functionTemplate = "%s(%s);";
        String functionCall = String.format(functionTemplate, getFunctionName(method), arguments.getFormattedArguments());

        //если аннотация есть на методе, то аннотацию на классе мы ее не учитываем
        jsVariableName = (method.isAnnotationPresent(JsVariableName.class)) ? getVariableName(method) : jsVariableName;
        //если аннотация есть на классе или методе, присоединяем имя переменной через точку
        functionCall = (jsVariableName != null) ? String.format("%s.%s", jsVariableName, functionCall) : functionCall;
        return functionCall;
    }

    private String getVariableName(Method method) {
        return method.getAnnotation(JsVariableName.class).value();
    }

    private String getFunctionName(Method method) {
        return method.isAnnotationPresent(JsFunctionName.class) ? method.getAnnotation(JsFunctionName.class).value() : method.getName();
    }

    /**
     * Forms JS-code to call a function and run it
     *
     * @param callId - unique identifier of call
     */
    public void callJavaScriptFunction(String callId) {
        String jsCall = "javascript:"
                + "(function(){"
                + "	 try {"
                + "		var response = " + jsFunction
                +       proxyId + ".onCallbackResponse('" + callId + "', response);"
                + "	 } catch (err) {"
                +       proxyId + ".onCallbackError('" + callId + "', err.message);"
                + "	 }"
                + "})();";
        scripto.getWebView().loadUrl(jsCall);
    }

}
