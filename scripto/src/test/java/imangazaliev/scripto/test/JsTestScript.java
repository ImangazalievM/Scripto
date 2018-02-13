package imangazaliev.scripto.test;

import imangazaliev.scripto.js.JavaScriptFunctionCall;
import imangazaliev.scripto.js.JsFunctionName;
import imangazaliev.scripto.js.JsVariableName;
import imangazaliev.scripto.js.RawResponse;

public interface JsTestScript {

    JavaScriptFunctionCall<String> getName();

    JavaScriptFunctionCall<RawResponse> getData();

    JavaScriptFunctionCall<CustomTestModel> getCustomModel();

    JavaScriptFunctionCall<Void> initProfile();

    @JsVariableName("settings")
    JavaScriptFunctionCall<Void> setFontSize();

    @JsFunctionName("getUserLogin")
    JavaScriptFunctionCall<String> getLogin();

}
