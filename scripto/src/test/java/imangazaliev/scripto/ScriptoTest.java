package imangazaliev.scripto;

import android.content.ContextWrapper;
import android.webkit.WebView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONArray;
import org.json.JSONException;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.MockitoAnnotations;
import org.robolectric.RuntimeEnvironment;

import imangazaliev.scripto.converter.JsonToJavaConverter;
import imangazaliev.scripto.java.JavaInterface;
import imangazaliev.scripto.test.BaseTest;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ScriptoTest extends BaseTest {

    @Rule
    public final ExpectedException exception = ExpectedException.none();

    @Override
    public void onSetup() {
        MockitoAnnotations.initMocks(this);

    }

    @Test
    public void scriptoBuilderTest() throws JSONException {

    }

}
