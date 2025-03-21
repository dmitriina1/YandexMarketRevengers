package pages;

import helpers.CustomWait;
import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static helpers.Assertions.assertTrue;
import static helpers.Properties.testsProperties;

/**
 * Класс {@code YandexAfterSearch} предназначен для обработки результатов поиска YandexMarket.
 * Он предоставляет методы для получения элементов, их обработки и поиска на сайте..
 */
public class YandexAfterSearch{

    /**
     * Поле веб-драйвера Chrome
     *
     * @author Наливайко Дмитрий
     */
    private WebDriver driver;

    /**
     * Поле тайтла страницы сайта
     *
     * @author Наливайко Дмитрий
     */
    private WebElement pageTitle;

    /**
     * Поле строки поиска в фильтре производителей
     *
     * @author Наливайко Дмитрий
     */
    private WebElement brandSearch;

    /**
     * Поле кнопки Показать всё в фильтре производителей
     *
     * @author Наливайко Дмитрий
     */
    private WebElement brandShowMoreButton;

    /**
     * Список элементов полученных после применения фильтров
     *
     * @author Наливайко Дмитрий
     */
    private List<WebElement> searchResultsList;

    /**
     * Поле названия первого элемента
     *
     * @author Наливайко Дмитрий
     */
    private String firstElement;

    /**
     * Поле ожидания
     *
     * @author Наливайко Дмитрий
     */
    private WebDriverWait wait;

    /**
     * Список элементов полученных после применения фильтров с первой страницы
     *
     * @author Наливайко Дмитрий
     */
    List<WebElement> foundElements;

    /**
     * Поле actions для наведения и ввода текста на сайте
     *
     * @author Наливайко Дмитрий
     */
    Actions actions;

