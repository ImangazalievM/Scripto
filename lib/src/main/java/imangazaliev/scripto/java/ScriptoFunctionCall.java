package imangazaliev.scripto.java;

public class ScriptoFunctionCall<T> {

    ScriptoFunction scriptoFunction;
    String callCode;

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

    public ScriptoFunctionCall<T> onResponse(ScriptoResponseCallback<T> responseCallback) {
        this.responseCallback = responseCallback;
        return this;
    }

    public ScriptoResponseCallback<T> getResponseCallback() {
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

    public boolean isThrowOnError() {
        return throwOnError;
    }


    protected Class<?> getResponseType() {
        return responseType;
    }


    public void call() {
        scriptoFunction.callJavaScriptFunction(callCode);
    }

}
