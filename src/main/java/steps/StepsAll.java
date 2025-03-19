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
    private static YandexBeforeSearch yandexBeforeSearch;
    private static YandexAfterSearch yandexAfterSearch;


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
        yandexBeforeSearch = new YandexBeforeSearch(driver);
        yandexBeforeSearch.catalogButtonClick();
    }

    @Step("Наведение на категорию {catalogContent} в Каталоге")
    public static void catalogContentMouseOver(String catalogContent){
        yandexBeforeSearch.catalogContentMouseOver(catalogContent);
    }

    @Step("Нажатие и поиск подкатегории {catalogSubItem} в Каталоге")
    public static void goToСatalogSubItem(String catalogSubItem){
        yandexBeforeSearch.catalogSubItemClick(catalogSubItem);
    }

    @Step("Проверка что перешел в раздел {catalogSubItem}")
    public static void checkingCatalogSubItemTitle(String catalogSubItem){
        yandexAfterSearch = new YandexAfterSearch(driver);
        yandexAfterSearch.checkingTitleByText(catalogSubItem);
    }

    @Step("Установка диапазона цены от {minimumPrice} до {maximumPrice} в фильтре")
    public static void inputPriceFilter(String minimumPrice, String maximumPrice){
        yandexAfterSearch.inputPriceInterval(minimumPrice, maximumPrice);
    }

    @Step("Выбираем бренды {brands} в фильтре")
    public static void inputBrandsFilter(List<String> brands){
        yandexAfterSearch.inputBrands(brands);
    }

    @Step("Проверка количества элементов на первой странице")
    public static void checkCountElementsOnFirstPage(int elementsCount){
        yandexAfterSearch.checkCountElementsOnFirstPage(elementsCount);
    }

    @Step("Проверка предложений по соответсвию фильтрам")
    public static void checkFiltersWork(String minimumPrice, String maximumPrice, List<String> brands){
        yandexAfterSearch.checkFilters(minimumPrice, maximumPrice, brands);
    }

    @Step("Возвращение в начало списка и запоминание первого наименования ноутбука")
    public static void pageUpAndSaveFirstElement(){
        yandexAfterSearch.saveFirstObject();
    }

    @Step("Ввод первого наименования ноутбука в поисковую строку на сайте")
    public static void inputFirstElementName(){
        yandexAfterSearch.inputSearchFirstName();
    }

    @Step("Нажатие на кнопку поиска на сайте")
    public static void getFirstElementName(){
        yandexAfterSearch.buttonSearchClick();
    }

    @Step("Проверка, что в результатах поиска, на первой странице, есть искомый товар")
    public static void searchFirstElement(){
        yandexAfterSearch.searchFirstElement();
    }

}
