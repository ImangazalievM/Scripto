package imangazaliev.scripto.sample.scripts;

import imangazaliev.scripto.java.ScriptoFunctionCall;
import imangazaliev.scripto.sample.User;

public interface UserInfoScript {

    ScriptoFunctionCall<Void> loadUserData();

    ScriptoFunctionCall<User> getUserData();

}
