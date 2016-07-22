package imangazaliev.scripto.java;

public class ScriptoFunctionCall<T> {

    private ScriptoFunction scriptoFunction;
    private String callCode;

    private ScriptoResponseCallback<T> responseCallback;
    private ScriptoErrorCallback errorCallback;
    private Class<?> responseType;

    private boolean throwOnEror;

    public ScriptoFunctionCall(ScriptoFunction scriptoFunction, Class<?> responseType, String callCode) {
        this.scriptoFunction = scriptoFunction;
        this.responseType = responseType;
        this.callCode = callCode;

        throwOnEror = false;
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
        this.throwOnEror = throwOnEror;
        return this;
    }

    public boolean isThrowOnError() {
        return throwOnEror;
    }


    protected Class<?> getResponseType() {
        return responseType;
    }


    public void call() {
        scriptoFunction.callJavaScriptFunction(callCode);
    }

}
