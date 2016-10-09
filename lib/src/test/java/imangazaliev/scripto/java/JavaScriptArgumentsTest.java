package imangazaliev.scripto.java;

import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import imangazaliev.scripto.Scripto;
import imangazaliev.scripto.test.BaseTest;

import static junit.framework.Assert.assertEquals;

public class JavaScriptArgumentsTest extends BaseTest {

    private final Object[] args = new Object[] {"My text", 55, true, null};

    @Mock
    Scripto scripto;

    @Override
    public void onSetup() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testArgsNull() {
        JavaScriptArguments javaScriptArgument = new JavaScriptArguments(scripto, null);

        assertEquals(0, javaScriptArgument.getArguments().length);
    }

    @Test
    public void testJavaScriptArguments() {
        JavaScriptArguments javaScriptArgument = new JavaScriptArguments(scripto, args);

        assertEquals(4, javaScriptArgument.getArguments().length);
        assertEquals("'My text'", javaScriptArgument.getArguments()[0]);
        assertEquals("55", javaScriptArgument.getArguments()[1]);
        assertEquals("true", javaScriptArgument.getArguments()[2]);
        assertEquals("null", javaScriptArgument.getArguments()[3]);
    }

    @Test
    public void testFormattedArguments() {
        JavaScriptArguments javaScriptArgument = new JavaScriptArguments(scripto, args);

        assertEquals("'My text',55,true,null", javaScriptArgument.getFormattedArguments());
    }

    @Test
    public void testFormattedArgumentsOnNull() {
        JavaScriptArguments javaScriptArgument = new JavaScriptArguments(scripto, null);

        assertEquals("", javaScriptArgument.getFormattedArguments());
    }

}