    /**
     * Конструктор класса {@code YandexAfterSearch}.
     * Инициализирует веб-драйвер, явное ожидание появления результатов поиска, а также список результатов.
     *
     * @param driver веб-драйвер для управления браузером
     *
     * @author Наливайко Дмитрий
     */
    public YandexAfterSearch(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, testsProperties.defaultTimeout());
        this.searchResultsList = new ArrayList<>();
    }

    /**
     * Метод {@code checkingTitleByText} ожидает появления названия подкатегории на странице.
     * Получает ее название и сравнивает с {@code expectedTitle}.
     *
     * @param expectedTitle ожидаемое название подкатегории
     *
     * @author Наливайко Дмитрий
     */
    public void checkingTitleByText(String expectedTitle) {
        pageTitle = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(
                "//div[contains(@data-zone-name, 'searchTitle')]//h1")));

        assertTrue(pageTitle.getText().contains(expectedTitle),
                "Заголовок страницы не содержит ожидаемый текст.\n" +
                        "Ожидалось: '" + pageTitle.getText() + "'\n" +
                        "Фактический заголовок: '" + expectedTitle + "'");
    }

    /**
     * Метод {@code inputPriceInterval} ожидает загрузки страницы.
     * Получает минимальную и максимальную стоимость и вносит ее в фильтр.
     *
     * @param minimum минимальная стоимость
     * @param maximum максимальная стоимость
     *
     * @author Наливайко Дмитрий
     */
    public void inputPriceInterval(String minimum, String maximum) {
        CustomWait.waitPageLoad(wait);
        setPrice("min", minimum);
        setPrice("max", maximum);
    }

    private void setPrice(String priceBorder, String priceValue){
        WebElement priceField = wait.until(ExpectedConditions.visibilityOf((
                driver.findElement(By.xpath(String.format(
                        "//span[contains(@data-auto, 'filter-range-%s')]//label[contains(text(), 'Цена')]/..//input",
                        priceBorder)))
        )));
        priceField.clear();
        priceField.click();
        priceField.sendKeys(priceValue + Keys.ENTER);
        CustomWait.waitElementsLoad(driver, wait);
    }

    /**
     * Метод {@code inputBrands} выбирает подходящих производителей в фильтре на сайте.
     * Если подходящих производителей не видно - нажимает на кнопку Показать всё
     * и вводит поочередно искомых производителей
     *
     * @param brands список необходимых производителей
     *
     * @author Наливайко Дмитрий
     */
    public void inputBrands(List<String> brands) {
        String brandSearchLocator = "//div[contains(@data-zone-data, 'Бренд')]//input";
        String brandShownLocator = "//div[contains(@data-zone-data, 'Бренд')]" +
                "//div[contains(@data-zone-name, 'FilterValue')]";
        String brandShownLocatorAdditional = "//div[contains(@data-zone-data, 'Бренд')]" +
                "//div[contains(@data-zone-name, 'FilterValue') " +
                "and not (contains(@data-baobab-name, 'showMoreFilters'))]";
        List<String> tempBrands = new ArrayList<>(brands);
        actions = new Actions(driver);
        Set<WebElement> brandElements = new HashSet<>();
        long startTime = System.currentTimeMillis();
        long timeout = 10000;
        while (brandElements.size() <= 5) {
            if (isTimeoutExceeded(startTime, timeout)) {
                System.out.println("Время ожидания истекло, выходим из цикла.");
                break;
            }
            List<WebElement> currentBrandElements = driver.findElements(By.xpath(brandShownLocatorAdditional));
            brandElements.addAll(currentBrandElements);

            checkingBrandElements(currentBrandElements, brands, tempBrands);
        }
        if (!tempBrands.isEmpty()) {
            inputBrandsShowMore(brandShownLocator, brandSearchLocator, tempBrands);
        }
    }

    private boolean isTimeoutExceeded(long startTime, long timeout) {
        return System.currentTimeMillis() - startTime > timeout;
    }

    private void checkingBrandElements(List<WebElement> currentBrandElements, List<String> brands,
                                       List<String> tempBrands) {
        for (WebElement element : currentBrandElements) {
            try {
                String text = element.getText().trim();
                for (String brand : brands) {
                    if (text.equalsIgnoreCase(brand)) {
                        selectBrand(element, tempBrands, brand);
                    }
                }
            } catch (StaleElementReferenceException e) {
                System.out.println("Элемент устарел: " + element);
            }
        }
    }

    private void selectBrand(WebElement element, List<String> tempBrands, String brand) {
        WebElement checkbox = element.findElement(By.xpath(".//label"));
        wait.until(ExpectedConditions.elementToBeClickable(checkbox));

        if (!"true".equals(checkbox.getAttribute("aria-checked"))) {
            actions.moveToElement(checkbox).click().perform();
            CustomWait.waitElementsLoad(driver, wait);
            tempBrands.remove(brand);
        }
    }

    private void inputBrandsShowMore(String brandShownLocator, String brandSearchLocator, List<String> tempBrands) {
        clickShowMoreButton(brandShownLocator);
        for (String element : tempBrands) {
            searchAndSelectElement(brandSearchLocator, brandShownLocator, element);
        }
    }

    private void clickShowMoreButton(String brandShownLocator) {
        this.brandShowMoreButton = wait.until(ExpectedConditions.visibilityOf(driver.findElement(By.xpath(
                brandShownLocator + "[contains(@data-baobab-name, 'showMoreFilters')]"))));
        brandShowMoreButton.click();
    }

    private void searchAndSelectElement(String brandSearchLocator, String brandShownLocator, String brand) {
        this.brandSearch = wait.until(ExpectedConditions.visibilityOf(
                driver.findElement(By.xpath(brandSearchLocator))));
        brandSearch.clear();
        brandSearch.click();
        brandSearch.sendKeys(brand);
        selectInvisibleElement(brandShownLocator, brand);
    }

    private void selectInvisibleElement(String brandShownLocator, String brand) {
        By elementLocator = By.xpath(brandShownLocator +
                "//span[(text()='" + brand + "' )]");
        WebElement brandObject = wait.until(ExpectedConditions.elementToBeClickable(
                driver.findElement(elementLocator)));
        brandObject.click();
        CustomWait.waitElementsLoad(driver, wait);
    }

    private void takeElementsOnFirstPage() {
        CustomWait.waitPageLoad(wait);
        foundElements = new ArrayList<>();
        actions = new Actions(driver);
        long startTime = System.currentTimeMillis();
        while ((System.currentTimeMillis() - startTime) < testsProperties.longTimeout()) {
            List<WebElement> elements = driver.findElements(By.xpath(
                    "//div[contains(@data-auto, 'SerpList')]" +
                            "//div[contains(@data-auto-themename, 'listDetailed')]"));
            foundElements.addAll(elements);
            if (!driver.findElements(By.xpath("//div[contains(@data-baobab-name, 'pager')]")).isEmpty()) {
                break;
            }
            actions.sendKeys(org.openqa.selenium.Keys.PAGE_DOWN).perform();
        }
    }

    /**
     * Метод {@code checkCountElementsOnFirstPage} получает элементы с первой страницы.
     * Рассчитывает их количество и проверяет, что их больше {@code expectedElementsCount}
     *
     * @param expectedElementsCount искомое количество элементов на первой странице
     *
     * @author Наливайко Дмитрий
     */
    public void checkCountElementsOnFirstPage(int expectedElementsCount){
        takeElementsOnFirstPage();
        assertTrue(foundElements.size() > expectedElementsCount,
                "Количество элементов на первой странице меньше " + expectedElementsCount);
    }

    private void countOfElementsOnAllPages() {
        List<WebElement> foundElements = new ArrayList<>();
        actions = new Actions(driver);
        long startTime = System.currentTimeMillis();
        while (System.currentTimeMillis() - startTime < testsProperties.longTimeout()) {
            if (isLastPage()) {
                foundElements.addAll(driver.findElements(By.xpath(
                        "//div[contains(@data-auto, 'SerpList')]" +
                                "//div[contains(@data-auto-themename, 'listDetailed')]")));
                break;
            } else {
                actions.sendKeys(Keys.PAGE_DOWN).perform();
                try {
                    wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(
                            "//div[contains(@data-auto, 'SerpList')]" +
                                    "//div[contains(@data-auto-themename, 'listDetailed')]")));
                } catch (TimeoutException e) {
                    System.out.println("Элемент не появился вовремя.");
                }
            }
        }

        searchResultsList = foundElements;
    }

    private boolean isLastPage() {
        return driver.findElements(By.xpath("//div[contains(@data-baobab-name, 'pager')]" +
                "//div[contains(@data-baobab-name, 'next')]")).isEmpty();
    }

    /**
     * Метод {@code checkFilters} получает список элементов найденных на всей странице.
     * Проверяет, что все элементы удовлетворяют фильтрам стоимости и произовдителям,
     * путем сравнения стоимости и названия элементов с искомыми. Если некоторые элементы не подходят под фильтры, то
     * происходит переход в карточку товара и проверяется производитель указанный в ней.
     *
     * @param minimum минимальная стоимость
     * @param maximum максимальная стоимость
     * @param namesOfBrand список искомых производителей
     *
     * @author Наливайко Дмитрий
     */
    public void checkFilters(String minimum, String maximum, List<String> namesOfBrand) {
        countOfElementsOnAllPages();
        double min = Double.parseDouble(minimum);
        double max = Double.parseDouble(maximum);
        List<WebElement> filteredProducts = filterProducts(min, max, namesOfBrand);

        List<WebElement> remainingProducts = searchResultsList.stream()
                .filter(product -> !filteredProducts.contains(product))
                .collect(Collectors.toList());

        checkBrandInCard(remainingProducts, namesOfBrand, filteredProducts);

        remainingProducts.clear();
        remainingProducts = searchResultsList.stream()
                .filter(element -> !filteredProducts.contains(element))
                .collect(Collectors.toList());

        assertTrue(filteredProducts.size() == searchResultsList.size(),
                "Не все элементы подходят под фильтр:" + remainingProducts);
    }

    private List<WebElement> filterProducts(Double minimum, Double maximum, List<String> namesOfBrand) {
        return searchResultsList.stream()
                .filter(product -> {
                    String titleText = product.findElement(By.xpath(
                            ".//div[contains(@data-baobab-name, 'title')]//span"
                    )).getText().toLowerCase();
                    boolean isBrandMatch = namesOfBrand.stream().map(String::toLowerCase).anyMatch(titleText::contains);
                    double price = getPrice(product);
                    boolean isPriceInRange = price > minimum && price < maximum;
                    return isBrandMatch && isPriceInRange;
                })
                .collect(Collectors.toList());
    }

    private double getPrice(WebElement product) {
        String priceText = product.findElement(By.xpath(
                ".//span[contains(@data-auto, 'snippet-price-current')]//*[1][name()='span']"
        )).getText().replaceAll("[^\\d.]", "");
        return Double.parseDouble(priceText);
    }

    private void checkBrandInCard(List<WebElement> remainingProducts, List<String> namesOfBrand,
                                  List<WebElement> filteredProducts) {
        for (WebElement element : remainingProducts) {
            element.click();
            CustomWait.waitPageLoad(wait);
            ArrayList<String> tabs = new ArrayList<>(driver.getWindowHandles());
            driver.switchTo().window(tabs.get(tabs.size() - 1));

            List<WebElement> brandElements = driver.findElements(By.xpath(
                    "//div[contains(@data-zone-name, 'fullSpecs')]" +
                            "//div[contains(@aria-label, 'Характеристики')]//span[text()='Бренд']" +
                            "/../../following-sibling::div[1]//span"));
            if (!brandElements.isEmpty()) {
                String brandText = brandElements.get(0).getText().trim();
                if (namesOfBrand.stream().anyMatch(brandText::equalsIgnoreCase)) {
                    filteredProducts.add(element);
                }
            }
            driver.close();
            driver.switchTo().window(tabs.get(0));
        }
    }

    /**
     * Метод {@code saveFirstObject} поднимается до начала страницы.
     * Получает и сохраняет наименование первого элемента.
     *
     * @author Наливайко Дмитрий
     */
    public void saveFirstObject() {
        long startTime = System.currentTimeMillis();
        boolean reachedTop = false;
        WebElement body = driver.findElement(By.tagName("body"));
        actions = new Actions(driver);

        while (System.currentTimeMillis() - startTime < testsProperties.longTimeout() && !reachedTop) {
            try {
                actions.sendKeys(Keys.PAGE_UP)
                        .pause(Duration.ofMillis(30))
                        .perform();

                Number offset = (Number) ((JavascriptExecutor) driver)
                        .executeScript("return window.pageYOffset;");
                reachedTop = offset.doubleValue() <= 1.0;

                if (!reachedTop) {
                    actions.sendKeys(Keys.ARROW_UP)
                            .pause(Duration.ofMillis(20))
                            .perform();
                }
            } catch (StaleElementReferenceException e) {
                body = driver.findElement(By.tagName("body"));
            }

        }
        firstElement = driver.findElement(By.xpath(
                "//div[contains(@data-auto, 'SerpList')]" +
                        "//div[contains(@data-auto-themename, 'listDetailed')]//span[@data-auto = 'snippet-title']"
        )).getText();

        assertTrue(!firstElement.isEmpty(),
                "Первый элемент не был найден:");
    }

    /**
     * Метод {@code inputSearchFirstName} ожидает появления поля поиска на странице.
     * Вводит в него наименование первого элемента.
     *
     * @return список результатов поиска на странице
     *
     * @author Наливайко Дмитрий
     */
    public void inputSearchFirstName(){
        WebElement searchField = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(
                "//div[contains(@data-zone-name, 'search_block')]//input[@id='header-search']")));
        searchField.sendKeys(firstElement);
    }

    /**
     * Метод {@code buttonSearchClick} ожидает появления кнопки поиска на странице и нажимает на неё.
     *
     * @author Наливайко Дмитрий
     */
    public void buttonSearchClick(){
        WebElement searchButton = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(
                "//div[contains(@data-zone-name, 'search_block')]//button[@data-auto='search-button']")));
        searchButton.click();
    }

    /**
     * Метод {@code searchFirstElement} получает элементы расположенные вертикально с первой страницы.
     * Проверяет, есть ли среди них элемент, который находится в строке поиска.
     * Если среди элементов не находится подходящего - получает элементы расположенные горизонтально и ищет среди них.
     *
     * @author Наливайко Дмитрий
     */
    public void searchFirstElement() {
        takeElementsOnFirstPage();
        boolean isElementFound = false;
        isElementFound = CompareElementTitles(foundElements,
                ".//div[contains(@data-baobab-name, 'title')]//span");
        if(isElementFound) {
            assertTrue(isElementFound,
                    "Первый элемент не был найден в поиске");
        }
        else{
            List<WebElement> elements = driver.findElements(By.xpath(
                    "//div[contains(@data-madv, 'show')]//li"));
            isElementFound = CompareElementTitles(elements, ".//div[@data-baobab-name='title']");
            assertTrue(isElementFound,
                    "Первый элемент не был найден в поиске");
        }
    }

    private boolean CompareElementTitles(List<WebElement> elements, String additionalXPath){
        for(WebElement element:elements){
            if(element.findElement(By.xpath(additionalXPath)).getText().equals(firstElement)){
                return true;
            }
        }
        return false;
    }



}
