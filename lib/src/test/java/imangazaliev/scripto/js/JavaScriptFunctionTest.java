package imangazaliev.scripto.js;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.lang.reflect.Method;

import imangazaliev.scripto.Scripto;
import imangazaliev.scripto.js.JavaScriptFunction;
import imangazaliev.scripto.test.BaseTest;
import imangazaliev.scripto.test.JsTestScript;

import static junit.framework.Assert.assertEquals;


public class JavaScriptFunctionTest extends BaseTest {

    private final Object[] args = new Object[] {"My text", 55, true, null};

    @Mock
    Scripto scripto;
    Method methodInitProfile;


    @Before
    public void onSetup() throws Exception {
        MockitoAnnotations.initMocks(this);

        methodInitProfile = JsTestScript.class.getMethod("initProfile");
    }

    @Test
    public void testScriptoFunction() {
        JavaScriptFunction function = new JavaScriptFunction(scripto, null, methodInitProfile, args, "");

        assertEquals("initProfile('My text',55,true,null);", function.jsFunction);
    }

    @Test
    public void testScriptoFunctionWithVariableName() {
        JavaScriptFunction function = new JavaScriptFunction(scripto, "myVar", methodInitProfile, args, "");

        assertEquals("myVar.initProfile('My text',55,true,null);", function.jsFunction);
    }

    @Test
    public void testScriptoFunctionWithJsVariableNameAnnotation() throws NoSuchMethodException {
        JavaScriptFunction function = new JavaScriptFunction(scripto, null, JsTestScript.class.getMethod("setFontSize"), new Object[] {14}, "");

        assertEquals("settings.setFontSize(14);", function.jsFunction);
    }

    @Test
    public void testScriptoFunctionWithDifferentNames() throws NoSuchMethodException {
        JavaScriptFunction function = new JavaScriptFunction(scripto, null, JsTestScript.class.getMethod("getLogin"), new Object[] {}, "");

        assertEquals("getUserLogin();", function.jsFunction);
    }



}
