package imangazaliev.scripto.java;

import java.lang.reflect.Method;

import imangazaliev.scripto.Scripto;

/**
 * Вызывает JavaScript функции
 */
public class ScriptoFunction {

    private Scripto scripto;
    private final String proxyId;
    private final String jsFunction;

    public ScriptoFunction(Scripto scripto, Method method, Object[] args, String proxyId) {
        this.scripto = scripto;
        this.proxyId = proxyId;
        this.jsFunction = buildJavaScriptFunctionCall(method, args);
    }

    /**
     * Формирует JS-код вызова функции и запускает его
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

    /**
     * Строит строку вызова JS-функции
     * @param method - название функции
     * @param args - аогументы функции
     * @return JS-код вызова функции с аргументами
     */
    private String buildJavaScriptFunctionCall(Method method, Object[] args) {
        //если функция имеет callback убираем его из аргументов
        JavaScriptArguments arguments = new JavaScriptArguments(scripto, args);
        String functionTemplate = "%s(%s);";
        return String.format(functionTemplate, method.getName(), arguments.getArguments());
    }

}
