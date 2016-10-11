package imangazaliev.scripto.sample;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.Toast;

import imangazaliev.scripto.Scripto;
import imangazaliev.scripto.ScriptoException;
import imangazaliev.scripto.ScriptoPrepareListener;
import imangazaliev.scripto.java.JavaScriptException;
import imangazaliev.scripto.java.ScriptoErrorCallback;
import imangazaliev.scripto.java.ScriptoResponseCallback;
import imangazaliev.scripto.js.ScriptoInterfaceConfig;
import imangazaliev.scripto.js.ScriptoSecureException;
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
        scripto.addInterface("Android", new AndroidInterface(this), new ScriptoInterfaceConfig().enableAnnotationProtection(true));
        scripto.addInterface("Preferences", new PreferencesInterface(this), new ScriptoInterfaceConfig());
        scripto.addJsScriptFromAssets("scripto/scripto.js");
        userInfoScript = scripto.create(UserInfoScript.class);

        scripto.onError(new Scripto.ErrorHandler() {
            @Override
            public void onError(ScriptoSecureException error) {
                Toast.makeText(MainActivity.this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
       scripto.onPrepared(new ScriptoPrepareListener() {
           @Override
           public void onScriptoPrepared() {
               userInfoScript.loadUserData();
           }
       });


        String html = AssetsReader.readFileAsText(this, "test.html");
        webView.loadDataWithBaseURL("file:///android_asset/", html, "text/html", "utf-8", null);
    }

    public void getUserData(View view) {
        userInfoScript.getUserData()
                .onResponse(new ScriptoResponseCallback<User>() {
                    @Override
                    public void onResponse(User user) {
                        Toast.makeText(MainActivity.this, user.getUserInfo(), Toast.LENGTH_LONG).show();
                    }
                })
                .onError(new ScriptoErrorCallback() {
                    @Override
                    public void onError(ScriptoException error) {
                        Toast.makeText(MainActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                })
                .call();
    }

}
