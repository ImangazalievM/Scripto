package imangazaliev.scripto.test;

import imangazaliev.scripto.java.JavaScriptSecure;

public class TestJsInterface {

    public void showAlert() {

    }

    public void showMessage(String message) {

    }

    public String getName() {
        return "My name";
    }

    public String getNull() {
        return null;
    }

    @JavaScriptSecure
    public void resetAll() {

    }

    public void killAll() {

    }

}