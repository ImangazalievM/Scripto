package imangazaliev.scripto.js;

import android.webkit.JavascriptInterface;

import com.google.gson.JsonSyntaxException;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.HashMap;

import imangazaliev.scripto.Scripto;
import imangazaliev.scripto.ScriptoException;
import imangazaliev.scripto.utils.ScriptoLogUtils;
import imangazaliev.scripto.utils.ScriptoUtils;
import imangazaliev.scripto.utils.StringUtils;

public class ScriptoProxy implements InvocationHandler {

    private Scripto scripto;
    private String jsVariableName;
    private String proxyId;
    private HashMap<String, JavaScriptFunctionCall> functionCalls;

    public ScriptoProxy(Scripto scripto, Class<?> scriptClass) {
        this.scripto = scripto;
        this.jsVariableName = getJsVariableName(scriptClass);

        functionCalls = new HashMap();
        proxyId = StringUtils.randomString(5);

        //добавляем себя как интерфейс для приема коллбеков от JS
        scripto.getWebView().addJavascriptInterface(this, proxyId);
    }

    private String getJsVariableName(Class<?> scriptClass) {
        return scriptClass.isAnnotationPresent(JsVariableName.class) ? scriptClass.getAnnotation(JsVariableName.class).value() : null;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object... args) throws Throwable {
        //перенаправляем стандартные вызовы методов класса Object к объекту
        if (method.getDeclaringClass() == Object.class) {
            return method.invoke(this, args);
        }

        JavaScriptFunction javaScriptFunction = new JavaScriptFunction(scripto, jsVariableName, method, args, proxyId);
        Class<?> returnType = ScriptoUtils.getCallResponseType(method);
        String callCode = StringUtils.randomNumericString(5);

        JavaScriptFunctionCall javaScriptFunctionCall = new JavaScriptFunctionCall(javaScriptFunction, returnType, callCode);
        functionCalls.put(callCode, javaScriptFunctionCall);

        return javaScriptFunctionCall;
    }

    @JavascriptInterface
    public void onCallbackResponse(final String callbackCode, final String responseString) {
        ScriptoUtils.runOnUi(new Runnable() {
            @Override
            public void run() {
                onCallbackResponseUi(callbackCode, responseString);
            }
        });
    }

    private void onCallbackResponseUi(String callbackCode, String responseString) {
        if (!functionCalls.containsKey(callbackCode)) {
            return;
        }

        JavaScriptFunctionCall functionCall = functionCalls.remove(callbackCode);
        JavaScriptCallResponseCallback callback = functionCall.getResponseCallback();

        if (callback == null) {
            return;
        }

        Class<?> responseType = functionCall.getResponseType();
        if (responseString == null || responseType.isAssignableFrom(Void.class)) {
            //если ответ не получен (null) или функция ничего не должна возвращать(Void), передаем null
            callback.onResponse(null);
            ScriptoLogUtils.logMessage(String.format("Function '%s' call success", functionCall.getJavaScriptFunction().getJsFunction()));
        } else if (responseType.isAssignableFrom(RawResponse.class)) {
            //возвращаем ответ без конвертации
            callback.onResponse(new RawResponse(responseString));
            ScriptoLogUtils.logMessage(String.format("Function '%s' call success", functionCall.getJavaScriptFunction().getJsFunction()));
        } else {
            try {
                Object response = scripto.getJsonToJavaConverter().toObject(responseString, responseType);
                callback.onResponse(response);
                ScriptoLogUtils.logMessage(String.format("Function '%s' call success", functionCall.getJavaScriptFunction().getJsFunction()));
            } catch (JsonSyntaxException e) {
                ScriptoException error =  new ScriptoException("Ошибка при конвертации JSON из JS", e);
                ScriptoLogUtils.logError(error, String.format("Function '%s' call error", functionCall.getJavaScriptFunction().getJsFunction()));
                onError(functionCall, error);
            }
        }
    }

    @JavascriptInterface
    public void onCallbackError(final String callbackCode, final String message) {
        ScriptoUtils.runOnUi(new Runnable() {
            @Override
            public void run() {
                onCallbackErrorUi(callbackCode, message);
            }
        });
    }

    private void onCallbackErrorUi(String callbackCode, String message) {
        if (!functionCalls.containsKey(callbackCode)) {
            return;
        }

        JavaScriptFunctionCall functionCall = functionCalls.remove(callbackCode);
        ScriptoLogUtils.logError(String.format("Function '%s' call error. Message: %s", functionCall.getJavaScriptFunction().getJsFunction(), message));
        onError(functionCall, new JavaScriptException(message));
    }

    private void onError(JavaScriptFunctionCall functionCall, ScriptoException error) {
        JavaScriptCallErrorCallback callback = functionCall.getErrorCallback();
        if (callback == null && functionCall.isThrowOnError()) {
            throw error;
        } else if (callback != null) {
            callback.onError(error);
        }
    }

}
