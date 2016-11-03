package imangazaliev.scripto.java;

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
import imangazaliev.scripto.converter.JavaConverter;
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


@PrepareForTest({StringUtils.class, ScriptoUtils.class, ScriptoProxy.class,  ScriptoFunction.class})
public class ScriptoProxyTest extends BaseTestPowerMock {

    private final String proxyId = "adrgb";
    private final String callCode = "26486";
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
    ScriptoFunction scriptoFunction;
    @Mock
    JavaConverter javaConverter;

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
        when(scripto.getJavaConverter()).thenReturn(javaConverter);
        when(javaConverter.toObject(jsResponse, String.class)).thenReturn(convertedJsResponse);
    }

    private void initPowerMockito() throws Exception {
        PowerMockito.when(StringUtils.randomString(5)).thenReturn(proxyId);
        PowerMockito.when(StringUtils.randomNumericString(5)).thenReturn(callCode);
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

        PowerMockito.whenNew(ScriptoFunction.class).withAnyArguments().thenReturn(scriptoFunction);
    }

    @Test
    public void testMethodCall() throws Throwable {
        ScriptoProxy scriptoProxy = new ScriptoProxy(scripto, JsTestScript.class);
        ScriptoFunctionCall<String> call = (ScriptoFunctionCall<String>) scriptoProxy.invoke(null, methodGetName, args);

        verify(webview).addJavascriptInterface(scriptoProxy, proxyId);
        verify(scriptoFunction, never()).callJavaScriptFunction(callCode);

        call.call();
        verify(scriptoFunction).callJavaScriptFunction(callCode);
    }

    @Test
    public void testMethodCallWithResponse() throws Throwable {
        ScriptoResponseCallback<String> callback = mock(ScriptoResponseCallback.class);
        ScriptoProxy scriptoProxy = new ScriptoProxy(scripto, JsTestScript.class);

        ScriptoFunctionCall<String> call = (ScriptoFunctionCall<String>) scriptoProxy.invoke(null, methodGetName, args);
        call.onResponse(callback);
        call.call();

        Whitebox.invokeMethod(scriptoProxy, "onCallbackResponseUi", callCode, jsResponse);
        verify(callback).onResponse(convertedJsResponse);
    }

    @Test
    public void testMethodDoubleCallWithResponse() throws Throwable {
        ScriptoResponseCallback<String> callback = mock(ScriptoResponseCallback.class);
        ScriptoProxy scriptoProxy = new ScriptoProxy(scripto, JsTestScript.class);

        ScriptoFunctionCall<String> call = (ScriptoFunctionCall<String>) scriptoProxy.invoke(null, methodGetName, args);
        call.onResponse(callback);
        call.call();

        Whitebox.invokeMethod(scriptoProxy, "onCallbackResponseUi", callCode, jsResponse);
        Whitebox.invokeMethod(scriptoProxy, "onCallbackResponseUi", callCode, jsResponse);

        verify(callback).onResponse(convertedJsResponse);
    }

    @Test
    public void testMethodCallWithNullResponse() throws Throwable {
        ScriptoResponseCallback<String> callback = mock(ScriptoResponseCallback.class);
        ScriptoProxy scriptoProxy = new ScriptoProxy(scripto, JsTestScript.class);

        ScriptoFunctionCall<String> call = (ScriptoFunctionCall<String>) scriptoProxy.invoke(null, methodGetName, args);
        call.onResponse(callback);
        call.call();

        Whitebox.invokeMethod(scriptoProxy, "onCallbackResponseUi", callCode, null);

        verify(callback).onResponse(null);
    }

    @Test
    public void testMethodCallWithRawResponse() throws Throwable {
        ScriptoResponseCallback<RawResponse> callback = mock(ScriptoResponseCallback.class);
        ScriptoProxy scriptoProxy = new ScriptoProxy(scripto, JsTestScript.class);
        PowerMockito.whenNew(RawResponse.class).withArguments(jsResponse).thenReturn(rawResponse);

        ScriptoFunctionCall<RawResponse> call = (ScriptoFunctionCall<RawResponse>) scriptoProxy.invoke(null, methodGetData, args);
        call.onResponse(callback);
        call.call();

        Whitebox.invokeMethod(scriptoProxy, "onCallbackResponseUi", callCode, jsResponse);

        verify(callback).onResponse(rawResponse);
    }

    @Test
    public void testMethodCallWithCustomClassResponse() throws Throwable {
        ScriptoResponseCallback<CustomTestModel> callback = mock(ScriptoResponseCallback.class);
        ScriptoProxy scriptoProxy = new ScriptoProxy(scripto, JsTestScript.class);
        when(javaConverter.toObject(jsonResponse, CustomTestModel.class)).thenReturn(customTestModel);

        ScriptoFunctionCall<CustomTestModel> call = (ScriptoFunctionCall<CustomTestModel>) scriptoProxy.invoke(null, methodCustomModel, args);
        call.onResponse(callback);
        call.call();

        Whitebox.invokeMethod(scriptoProxy, "onCallbackResponseUi", callCode, jsonResponse);

        verify(callback).onResponse(customTestModel);
    }


    @Test
    public void testMethodCallWithError() throws Throwable {
        ScriptoErrorCallback callback = mock(ScriptoErrorCallback.class);
        ScriptoProxy scriptoProxy = new ScriptoProxy(scripto, JsTestScript.class);
        PowerMockito.whenNew(JavaScriptException.class).withArguments(jsErrorMessage).thenReturn(jsException);

        ScriptoFunctionCall<String> call = (ScriptoFunctionCall<String>) scriptoProxy.invoke(null, methodGetName, args);
        call.onError(callback);
        call.call();

        Whitebox.invokeMethod(scriptoProxy, "onCallbackErrorUi", callCode, jsErrorMessage);
        verify(callback).onError(jsException);
    }

    @Test
    public void testMethodDoubleCallWithError() throws Throwable {
        ScriptoErrorCallback callback = mock(ScriptoErrorCallback.class);
        ScriptoProxy scriptoProxy = new ScriptoProxy(scripto, JsTestScript.class);
        PowerMockito.whenNew(JavaScriptException.class).withArguments(jsErrorMessage).thenReturn(jsException);

        ScriptoFunctionCall<String> call = (ScriptoFunctionCall<String>) scriptoProxy.invoke(null, methodGetName, args);
        call.onError(callback);
        call.call();

        Whitebox.invokeMethod(scriptoProxy, "onCallbackErrorUi", callCode, jsErrorMessage);
        Whitebox.invokeMethod(scriptoProxy, "onCallbackErrorUi", callCode, jsErrorMessage);

        verify(callback).onError(jsException);
    }

}
