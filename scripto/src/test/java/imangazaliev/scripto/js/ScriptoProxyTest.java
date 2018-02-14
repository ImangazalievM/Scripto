package imangazaliev.scripto.js;

import android.webkit.WebView;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.reflect.Whitebox;

import java.lang.reflect.Method;

import imangazaliev.scripto.Scripto;
import imangazaliev.scripto.converter.JsonToJavaConverter;
import imangazaliev.scripto.test.BaseTestPowerMock;
import imangazaliev.scripto.test.CustomTestModel;
import imangazaliev.scripto.test.JsTestScript;
import imangazaliev.scripto.utils.ScriptoUtils;
import imangazaliev.scripto.utils.StringUtils;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.verifyNew;


@PrepareForTest({StringUtils.class, ScriptoUtils.class, ScriptoProxy.class,  JavaScriptFunction.class})
public class ScriptoProxyTest extends BaseTestPowerMock {

    private final String proxyId = "adrgb";
    private final String callId = "26486";
    private final Object[] args = new Object[] {"My text", 55, true, null};
    private final String jsResponse = "My js response";
    private final String convertedJsResponse = "My  converted response";
    private final RawResponse rawResponse = new RawResponse(jsResponse);
    private final String jsonResponse = "My json";
    private final CustomTestModel customTestModel = new CustomTestModel("name", "surname");
    private final String jsErrorMessage = "My  error message";
    private final JavaScriptException jsException = new JavaScriptException(jsErrorMessage);

    @Mock
    Scripto scripto;
    @Mock
    WebView webview;
    @Mock
    JavaScriptFunction mJavaScriptFunction;
    @Mock
    JsonToJavaConverter mJsonToJavaConverter;

    Method methodGetName;
    Method methodGetData;
    Method methodCustomModel;


    @Before
    public void onSetup() throws Exception {
        MockitoAnnotations.initMocks(this);
        PowerMockito.mockStatic(ScriptoUtils.class);
        PowerMockito.mockStatic(StringUtils.class);

        initMocks();
        initPowerMockito();
    }

    private void initMocks() throws NoSuchMethodException {
        methodGetName = JsTestScript.class.getMethod("getName");
        methodGetData = JsTestScript.class.getMethod("getData");
        methodCustomModel = JsTestScript.class.getMethod("getCustomModel");

        when(scripto.getWebView()).thenReturn(webview);
        when(scripto.getJsonToJavaConverter()).thenReturn(mJsonToJavaConverter);
        when(mJsonToJavaConverter.toObject(jsResponse, String.class)).thenReturn(convertedJsResponse);
    }

    private void initPowerMockito() throws Exception {
        PowerMockito.when(StringUtils.randomString(5)).thenReturn(proxyId);
        PowerMockito.when(StringUtils.randomNumericString(5)).thenReturn(callId);
        PowerMockito.when(ScriptoUtils.getCallResponseType(methodGetName)).thenAnswer(new Answer<Class<String>>() {
            @Override
            public Class<String> answer(InvocationOnMock invocation) throws Throwable {
                return String.class;
            }
        });
        PowerMockito.when(ScriptoUtils.getCallResponseType(methodGetData)).thenAnswer(new Answer<Class<RawResponse>>() {
            @Override
            public Class<RawResponse> answer(InvocationOnMock invocation) throws Throwable {
                return RawResponse.class;
            }
        });
        PowerMockito.when(ScriptoUtils.getCallResponseType(methodGetData)).thenAnswer(new Answer<Class<RawResponse>>() {
            @Override
            public Class<RawResponse> answer(InvocationOnMock invocation) throws Throwable {
                return RawResponse.class;
            }
        });
        PowerMockito.when(ScriptoUtils.getCallResponseType(methodCustomModel)).thenAnswer(new Answer<Class<CustomTestModel>>() {
            @Override
            public Class<CustomTestModel> answer(InvocationOnMock invocation) throws Throwable {
                return CustomTestModel.class;
            }
        });

        PowerMockito.whenNew(JavaScriptFunction.class).withAnyArguments().thenReturn(mJavaScriptFunction);
    }

    @Test
    public void testMethodCall() throws Throwable {
        ScriptoProxy scriptoProxy = new ScriptoProxy(scripto, JsTestScript.class);
        JavaScriptFunctionCall<String> call = (JavaScriptFunctionCall<String>) scriptoProxy.invoke(null, methodGetName, args);

        verify(webview).addJavascriptInterface(scriptoProxy, proxyId);
        verify(mJavaScriptFunction, never()).callJavaScriptFunction(callId);

        call.call();
        verify(mJavaScriptFunction).callJavaScriptFunction(callId);
    }

