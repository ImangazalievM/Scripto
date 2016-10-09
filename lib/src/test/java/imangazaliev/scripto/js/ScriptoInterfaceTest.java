package imangazaliev.scripto.js;

import android.webkit.WebView;

import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.powermock.modules.junit4.rule.PowerMockRule;

import imangazaliev.scripto.Scripto;
import imangazaliev.scripto.ScriptoException;
import imangazaliev.scripto.converter.JavaConverter;
import imangazaliev.scripto.converter.JavaScriptConverter;
import imangazaliev.scripto.test.BaseTest;
import imangazaliev.scripto.test.TestJsInterface;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class ScriptoInterfaceTest extends BaseTest {

    private final String interfaceTag = "MyTag";
    
    @Mock
    Scripto scripto;
    @Mock
    WebView webview;
    @Spy
    TestJsInterface jsInterfaceObj;
    @Spy
    JavaConverter javaConverter;
    @Spy
    JavaScriptConverter javaScriptConverter;

    @Override
    public void onSetup() {
        MockitoAnnotations.initMocks(this);

        when(scripto.getWebView()).thenReturn(webview);
        when(scripto.getJavaConverter()).thenReturn(javaConverter);
        when(scripto.getJavaScriptConverter()).thenReturn(javaScriptConverter);
    }

    @Test()
    public void testMethodCall() {
        ScriptoInterface javaInterface = new ScriptoInterface(scripto, interfaceTag, jsInterfaceObj);
        javaInterface.call("showAlert", "[]");

        verify(jsInterfaceObj).showAlert();
    }

    @Test(expected = ScriptoException.class)
    public void testInvalidMethodNameCall() {
        ScriptoInterface javaInterface = new ScriptoInterface(scripto, interfaceTag, jsInterfaceObj);
        javaInterface.call("myInvalidMethodName", "[]");

        verify(jsInterfaceObj).showAlert();
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvalidArguments() {
        ScriptoInterface javaInterface = new ScriptoInterface(scripto, interfaceTag, jsInterfaceObj);
        javaInterface.call("showAlert", "myInvalidArgs");

        verify(jsInterfaceObj).showAlert();
    }

    @Test()
    public void testMethodCallWithArgs() {
        ScriptoInterface javaInterface = new ScriptoInterface(scripto, interfaceTag, jsInterfaceObj);
        javaInterface.call("showMessage", "[\"MyText\"]");

        verify(javaConverter).toObject("MyText", String.class);
        verify(jsInterfaceObj).showMessage("MyText");
    }

    @Test()
    public void testMethodCallWithCallback() {
        ScriptoInterface javaInterface = new ScriptoInterface(scripto, interfaceTag, jsInterfaceObj);
        javaInterface.callWithCallback("getName", "[]", "my_code");

        verify(jsInterfaceObj).getName();
        verify(javaScriptConverter).convertToString("My name", String.class);
        verify(webview).loadUrl("javascript:Scripto.removeCallBack('my_code', '\"My name\"')");
    }

    @Test()
    public void testMethodCallWithCallbackResponseNull() {
        ScriptoInterface javaInterface = new ScriptoInterface(scripto, interfaceTag, jsInterfaceObj);
        javaInterface.callWithCallback("getNull", "[]", "my_code");

        verify(jsInterfaceObj).getNull();
        verify(javaScriptConverter, never()).convertToString(Mockito.anyString(), any(Class.class));
        verify(webview).loadUrl("javascript:Scripto.removeCallBack('my_code', 'null')");
    }

    @Test()
    public void testMethodCallWithAnnotationProtection() {
        ScriptoInterfaceConfig config = new ScriptoInterfaceConfig();
        config.enableAnnotationProtection(true);
        TestJsInterface testJsInterface = new TestJsInterface();

        ScriptoInterface javaInterface = new ScriptoInterface(scripto, interfaceTag, testJsInterface, config);
        javaInterface.callWithCallback("resetAll", "[]", "my_code");

        //verify(testJsInterface).resetAll();
        verify(webview).loadUrl("javascript:Scripto.removeCallBack('my_code', 'null')");
    }

    @Test()
    public void testMethodCallWithInvalidAnnotationProtection() {
        ScriptoInterfaceConfig config = new ScriptoInterfaceConfig();
        config.enableAnnotationProtection(true);
        Scripto.ErrorHandler handler = mock(Scripto.ErrorHandler.class);
        when(scripto.getErrorHandler()).thenReturn(handler);
        TestJsInterface testJsInterface = new TestJsInterface();

        ScriptoInterface javaInterface = new ScriptoInterface(scripto, interfaceTag, testJsInterface, config);
        javaInterface.callWithCallback("killAll", "[]", "my_code");

        verify(handler).onError(any(ScriptoSecureException.class));
    }

}
