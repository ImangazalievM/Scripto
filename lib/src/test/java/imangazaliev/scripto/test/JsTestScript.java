package imangazaliev.scripto.test;

import imangazaliev.scripto.java.JsVariableName;
import imangazaliev.scripto.java.RawResponse;
import imangazaliev.scripto.java.ScriptoFunctionCall;

public interface JsTestScript {

    ScriptoFunctionCall<String> getName();

    ScriptoFunctionCall<RawResponse> getData();

    ScriptoFunctionCall<CustomTestModel> getCustomModel();

    ScriptoFunctionCall<Void> initProfile();

    @JsVariableName("settings")
    ScriptoFunctionCall<Void> setFontSize();

}