    @Test
    public void testMethodCallWithResponse() throws Throwable {
        JavaScriptCallResponseCallback<String> callback = mock(JavaScriptCallResponseCallback.class);
        ScriptoProxy scriptoProxy = new ScriptoProxy(scripto, JsTestScript.class);

        JavaScriptFunctionCall<String> call = (JavaScriptFunctionCall<String>) scriptoProxy.invoke(null, methodGetName, args);
        call.onResponse(callback);
        call.call();

        Whitebox.invokeMethod(scriptoProxy, "onCallbackResponseUi", callId, jsResponse);
        verify(callback).onResponse(convertedJsResponse);
    }

    @Test
    public void testMethodDoubleCallWithResponse() throws Throwable {
        JavaScriptCallResponseCallback<String> callback = mock(JavaScriptCallResponseCallback.class);
        ScriptoProxy scriptoProxy = new ScriptoProxy(scripto, JsTestScript.class);

        JavaScriptFunctionCall<String> call = (JavaScriptFunctionCall<String>) scriptoProxy.invoke(null, methodGetName, args);
        call.onResponse(callback);
        call.call();

        Whitebox.invokeMethod(scriptoProxy, "onCallbackResponseUi", callId, jsResponse);
        Whitebox.invokeMethod(scriptoProxy, "onCallbackResponseUi", callId, jsResponse);

        verify(callback).onResponse(convertedJsResponse);
    }

    @Test
    public void testMethodCallWithNullResponse() throws Throwable {
        JavaScriptCallResponseCallback<String> callback = mock(JavaScriptCallResponseCallback.class);
        ScriptoProxy scriptoProxy = new ScriptoProxy(scripto, JsTestScript.class);

        JavaScriptFunctionCall<String> call = (JavaScriptFunctionCall<String>) scriptoProxy.invoke(null, methodGetName, args);
        call.onResponse(callback);
        call.call();

        Whitebox.invokeMethod(scriptoProxy, "onCallbackResponseUi", callId, null);

        verify(callback).onResponse(null);
    }

    @Test
    public void testMethodCallWithRawResponse() throws Throwable {
        JavaScriptCallResponseCallback<RawResponse> callback = mock(JavaScriptCallResponseCallback.class);
        ScriptoProxy scriptoProxy = new ScriptoProxy(scripto, JsTestScript.class);
        PowerMockito.whenNew(RawResponse.class).withArguments(jsResponse).thenReturn(rawResponse);

        JavaScriptFunctionCall<RawResponse> call = (JavaScriptFunctionCall<RawResponse>) scriptoProxy.invoke(null, methodGetData, args);
        call.onResponse(callback);
        call.call();

        Whitebox.invokeMethod(scriptoProxy, "onCallbackResponseUi", callId, jsResponse);

        verify(callback).onResponse(rawResponse);
    }

    @Test
    public void testMethodCallWithCustomClassResponse() throws Throwable {
        JavaScriptCallResponseCallback<CustomTestModel> callback = mock(JavaScriptCallResponseCallback.class);
        ScriptoProxy scriptoProxy = new ScriptoProxy(scripto, JsTestScript.class);
        when(mJsonToJavaConverter.toObject(jsonResponse, CustomTestModel.class)).thenReturn(customTestModel);

        JavaScriptFunctionCall<CustomTestModel> call = (JavaScriptFunctionCall<CustomTestModel>) scriptoProxy.invoke(null, methodCustomModel, args);
        call.onResponse(callback);
        call.call();

        Whitebox.invokeMethod(scriptoProxy, "onCallbackResponseUi", callId, jsonResponse);

        verify(callback).onResponse(customTestModel);
    }


    @Test
    public void testMethodCallWithError() throws Throwable {
        JavaScriptCallErrorCallback callback = mock(JavaScriptCallErrorCallback.class);
        ScriptoProxy scriptoProxy = new ScriptoProxy(scripto, JsTestScript.class);
        PowerMockito.whenNew(JavaScriptException.class).withArguments(jsErrorMessage).thenReturn(jsException);

        JavaScriptFunctionCall<String> call = (JavaScriptFunctionCall<String>) scriptoProxy.invoke(null, methodGetName, args);
        call.onError(callback);
        call.call();

        Whitebox.invokeMethod(scriptoProxy, "onCallbackErrorUi", callId, jsErrorMessage);
        verify(callback).onError(jsException);
    }

    @Test
    public void testMethodDoubleCallWithError() throws Throwable {
        JavaScriptCallErrorCallback callback = mock(JavaScriptCallErrorCallback.class);
        ScriptoProxy scriptoProxy = new ScriptoProxy(scripto, JsTestScript.class);
        PowerMockito.whenNew(JavaScriptException.class).withArguments(jsErrorMessage).thenReturn(jsException);

        JavaScriptFunctionCall<String> call = (JavaScriptFunctionCall<String>) scriptoProxy.invoke(null, methodGetName, args);
        call.onError(callback);
        call.call();

        Whitebox.invokeMethod(scriptoProxy, "onCallbackErrorUi", callId, jsErrorMessage);
        Whitebox.invokeMethod(scriptoProxy, "onCallbackErrorUi", callId, jsErrorMessage);

        verify(callback).onError(jsException);
    }

}
