package imangazaliev.scripto.utils;

import android.content.Context;
import android.content.res.AssetManager;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import imangazaliev.scripto.test.BaseTest;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertNull;
import static org.mockito.Mockito.when;

public class ScriptoAssetsJavaScriptReaderTest extends BaseTest {

    @Rule
    public final ExpectedException exception = ExpectedException.none();

    @Mock
    Context mContextMock;
    @Mock
    AssetManager mAssetManagerMock;
    ScriptoAssetsJavaScriptReader mScriptoAssetsJavaScriptReader;

    private final String mText = "My super mText";
    private final String mFileName = "step.xml";
    private final InputStream mTextStream = new ByteArrayInputStream(mText.getBytes());

    @Override
    public void onSetup() {
        MockitoAnnotations.initMocks(this);
        when(mContextMock.getAssets()).thenReturn(mAssetManagerMock);

        mScriptoAssetsJavaScriptReader = new ScriptoAssetsJavaScriptReader(mContextMock);
    }

    @Test
    public void readTextFile() throws IOException {
        when(mContextMock.getAssets().open(mFileName)).thenReturn(mTextStream);
        String testText = mScriptoAssetsJavaScriptReader.read(mFileName);
        assertNotNull(testText);
        assertEquals(mText, testText);
    }

    @Test
    public void contextNullTest() throws IOException {
        Context context = null;
        exception.expect(IllegalArgumentException.class);
        new ScriptoAssetsJavaScriptReader(context);
    }

    @Test
    public void fileNameNullTest() throws IOException {
        String fileName = null;
        exception.expect(IllegalArgumentException.class);
        mScriptoAssetsJavaScriptReader.read(fileName);
    }

    @Test
    public void invalidFileName() throws IOException {
        String fileName = "invalid_file.xml";
        String testText = mScriptoAssetsJavaScriptReader.read(fileName);
        assertNull(testText);
    }

}
