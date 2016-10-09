package imangazaliev.scripto.test;

import android.os.Build;

import org.junit.Before;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import imangazaliev.scripto.BuildConfig;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, manifest=Config.NONE, sdk = Build.VERSION_CODES.LOLLIPOP)
public abstract class BaseTest {

    @Before
    public void setup() throws Exception {
        onSetup();
    }

    public abstract void onSetup() throws Exception;


}
