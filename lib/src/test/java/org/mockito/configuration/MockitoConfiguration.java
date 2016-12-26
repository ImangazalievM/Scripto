package org.mockito.configuration;

/**
    Настройки запуска mockito
 */
public class MockitoConfiguration extends DefaultMockitoConfiguration {

    /**
        Отключаем кеш для нормальной работы PowerMock и Mockito
     */
    @Override
    public boolean enableClassCache() {
        return false;
    }

}