package imangazaliev.scripto;

import android.util.Base64;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;

import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Collections;

import imangazaliev.scripto.converter.JavaConverter;
import imangazaliev.scripto.converter.JavaScriptConverter;
import imangazaliev.scripto.java.ScriptoProxy;
import imangazaliev.scripto.js.ScriptoInterface;
import imangazaliev.scripto.js.ScriptoInterfaceConfig;
import imangazaliev.scripto.js.ScriptoSecureException;
import imangazaliev.scripto.utils.ScriptoAssetsJavaScriptReader;
import imangazaliev.scripto.utils.ScriptoUtils;

/**
 * Создает прокси для JS-скриптов. Добавляет JavaScript интерфейсы
 */
public class Scripto {

    public interface ErrorHandler {
        void onError(ScriptoSecureException error);
    }

    private WebView webView;
    private JavaScriptConverter javaScriptConverter;
    private JavaConverter javaConverter;

    private ErrorHandler errorHandler;
    private ScriptoPrepareListener prepareListener;

    private ScriptoAssetsJavaScriptReader scriptoAssetsJavaScriptReader;
    private ArrayList<String> jsScripts;

    private Scripto(Builder builder) {
        this.webView = builder.webView;
        this.javaScriptConverter = builder.javaScriptConverter;
        this.javaConverter = builder.javaConverter;

        jsScripts = new ArrayList<>();
        scriptoAssetsJavaScriptReader = new ScriptoAssetsJavaScriptReader(webView.getContext());

        initWebView(builder);
    }

    private void initWebView(Builder builder) {
        ScriptoWebViewClient scriptoWebViewClient = builder.scriptoWebViewClient;
        webView.setWebViewClient(builder.scriptoWebViewClient);
        scriptoWebViewClient.setOnPageLoadedListener(new ScriptoWebViewClient.OnPageLoadedListener() {
            @Override
            public void onPageLoaded() {
                addJsScripts();
            }
        });

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
        }, "ScriptoInterface");
    }

    private void addJsScripts() {
        StringBuilder fullJsCodeBuilder = new StringBuilder();
        for (int i = 0; i < jsScripts.size(); i++) {
            fullJsCodeBuilder.append(jsScripts.remove(i));
        }

        //оповещаем java-библиотеку, о готовности к работе
        fullJsCodeBuilder.append("ScriptoInterface.onScriptoPrepared();");

        String encodedJsCode = Base64.encodeToString(fullJsCodeBuilder.toString().getBytes(), Base64.NO_WRAP);
        webView.loadUrl("javascript:(function() {" +
                "   var head = document.getElementsByTagName('head').item(0);" +
                "   var script = document.createElement('script');" +
                "   script.type = 'text/javascript';" +
                // Tell the browser to BASE64-decode the string into your script !!!
                "   script.innerHTML = window.atob('" + encodedJsCode + "');" +
                "   head.insertBefore(script, head.firstChild);" +
                "})()");
    }

    public void addJsScriptFromAssets(String fileName) {
        addJsScript(scriptoAssetsJavaScriptReader.read(fileName));
    }

    public void addJsScript(String jsCode) {
        jsScripts.add(jsCode);
    }

    /**
     * Создает прокси из интерфейс для вызова JS-функций
     */
    public <T> T create(final Class<T> script) {
        ScriptoUtils.checkNotNull(script, "Script class can not be null");

        //если объект не является интерфейсом, выбрасываем исключение
        ScriptoUtils.validateScriptInterface(script);
        return (T) Proxy.newProxyInstance(script.getClassLoader(), new Class<?>[]{script}, new ScriptoProxy(this));
    }

    public WebView getWebView() {
        return webView;
    }

    public JavaScriptConverter getJavaScriptConverter() {
        return javaScriptConverter;
    }

    public JavaConverter getJavaConverter() {
        return javaConverter;
    }

    public void addInterface(String tag, Object obj) {
        webView.addJavascriptInterface(new ScriptoInterface(this, tag, obj), tag);
    }

    public void addInterface(String tag, Object obj, ScriptoInterfaceConfig config) {
        webView.addJavascriptInterface(new ScriptoInterface(this, tag,  obj, config), tag);
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
        private JavaScriptConverter javaScriptConverter;
        private JavaConverter javaConverter;

        public Builder(WebView webView) {
            this.webView = webView;

            this.scriptoWebViewClient = new ScriptoWebViewClient();
            this.javaScriptConverter = new JavaScriptConverter();
            this.javaConverter = new JavaConverter();
        }

        public Builder setWebViewClient(ScriptoWebViewClient scriptoWebViewClient) {
            ScriptoUtils.checkNotNull(scriptoWebViewClient, "ScriptoWebViewClient can not be null");
            this.scriptoWebViewClient = scriptoWebViewClient;
            return this;
        }

        public Builder setJavaScriptConverter(JavaScriptConverter javaScriptConverter) {
            ScriptoUtils.checkNotNull(javaScriptConverter, "Converter can not be null");
            this.javaScriptConverter = javaScriptConverter;
            return this;
        }

        public Builder setJavaConverter(JavaConverter javaConverter) {
            ScriptoUtils.checkNotNull(javaConverter, "Converter can not be null");
            this.javaConverter = javaConverter;
            return this;
        }

        public Scripto build() {
            return new Scripto(this);
        }

    }


}