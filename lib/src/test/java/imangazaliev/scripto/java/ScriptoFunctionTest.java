package imangazaliev.scripto.java;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.lang.reflect.Method;

import imangazaliev.scripto.Scripto;
import imangazaliev.scripto.test.BaseTest;
import imangazaliev.scripto.test.BaseTestPowerMock;
import imangazaliev.scripto.test.CustomTestModel;
import imangazaliev.scripto.test.JsScript;

import static junit.framework.Assert.assertEquals;


public class ScriptoFunctionTest extends BaseTest {

    private final Object[] args = new Object[] {"My text", 55, true, null};

    @Mock
    Scripto scripto;

    Method methodGetName;


    @Before
    public void onSetup() throws Exception {
        MockitoAnnotations.initMocks(this);

        methodGetName = JsScript.class.getMethod("getName");
    }

    @Test
    public void testScriptoFunction() {
        ScriptoFunction function = new ScriptoFunction(scripto, methodGetName, args, "");

        assertEquals("getName('My text',55,true,null);", function.jsFunction);
    }



}
