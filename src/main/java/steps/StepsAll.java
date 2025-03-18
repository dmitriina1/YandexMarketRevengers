package steps;

import io.qameta.allure.Step;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import pages.YandexAfterSearch;
import pages.YandexBeforeSearch;

import java.util.List;

import static helpers.Properties.testsProperties;
import static helpers.Assertions.assertTrue;

public class StepsAll {
    private static WebDriverWait wait;
    private static WebDriver driver;

    @Step("Переходим на сайт: {url}")
    public static void openSite(String url, String title, WebDriver currentDriver) {
        driver = currentDriver;
        driver.get(url);
        wait = new WebDriverWait(driver, testsProperties.defaultTimeout());
        wait.until(ExpectedConditions.titleContains(title));
        String actualTitle = driver.getTitle();
        assertTrue(actualTitle.contains(title),
                "Заголовок страницы не содержит ожидаемый текст.\n" +
                        "Ожидалось: '" + title + "'\n" +
                        "Фактический заголовок: '" + actualTitle + "'");
    }

    @Step("Переход в Каталог")
    public static void findCatalog(){
        YandexBeforeSearch yandexBeforeSearch = new YandexBeforeSearch(driver);
        yandexBeforeSearch.catalogButtonClick();
    }

    @Step("Наведение на категорию {catalogContent} в Каталоге")
    public static void catalogContentMouseOver(String catalogContent){
        YandexBeforeSearch yandexBeforeSearch = new YandexBeforeSearch(driver);
        yandexBeforeSearch.catalogContentMouseOver(catalogContent);
    }

    @Step("Нажатие и поиск подкатегории {catalogSubItem} в Каталоге")
    public static void goToСatalogSubItem(String catalogSubItem){
        YandexBeforeSearch yandexBeforeSearch = new YandexBeforeSearch(driver);
        yandexBeforeSearch.catalogSubItemClick(catalogSubItem);
    }

    @Step("Проверка что перешел в раздел {catalogSubItem}")
    public static void checkingCatalogSubItemTitle(String catalogSubItem){
        YandexAfterSearch yandexAfterSearch = new YandexAfterSearch(driver);
        yandexAfterSearch.checkingTitleByText(catalogSubItem);
    }

    @Step("Установка диапазона цены от {minimumPrice} до {maximumPrice} в фильтре")
    public static void inputPriceFilter(String minimumPrice, String maximumPrice){
        YandexAfterSearch yandexAfterSearch = new YandexAfterSearch(driver);
        yandexAfterSearch.inputPriceInterval(minimumPrice, maximumPrice);
    }

    @Step("Выбираем бренды {brands} в фильтре")
    public static void inputBrandsFilter(List<String> brands){
        YandexAfterSearch yandexAfterSearch = new YandexAfterSearch(driver);
        yandexAfterSearch.inputBrands(brands);
    }
}
