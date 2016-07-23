package imangazaliev.scripto;

import android.webkit.JavascriptInterface;
import android.webkit.WebView;

import java.lang.reflect.Proxy;

import imangazaliev.scripto.converter.JavaScriptConverter;
import imangazaliev.scripto.converter.JavaConverter;
import imangazaliev.scripto.java.ScriptProxy;
import imangazaliev.scripto.js.ScriptoInterface;
import imangazaliev.scripto.js.ScriptoInterfaceConfig;
import imangazaliev.scripto.utils.ScriptoUtils;

/**
 * Создает прокси для JS-скриптов. Добавляет JavaScript интерфейсы
 */
public class Scripto {

	private WebView webView;
	private JavaScriptConverter javaScriptConverter;
	private JavaConverter javaConverter;

	private ScriptoPrepareListener prepareListener;

	private Scripto(Builder builder) {
		this.webView = builder.webView;
		this.javaScriptConverter = builder.javaScriptConverter;
		this.javaConverter = builder.javaConverter;

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

	/**
	 * Создает прокси из интерфейс для вызова JS-функций
     */
	public <T> T create(final Class<T> script) {
		ScriptoUtils.checkNotNull(script, "Script class can not be null");

		//если объект не является интерфейсом, выбрасываем исключение
		ScriptoUtils.validateScriptInterface(script);
		return (T) Proxy.newProxyInstance(script.getClassLoader(),  new Class<?>[] {script}, new ScriptProxy(this));
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
		webView.addJavascriptInterface(new ScriptoInterface(this, obj), tag);
	}

	public void addInterface(String tag, Object obj, ScriptoInterfaceConfig config) {
		webView.addJavascriptInterface(new ScriptoInterface(this, obj, config), tag);
	}

	public void removeInterface(String tag) {
		webView.removeJavascriptInterface(tag);
	}

	public void onPrepared(ScriptoPrepareListener prepareListener) {
		this.prepareListener = prepareListener;
	}

	public static class Builder {
		
		private WebView webView;
		private JavaScriptConverter javaScriptConverter;
		private JavaConverter javaConverter;
		
		public Builder(WebView webView) {
			this.webView = webView;

			javaScriptConverter = new JavaScriptConverter();
			javaConverter = new JavaConverter();
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