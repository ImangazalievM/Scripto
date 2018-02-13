package imangazaliev.scripto.sample.scripts;

import imangazaliev.scripto.js.JavaScriptFunctionCall;
import imangazaliev.scripto.js.JsFunctionName;
import imangazaliev.scripto.sample.User;

public interface UserInfoScript {

    JavaScriptFunctionCall<Void> loadUserData();

    @JsFunctionName("getUserData")
    JavaScriptFunctionCall<User> getUser();

}
