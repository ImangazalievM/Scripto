package imangazaliev.scripto.js;

public class JavaScriptFunctionCall<T> {

    private JavaScriptFunction mJavaScriptFunction;
    private String callCode;

    private JavaScriptCallResponseCallback<T> responseCallback;
    private JavaScriptCallErrorCallback errorCallback;
    private Class<T> responseType;

    private boolean throwOnError;

    public JavaScriptFunctionCall(JavaScriptFunction javaScriptFunction, Class<T> responseType, String callCode) {
        this.mJavaScriptFunction = javaScriptFunction;
        this.responseType = responseType;
        this.callCode = callCode;
        this.throwOnError = false;
    }

    JavaScriptFunction getJavaScriptFunction() {
        return mJavaScriptFunction;
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
     * Выбрасывать исключение при ошибке в JS или нет, если нет callback'а для ошибкм
     */
    public JavaScriptFunctionCall<T> throwOnError(boolean throwOnEror) {
        this.throwOnError = throwOnEror;
        return this;
    }

    boolean isThrowOnError() {
        return throwOnError;
    }

    Class<?> getResponseType() {
        return responseType;
    }

    public void call() {
        mJavaScriptFunction.callJavaScriptFunction(callCode);
    }

}
