package pages;

import helpers.CustomWait;
import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import static helpers.Properties.testsProperties;

/**
 * Класс {@code YandexBeforeSearch} предназначен для работы с главной страницей Яндекс.Маркета.
 * Он предоставляет методы для взаимодействия с каталогом, включая выбор категории и подкатегории.
 */
public class YandexBeforeSearch {

    /**
     * Поле веб-драйвера Chrome
     *
     * @author Наливайко Дмитрий
     */
    private WebDriver driver;

    /**
     * Поле категории из каталога
     *
     * @author Наливайко Дмитрий
     */
    private WebElement catalogContent;

    /**
     * Поле подкатегории из каталога
     *
     * @author Наливайко Дмитрий
     */
    private WebElement catalogSubItem;

    /**
     * Поле кнопки каталога
     *
     * @author Наливайко Дмитрий
     */
    private WebElement catalogButton;

    /**
     * Поле ожидания
     *
     * @author Наливайко Дмитрий
     */
    private WebDriverWait wait;

    /**
     * Конструктор класса {@code YandexBeforeSearch}.
     * Инициализирует веб-драйвер и ожидание.
     *
     * @param driver веб-драйвер для управления браузером
     *
     * @author Наливайко Дмитрий
     */
    public YandexBeforeSearch(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, testsProperties.defaultTimeout());
    }

    /**
     * Метод {@code catalogButtonClick} выполняет поиск и нажатие по кнопке каталога
     *
     * @author Наливайко Дмитрий
     */
    public void catalogButtonClick() {
        CustomWait.waitPageLoad(wait);
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(
                "//div[(@data-baobab-name='catalog')]")));
        this.catalogButton = driver.findElement(By.xpath(
                "//div[(@data-baobab-name='catalog')]"));
        catalogButton.click();
    }

    /**
     * Метод {@code catalogContentMouseOver} выполняет поиск и наведение на категорию из каталога
     *
     * @param catalogContent искомая категория
     *
     * @author Наливайко Дмитрий
     */
    public void catalogContentMouseOver(String catalogContent) {
        CustomWait.waitPageLoad(wait);
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(String.format(
                "//div[contains(@data-zone-name, 'catalog-content')]//ul//a//span[contains(text(), '%s')]",
                catalogContent))));
        this.catalogContent = driver.findElement(By.xpath(String.format(
                "//div[contains(@data-zone-name, 'catalog-content')]//ul//a//span[contains(text(), '%s')]",
                catalogContent)));
        Actions actions = new Actions(driver);
        actions.moveToElement(this.catalogContent).perform();
    }

    /**
     * Метод {@code catalogSubItemClick} выполняет поиск и нажатие на подкатегорию из каталога
     *
     * @param subItem искомая подкатегория
     *
     * @author Наливайко Дмитрий
     */
    public void catalogSubItemClick(String subItem) {
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(String.format(
                "//li//a[contains(text(), '%s')]",
                subItem))));
        this.catalogSubItem = driver.findElement(By.xpath(String.format(
                "//li//a[contains(text(), '%s')]",
                subItem)));
        catalogSubItem.click();
    }
}
