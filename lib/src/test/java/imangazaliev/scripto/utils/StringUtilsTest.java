package imangazaliev.scripto.utils;

import org.junit.Test;

import imangazaliev.scripto.test.BaseTest;

import static junit.framework.Assert.assertEquals;

public class StringUtilsTest extends BaseTest {


    @Override
    public void onSetup() {
    }

    @Test
    public void testRandomString() {
        String randomString = StringUtils.randomString(5);

        assertEquals(5, randomString.length());
    }

    @Test
    public void testRandomNumericString() {
        String randomString = StringUtils.randomNumericString(5);

        assertEquals(5, randomString.length());
        //если не выдаст ошибку, значит это действительно строка из чисел
        Integer.valueOf(randomString);
    }

}
