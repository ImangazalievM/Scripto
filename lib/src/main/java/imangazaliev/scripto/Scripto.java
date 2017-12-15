package imangazaliev.scripto;

import android.util.Log;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;

import java.lang.reflect.Proxy;
import java.util.ArrayList;

import imangazaliev.scripto.converter.JsonToJavaConverter;
import imangazaliev.scripto.converter.JavaToJsonConverter;
import imangazaliev.scripto.js.ScriptoProxy;
import imangazaliev.scripto.java.JavaInterface;
import imangazaliev.scripto.java.JavaInterfaceConfig;
import imangazaliev.scripto.utils.ScriptoAssetsJavaScriptReader;
import imangazaliev.scripto.utils.ScriptoUtils;

/**
 * Создает прокси для JS-скриптов. Добавляет JavaScript интерфейсы
 */
public class Scripto {

    private static final String ASSETS_FOLDER_PATH = "file:///android_asset/";

    public interface ErrorHandler {
        void onError(ScriptoException error);
    }

    private WebView webView;
    private JavaToJsonConverter javaToJsonConverter;
    private JsonToJavaConverter jsonToJavaConverter;

    private ErrorHandler errorHandler;
    private ScriptoPrepareListener prepareListener;

    private ScriptoAssetsJavaScriptReader scriptoAssetsJavaScriptReader;
    private ArrayList<String> jsFiles;

    private Scripto(Builder builder) {
        this.webView = builder.webView;
        this.javaToJsonConverter = builder.javaToJsonConverter;
        this.jsonToJavaConverter = builder.jsonToJavaConverter;

        jsFiles = new ArrayList<>();
        scriptoAssetsJavaScriptReader = new ScriptoAssetsJavaScriptReader(webView.getContext());

        initWebView(builder);
    }

    private void initWebView(Builder builder) {
        ScriptoWebViewClient scriptoWebViewClient = builder.scriptoWebViewClient;
        scriptoWebViewClient.setOnPageLoadedListener(new ScriptoWebViewClient.OnPageLoadedListener() {
            @Override
            public void onPageLoaded() {
                addJsScripts();
            }
        });
        webView.setWebViewClient(scriptoWebViewClient);

        webView.getSettings().setJavaScriptEnabled(true);
        //ждем команду от JS о готовности к работе
        webView.addJavascriptInterface(new Object() {
            @JavascriptInterface
            public void onScriptoPrepared() {
                ScriptoUtils.runOnUi(new Runnable() {
                    @Override
                    public void run() {
                        if (prepareListener != null) {
                            prepareListener.onScriptoPrepared();
                        }
                    }
                });
            }
        }, "ScriptoPreparedListener");
    }

    private void addJsScripts() {
        StringBuilder jsScriptsListBuilder = new StringBuilder();

        int scriptsCount = jsFiles.size();
        //список с JS-файлами
        for (int i = 0; i < scriptsCount; i++) {
            jsScriptsListBuilder.append("\"").append(jsFiles.get(i)).append("\"");
            if (i < scriptsCount - 1) {
                jsScriptsListBuilder.append(", ");
            }
        }

        webView.loadUrl(
                "javascript:(function() {" +
                "   var jsFiles = [" + jsScriptsListBuilder.toString() + "];" +
                "    " +
                "   jsFiles.forEach(function(jsFile, i, jsFiles) {\n" +
                "       var jsScript = document.createElement(\"script\");" +
                "       jsScript.setAttribute(\"src\", jsFile);\n" +
                "       document.head.appendChild(jsScript);" +
                "   });" +
                "   ScriptoPreparedListener.onScriptoPrepared();" + //оповещаем java-библиотеку, о готовности к работе
                "})();");
    }

    public void addJsFile(String filePath) {
        jsFiles.add(filePath);
    }

    public void addJsFileFromAssets(String filePath) {
        jsFiles.add(ASSETS_FOLDER_PATH + filePath);
    }

    /**
     * Создает прокси из интерфейса для вызова JS-функций
     */
    public <T> T create(final Class<T> script) {
        ScriptoUtils.checkNotNull(script, "Script class can't be null");

        //если объект не является интерфейсом, выбрасываем исключение
        ScriptoUtils.validateScriptInterface(script);
        return (T) Proxy.newProxyInstance(script.getClassLoader(), new Class<?>[]{script}, new ScriptoProxy(this, script));
    }

    public WebView getWebView() {
        return webView;
    }

    public JavaToJsonConverter getJavaToJsonConverter() {
        return javaToJsonConverter;
    }

    public JsonToJavaConverter getJsonToJavaConverter() {
        return jsonToJavaConverter;
    }

    public void addInterface(String tag, Object jsInterface) {
        addInterface(tag, jsInterface, new JavaInterfaceConfig());
    }

    public void addInterface(String tag, Object jsInterface, JavaInterfaceConfig config) {
        if (tag == null) {
            throw new NullPointerException("Tag can't be null");
        }

        if (jsInterface == null) {
            throw new NullPointerException("JavaScript interface object can't be null");
        }

        if (config == null) {
            throw new NullPointerException("Config object can't be null");
        }

        webView.addJavascriptInterface(new JavaInterface(this, tag, jsInterface, config), tag);
    }

    public void removeInterface(String tag) {
        webView.removeJavascriptInterface(tag);
    }

    public void onError(ErrorHandler errorHandler) {
        this.errorHandler = errorHandler;
    }

    public ErrorHandler getErrorHandler() {
        return errorHandler;
    }

    public void onPrepared(ScriptoPrepareListener prepareListener) {
        this.prepareListener = prepareListener;
    }

    public static class Builder {

        private WebView webView;
        private ScriptoWebViewClient scriptoWebViewClient;
        private JavaToJsonConverter javaToJsonConverter;
        private JsonToJavaConverter jsonToJavaConverter;

        public Builder(WebView webView) {
            this.webView = webView;

            this.scriptoWebViewClient = new ScriptoWebViewClient();
            this.javaToJsonConverter = new JavaToJsonConverter();
            this.jsonToJavaConverter = new JsonToJavaConverter();
        }

        public Builder setWebViewClient(ScriptoWebViewClient scriptoWebViewClient) {
            ScriptoUtils.checkNotNull(scriptoWebViewClient, "ScriptoWebViewClient can not be null");
            this.scriptoWebViewClient = scriptoWebViewClient;
            return this;
        }

        public Builder setJavaToJsonConverter(JavaToJsonConverter javaToJsonConverter) {
            ScriptoUtils.checkNotNull(javaToJsonConverter, "Converter can not be null");
            this.javaToJsonConverter = javaToJsonConverter;
            return this;
        }

        public Builder setJsonToJavaConverter(JsonToJavaConverter jsonToJavaConverter) {
            ScriptoUtils.checkNotNull(jsonToJavaConverter, "Converter can not be null");
            this.jsonToJavaConverter = jsonToJavaConverter;
            return this;
        }

        public Scripto build() {
            return new Scripto(this);
        }

    }


}