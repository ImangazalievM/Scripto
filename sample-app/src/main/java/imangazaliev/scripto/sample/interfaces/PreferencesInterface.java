package imangazaliev.scripto.sample.interfaces;

import android.content.Context;
import android.content.SharedPreferences;
import android.widget.Toast;

import imangazaliev.scripto.sample.User;

public class PreferencesInterface {

    private Context context;
    private SharedPreferences prefs;

    public PreferencesInterface(Context context)  {
        this.context = context;
        this.prefs = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
    }

    public void saveUserData(User user) {
        prefs.edit().putString("user_name", user.getName()).apply();
        prefs.edit().putString("user_surname", user.getSurname()).apply();
        prefs.edit().putInt("user_age", user.getAge()).apply();
        prefs.edit().putFloat("user_height", user.getHeight()).apply();
        prefs.edit().putBoolean("user_married", user.isMarried()).apply();

        Toast.makeText(context, user.getUserInfo(), Toast.LENGTH_SHORT).show();
    }

    public User getUserData() {
        String userName = prefs.getString("user_name", "");
        String userSurname = prefs.getString("user_surname", "");
        int userAge = prefs.getInt("user_age", 0);
        float userHeight = prefs.getFloat("user_height", 0.0f);
        boolean userMarried = prefs.getBoolean("user_married", false);

        return new User (userName, userSurname, userAge, userHeight, userMarried);
    }

}
