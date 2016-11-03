package imangazaliev.scripto.java;

public class ScriptoFunctionCall<T> {

    private ScriptoFunction scriptoFunction;
    private String callCode;

    private ScriptoResponseCallback<T> responseCallback;
    private ScriptoErrorCallback errorCallback;
    private Class<T> responseType;

    private boolean throwOnError;

    public ScriptoFunctionCall(ScriptoFunction scriptoFunction, Class<T> responseType, String callCode) {
        this.scriptoFunction = scriptoFunction;
        this.responseType = responseType;
        this.callCode = callCode;

        throwOnError = false;
    }

    ScriptoFunction getScriptoFunction() {
        return scriptoFunction;
    }

    public ScriptoFunctionCall<T> onResponse(ScriptoResponseCallback<T> responseCallback) {
        this.responseCallback = responseCallback;
        return this;
    }

    ScriptoResponseCallback<T> getResponseCallback() {
        return responseCallback;
    }

    public ScriptoFunctionCall<T> onError(ScriptoErrorCallback errorCallback) {
        this.errorCallback = errorCallback;
        return this;
    }

    public ScriptoErrorCallback getErrorCallback() {
        return errorCallback;
    }

    /**
     * Выбрасывать исключение при ошибке в JS или нет, если нет callback'а для ошибкм
     */
    public ScriptoFunctionCall<T> throwOnError(boolean throwOnEror) {
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
        scriptoFunction.callJavaScriptFunction(callCode);
    }

}
