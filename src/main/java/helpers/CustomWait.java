package helpers;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Wait;
import org.openqa.selenium.support.ui.WebDriverWait;

/**
 * Класс {@code CustomWait} предназначен для специфических явных ожиданий.
 * Он методы для ожидания загрузки страницы, элементов и т.д.
 *
 * @author Наливайко Дмитрий
 */
public class CustomWait {

    /**
     * Метод {@code waitPageLoad} ожидает полной загрузки страницы.
     *
     * @param wait явное ожидание
     *
     * @author Наливайко Дмитрий
     */
    public static void waitPageLoad(Wait<WebDriver> wait) {
        wait.until(d -> ((JavascriptExecutor) d).executeScript("return document.readyState").equals("complete"));
    }

    /**
     * Метод {@code waitElementsLoad} ожидает загрузки элементов страницы, после применения фильтров.
     *
     * @param driver веб-драйвер для управления браузером
     * @param wait явное ожидание
     *
     * @author Наливайко Дмитрий
     */
    public static void waitElementsLoad(WebDriver driver, Wait<WebDriver> wait) {
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[@data-auto = 'SerpStatic-loader']")));
        wait.until(ExpectedConditions.invisibilityOf(driver.findElement(By.xpath("//div[@data-auto = 'SerpStatic-loader']"))));
    }


}
