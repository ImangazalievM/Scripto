package imangazaliev.scripto.java;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.lang.reflect.Method;

import imangazaliev.scripto.Scripto;
import imangazaliev.scripto.test.BaseTest;
import imangazaliev.scripto.test.JsTestScript;

import static junit.framework.Assert.assertEquals;


public class ScriptoFunctionTest extends BaseTest {

    private final Object[] args = new Object[] {"My text", 55, true, null};

    @Mock
    Scripto scripto;
    Method methodInitProfile;
    Method methodSetFontSize;


    @Before
    public void onSetup() throws Exception {
        MockitoAnnotations.initMocks(this);

        methodInitProfile = JsTestScript.class.getMethod("initProfile");
        methodSetFontSize = JsTestScript.class.getMethod("setFontSize");
    }

    @Test
    public void testScriptoFunction() {
        ScriptoFunction function = new ScriptoFunction(scripto, null, methodInitProfile, args, "");

        assertEquals("initProfile('My text',55,true,null);", function.jsFunction);
    }

    @Test
    public void testScriptoWithVariable() {
        ScriptoFunction function = new ScriptoFunction(scripto, "myVar", methodInitProfile, args, "");

        assertEquals("myVar.initProfile('My text',55,true,null);", function.jsFunction);
    }

    @Test
    public void testScriptoWithVariableFromAnnotation() {
        ScriptoFunction function = new ScriptoFunction(scripto, null, methodSetFontSize, new Object[] {14}, "");

        assertEquals("settings.setFontSize(14);", function.jsFunction);
    }



}
