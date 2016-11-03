package imangazaliev.scripto.sample.scripts;

import imangazaliev.scripto.java.FunctionName;
import imangazaliev.scripto.java.JsVariableName;
import imangazaliev.scripto.java.ScriptoFunctionCall;
import imangazaliev.scripto.sample.User;

@JsVariableName("")
public interface UserInfoScript {

    ScriptoFunctionCall<Void> loadUserData();

    @FunctionName("getUserData")
    ScriptoFunctionCall<User> getUser();

}
