package imangazaliev.scripto.test;

import imangazaliev.scripto.java.RawResponse;
import imangazaliev.scripto.java.ScriptoFunctionCall;

public interface JsScript {

    ScriptoFunctionCall<String> getName();

    ScriptoFunctionCall<RawResponse> getData();

    ScriptoFunctionCall<CustomTestModel> getCustomModel();

}
