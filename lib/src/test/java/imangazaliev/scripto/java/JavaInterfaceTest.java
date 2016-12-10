package imangazaliev.scripto.java;

import android.webkit.WebView;

import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;

import imangazaliev.scripto.Scripto;
import imangazaliev.scripto.ScriptoException;
import imangazaliev.scripto.converter.JsonToJavaConverter;
import imangazaliev.scripto.converter.JavaToJsonConverter;
import imangazaliev.scripto.test.BaseTest;
import imangazaliev.scripto.test.TestJsInterface;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class JavaInterfaceTest extends BaseTest {

    private final String interfaceTag = "MyTag";
    
    @Mock
    Scripto scripto;
    @Mock
    WebView webview;
    @Spy
    TestJsInterface jsInterfaceObj;
    @Spy
    JsonToJavaConverter mJsonToJavaConverter;
    @Spy
    JavaToJsonConverter mJavaToJsonConverter;

    @Override
    public void onSetup() {
        MockitoAnnotations.initMocks(this);

        when(scripto.getWebView()).thenReturn(webview);
        when(scripto.getJsonToJavaConverter()).thenReturn(mJsonToJavaConverter);
        when(scripto.getJavaToJsonConverter()).thenReturn(mJavaToJsonConverter);
    }

    @Test()
    public void testMethodCall() {
        JavaInterface javaInterface = new JavaInterface(scripto, interfaceTag, jsInterfaceObj);
        javaInterface.call("showAlert", "[]");

        verify(jsInterfaceObj).showAlert();
    }

    @Test(expected = ScriptoException.class)
    public void testInvalidMethodNameCall() {
        JavaInterface javaInterface = new JavaInterface(scripto, interfaceTag, jsInterfaceObj);
        javaInterface.call("myInvalidMethodName", "[]");

        verify(jsInterfaceObj).showAlert();
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvalidArguments() {
        JavaInterface javaInterface = new JavaInterface(scripto, interfaceTag, jsInterfaceObj);
        javaInterface.call("showAlert", "myInvalidArgs");

        verify(jsInterfaceObj).showAlert();
    }

    @Test()
    public void testMethodCallWithArgs() {
        JavaInterface javaInterface = new JavaInterface(scripto, interfaceTag, jsInterfaceObj);
        javaInterface.call("showMessage", "[\"MyText\"]");

        verify(mJsonToJavaConverter).toObject("MyText", String.class);
        verify(jsInterfaceObj).showMessage("MyText");
    }

    @Test()
    public void testMethodCallWithCallback() {
        JavaInterface javaInterface = new JavaInterface(scripto, interfaceTag, jsInterfaceObj);
        javaInterface.callWithCallback("getName", "[]", "my_code");

        verify(jsInterfaceObj).getName();
        verify(mJavaToJsonConverter).convertToString("My name", String.class);
        verify(webview).loadUrl("javascript:Scripto.removeCallBack('my_code', '\"My name\"')");
    }

    @Test()
    public void testMethodCallWithCallbackResponseNull() {
        JavaInterface javaInterface = new JavaInterface(scripto, interfaceTag, jsInterfaceObj);
        javaInterface.callWithCallback("getNull", "[]", "my_code");

        verify(jsInterfaceObj).getNull();
        verify(mJavaToJsonConverter, never()).convertToString(Mockito.anyString(), any(Class.class));
        verify(webview).loadUrl("javascript:Scripto.removeCallBack('my_code', 'null')");
    }

    @Test()
    public void testMethodCallWithAnnotationProtection() {
        JavaInterfaceConfig config = new JavaInterfaceConfig();
        config.enableAnnotationProtection(true);
        TestJsInterface testJsInterface = new TestJsInterface();

        JavaInterface javaInterface = new JavaInterface(scripto, interfaceTag, testJsInterface, config);
        javaInterface.callWithCallback("resetAll", "[]", "my_code");

        //verify(testJsInterface).resetAll();
        verify(webview).loadUrl("javascript:Scripto.removeCallBack('my_code', 'null')");
    }

    @Test()
    public void testMethodCallWithInvalidAnnotationProtection() {
        JavaInterfaceConfig config = new JavaInterfaceConfig();
        config.enableAnnotationProtection(true);
        Scripto.ErrorHandler handler = mock(Scripto.ErrorHandler.class);
        when(scripto.getErrorHandler()).thenReturn(handler);
        TestJsInterface testJsInterface = new TestJsInterface();

        JavaInterface javaInterface = new JavaInterface(scripto, interfaceTag, testJsInterface, config);
        javaInterface.callWithCallback("killAll", "[]", "my_code");

        verify(handler).onError(any(JavaScriptSecureException.class));
    }

}
