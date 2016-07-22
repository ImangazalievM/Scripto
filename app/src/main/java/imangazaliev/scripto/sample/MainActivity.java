package imangazaliev.scripto.sample;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.webkit.WebView;
import android.widget.Toast;

import imangazaliev.scripto.Scripto;
import imangazaliev.scripto.sample.interfaces.AndroidInterface;
import imangazaliev.scripto.sample.interfaces.PreferencesInterface;
import imangazaliev.scripto.sample.scripts.UserInfoScript;
import imangazaliev.scripto.sample.utils.AssetsReader;

public class MainActivity extends AppCompatActivity {

    private Scripto scripto;
    private UserInfoScript userInfoScript;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        WebView webView = (WebView) findViewById(R.id.web_view);

        scripto = new Scripto.Builder(webView).build();
        scripto.addInterface("Android", new AndroidInterface(this));
        scripto.addInterface("Preferences", new PreferencesInterface(this));
        userInfoScript = scripto.create(UserInfoScript.class);

        scripto.onPrepared(this::onScriptoPrepared);


        String html = AssetsReader.readFileAsText(this, "test.html");
        webView.loadDataWithBaseURL("file:///android_asset/", html, "text/html", "utf-8", null);
    }

    private void onScriptoPrepared() {
        userInfoScript.loadUserData();
    }

    public void getUserData(View view) {
        userInfoScript.getUserData()
                .onResponse(user -> Toast.makeText(MainActivity.this, user.getUserInfo(), Toast.LENGTH_LONG).show())
                .onError(error -> Toast.makeText(MainActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show())
                .call();
    }

}
