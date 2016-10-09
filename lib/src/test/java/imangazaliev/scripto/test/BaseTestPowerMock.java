package imangazaliev.scripto.test;

import android.os.Build;

import org.junit.Rule;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.modules.junit4.PowerMockRunnerDelegate;
import org.powermock.modules.junit4.rule.PowerMockRule;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.lang.reflect.Method;

import imangazaliev.scripto.BuildConfig;

@RunWith(PowerMockRunner.class)
@PowerMockRunnerDelegate(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, manifest=Config.NONE, sdk = Build.VERSION_CODES.LOLLIPOP)
@PowerMockIgnore({"org.mockito.*", "org.robolectric.*", "android.*", "org.powermock.*"})
public abstract class BaseTestPowerMock {

    @Rule
    public PowerMockRule rule = new PowerMockRule();

    public abstract void onSetup() throws Exception;
}
