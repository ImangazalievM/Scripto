package imangazaliev.scripto.test;

import imangazaliev.scripto.js.ScriptoSecure;

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

    @ScriptoSecure
    public void resetAll() {

    }

    public void killAll() {

    }

}