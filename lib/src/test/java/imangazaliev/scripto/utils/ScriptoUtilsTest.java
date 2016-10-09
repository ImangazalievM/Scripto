package imangazaliev.scripto.utils;

import org.junit.Test;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.robolectric.Robolectric;
import org.robolectric.shadows.ShadowLooper;

import java.lang.reflect.Method;
import java.lang.reflect.Type;

import imangazaliev.scripto.java.ScriptoFunctionCall;
import imangazaliev.scripto.test.BaseTestPowerMock;
import imangazaliev.scripto.test.JsScript;
import imangazaliev.scripto.test.TestJsInterface;

import static junit.framework.Assert.assertEquals;

public class ScriptoUtilsTest extends BaseTestPowerMock {

    @Override
    public void onSetup() {

    }

    @Test
    public void testValidateScriptInterface() {
        //если не выдало ошибку, то все нормально
        ScriptoUtils.validateScriptInterface(JsScript.class);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testValidateScriptInterfaceIllegal() {
        ScriptoUtils.validateScriptInterface(TestJsInterface.class);
    }

    @Test
    public void testCheckNotNull() {
        //если не выдало ошибку, то все нормально
        ScriptoUtils.checkNotNull(new Object(), "Error message");
    }

    @Test(expected = NullPointerException.class)
    public void testCheckNotNullWithNull() {
        ScriptoUtils.checkNotNull(null, "Error message");
    }

    @Test
    public void testIsPrimitiveWrapper() {
        assertEquals(true, ScriptoUtils.isPrimitiveWrapper(Boolean.class));
        assertEquals(true, ScriptoUtils.isPrimitiveWrapper(Byte.class));
        assertEquals(true, ScriptoUtils.isPrimitiveWrapper(Character.class));
        assertEquals(true, ScriptoUtils.isPrimitiveWrapper(Double.class));
        assertEquals(true, ScriptoUtils.isPrimitiveWrapper(Float.class));
        assertEquals(true, ScriptoUtils.isPrimitiveWrapper(Integer.class));
        assertEquals(true, ScriptoUtils.isPrimitiveWrapper(Long.class));
        assertEquals(true, ScriptoUtils.isPrimitiveWrapper(Short.class));

        assertEquals(false, ScriptoUtils.isPrimitiveWrapper(null));
        assertEquals(false, ScriptoUtils.isPrimitiveWrapper(String.class));
    }

    @Test
    public void testGetCallResponseType() throws NoSuchMethodException {
        assertEquals(String.class, ScriptoUtils.getCallResponseType(JsScript.class.getMethod("getName")));
    }

    @Test
    public void hasSecureAnnotation() throws NoSuchMethodException {
        assertEquals(false, ScriptoUtils.hasSecureAnnotation(TestJsInterface.class.getMethod("showAlert")));
        assertEquals(true, ScriptoUtils.hasSecureAnnotation(TestJsInterface.class.getMethod("resetAll")));
    }

    @Test
    public void testRunOnUi()  {

    }


}
