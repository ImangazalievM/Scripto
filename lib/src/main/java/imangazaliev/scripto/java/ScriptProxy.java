package imangazaliev.scripto.java;

import android.webkit.JavascriptInterface;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.HashMap;

import imangazaliev.scripto.Scripto;
import imangazaliev.scripto.ScriptoException;
import imangazaliev.scripto.utils.ScriptoUtils;
import imangazaliev.scripto.utils.StringUtils;

public class ScriptProxy implements InvocationHandler {

    private Scripto scripto;
    private String proxyId;
    private HashMap<String, ScriptoFunctionCall> functionCalls;

    public ScriptProxy(Scripto scripto) {
        this.scripto = scripto;

        functionCalls = new HashMap();
        proxyId = StringUtils.randomString(5);
        scripto.getWebView().addJavascriptInterface(this, proxyId);
    }

    @Override
    public Object invoke(Object proxy, Method method, Object... args) throws Throwable {
        //перенаправляем стандартные вызовы методов класса Object к объекту
        if (method.getDeclaringClass() == Object.class) {
            return method.invoke(this, args);
        }

        ScriptoFunction scriptoFunction = new ScriptoFunction(scripto, method, args, proxyId);

        Class<?> returnType = ScriptoUtils.getCallResponseType(method);
        String callCode = StringUtils.randomStringNumeric(5);
        ScriptoFunctionCall scriptoFunctionCall = new ScriptoFunctionCall(scriptoFunction, returnType, callCode);
        functionCalls.put(callCode, scriptoFunctionCall);

        return scriptoFunctionCall;
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
        ScriptoFunctionCall functionCall = functionCalls.remove(callbackCode);
        ScriptoResponseCallback callback = functionCall.getResponseCallback();

        if (callback == null) {
            return;
        }

        Class<?> responseType = functionCall.getResponseType();
        if (responseString == null || responseType.isAssignableFrom(Void.class)) {
            //если ответ не получен (null) или функция ничего не должна возвращать(Void), передаем null
            callback.onResponse(null);
        } else if (responseType.isAssignableFrom(RawResponse.class)) {
            //возвращаем ответ без конвертации
            callback.onResponse(new RawResponse(responseString));
        } else {
            Object response = scripto.getJavaConverter().toObject(responseString, responseType);
            callback.onResponse(response);
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
        ScriptoFunctionCall functionCall = functionCalls.remove(callbackCode);
        ScriptoErrorCallback callback = functionCall.getErrorCallback();

        if (callback == null && functionCall.isThrowOnError()) {
            throw new JavaScriptException(message);
        } else if (callback != null) {
            callback.onError(new ScriptoException(message));
        }
    }

}
