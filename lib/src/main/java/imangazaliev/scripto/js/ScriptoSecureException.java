package imangazaliev.scripto.js;

import imangazaliev.scripto.ScriptoException;

public class ScriptoSecureException extends ScriptoException {

    public ScriptoSecureException() {
    }

    public ScriptoSecureException(String detailMessage) {
        super(detailMessage);
    }


    public ScriptoSecureException(String detailMessage, Throwable throwable) {
        super(detailMessage, throwable);
    }

}
