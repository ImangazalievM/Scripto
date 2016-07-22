package imangazaliev.scripto.java;

public class JavaScriptException extends RuntimeException {

    public JavaScriptException() {
    }

    public JavaScriptException(String detailMessage) {
        super(detailMessage);
    }


    public JavaScriptException(String detailMessage, Throwable throwable) {
        super(detailMessage, throwable);
    }

}
