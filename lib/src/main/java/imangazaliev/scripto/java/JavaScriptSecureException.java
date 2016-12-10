package imangazaliev.scripto.java;

import imangazaliev.scripto.ScriptoException;

public class JavaScriptSecureException extends ScriptoException {

    public JavaScriptSecureException() {
    }

    public JavaScriptSecureException(String detailMessage) {
        super(detailMessage);
    }


    public JavaScriptSecureException(String detailMessage, Throwable throwable) {
        super(detailMessage, throwable);
    }

}
