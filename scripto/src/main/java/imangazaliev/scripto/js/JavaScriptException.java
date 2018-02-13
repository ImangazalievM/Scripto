package imangazaliev.scripto.js;

import imangazaliev.scripto.ScriptoException;

public class JavaScriptException extends ScriptoException {

    public JavaScriptException() {
    }

    public JavaScriptException(String detailMessage) {
        super(detailMessage);
    }


    public JavaScriptException(String detailMessage, Throwable throwable) {
        super(detailMessage, throwable);
    }

}
