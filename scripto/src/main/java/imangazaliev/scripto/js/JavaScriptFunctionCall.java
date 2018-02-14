package imangazaliev.scripto.js;

public class JavaScriptFunctionCall<T> {

    private JavaScriptFunction javaScriptFunction;
    private String callId;

    private JavaScriptCallResponseCallback<T> responseCallback;
    private JavaScriptCallErrorCallback errorCallback;
    private Class<T> responseType;

    private boolean throwOnError;

    JavaScriptFunctionCall(JavaScriptFunction javaScriptFunction, Class<T> responseType, String callId) {
        this.javaScriptFunction = javaScriptFunction;
        this.responseType = responseType;
        this.callId = callId;
        this.throwOnError = false;
    }

    JavaScriptFunction getJavaScriptFunction() {
        return javaScriptFunction;
    }

    public JavaScriptFunctionCall<T> onResponse(JavaScriptCallResponseCallback<T> responseCallback) {
        this.responseCallback = responseCallback;
        return this;
    }

    JavaScriptCallResponseCallback<T> getResponseCallback() {
        return responseCallback;
    }

    public JavaScriptFunctionCall<T> onError(JavaScriptCallErrorCallback errorCallback) {
        this.errorCallback = errorCallback;
        return this;
    }

    JavaScriptCallErrorCallback getErrorCallback() {
        return errorCallback;
    }

    /**
     * To throw an exception at an error in JS or not, if there is no callback for errors
     */
    public JavaScriptFunctionCall<T> throwOnError(boolean throwOnError) {
        this.throwOnError = throwOnError;
        return this;
    }

    boolean isThrowOnError() {
        return throwOnError;
    }

    Class<?> getResponseType() {
        return responseType;
    }

    public void call() {
        javaScriptFunction.callJavaScriptFunction(callId);
    }

}
