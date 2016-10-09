package imangazaliev.scripto;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.MockitoAnnotations;

import imangazaliev.scripto.test.BaseTest;

public class ScriptoTest extends BaseTest {

    @Rule
    public final ExpectedException exception = ExpectedException.none();

    @Override
    public void onSetup() {
        MockitoAnnotations.initMocks(this);

    }

    @Test
    public void readTextFile() {

    }

}
