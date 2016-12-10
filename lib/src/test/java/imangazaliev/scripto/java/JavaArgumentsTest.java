package imangazaliev.scripto.java;

import org.json.JSONObject;
import org.junit.Test;

import imangazaliev.scripto.java.JavaArguments;
import imangazaliev.scripto.test.BaseTest;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;

public class JavaArgumentsTest extends BaseTest {

    private final String TEST_ARGS = "[\"My text\", 10, true, {\"mybool\":false}, null]";

    @Override
    public void onSetup() {

    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvalidJson() {
        JavaArguments javaArguments = new JavaArguments("MyInvalidJson");
    }

    @Test
    public void testRaw() {
        JavaArguments javaArguments = new JavaArguments(TEST_ARGS);

        assertEquals(TEST_ARGS, javaArguments.getRaw());
    }

    @Test
    public void testArgTypes() {
        JavaArguments javaArguments = new JavaArguments(TEST_ARGS);
        Class<?>[] argsTypes = javaArguments.getArgsTypes();

        assertEquals(String.class, argsTypes[0]);
        assertEquals(Integer.class, argsTypes[1]);
        assertEquals(Boolean.class, argsTypes[2]);
        assertEquals(JSONObject.class, argsTypes[3]);
    }

    @Test
    public void testArgValues() {
        JavaArguments javaArguments = new JavaArguments(TEST_ARGS);
        Object[] argsObjects = javaArguments.getArgs();

        assertEquals("My text", argsObjects[0]);
        assertEquals(10, argsObjects[1]);
        assertEquals(true, argsObjects[2]);
        assertNotNull(argsObjects[3]);
        assertEquals(null, argsObjects[4]);
    }

}
