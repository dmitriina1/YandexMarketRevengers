package ru.yandex;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;

import java.util.concurrent.TimeUnit;

import static helpers.Properties.testsProperties;

/**
 * Базовый тестовый класс {@code BaseTest} настраивает и завершает работу веб-драйвера.
 * Используется для инициализации браузера и его закрытия после тестов.
 *
 * @author Наливайко Дмитрий
 */
public class BaseTest {

    /**
     * Поле веб-драйвера Chrome
     *
     * @author Наливайко Дмитрий
     */
    protected WebDriver chromeDriver;

    /**
     * Метод {@code before} выполняется перед каждым тестом.
     * Он настраивает параметры веб-драйвера и открывает браузер.
     *
     * @author Наливайко Дмитрий
     */
    @BeforeEach
    public void before() {
        System.setProperty("webdriver.chrome.driver",System.getenv("CHROME_DRIVER"));
        DesiredCapabilities capabilities = new DesiredCapabilities();
        capabilities.setCapability(CapabilityType.PAGE_LOAD_STRATEGY,"none");
        chromeDriver = new ChromeDriver(capabilities);
        chromeDriver.manage().window().maximize();
        chromeDriver.manage().timeouts().implicitlyWait(testsProperties.defaultTimeout(), TimeUnit.SECONDS);
    }

    /**
     * Метод {@code after} выполняется после каждого теста.
     * Он закрывает браузер и завершает работу веб-драйвера.
     *
     * @author Наливайко Дмитрий
     */
    @AfterEach
    public void after(){
        chromeDriver.quit();
    }
}
