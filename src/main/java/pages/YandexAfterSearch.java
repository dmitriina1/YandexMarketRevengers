package pages;

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

public class YandexAfterSearch{
    private WebDriver driver;
    private WebElement pageTitle;
    private WebElement brandSearch;
    private WebElement brandShowMoreButton;
    private List<WebElement> searchResultsList;
    private String firstElement;
    private WebDriverWait wait;
    private static String PAGE_TITLE = "//div[contains(@data-zone-name, 'searchTitle')]//h1";
    private static String PRICE_INTERVAL = "//span[contains(@data-auto, 'filter-range-%s')]//label[contains(text(), 'Цена')]/..//input";
    List<WebElement> foundElements;
    Actions actions;

    public YandexAfterSearch(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, testsProperties.defaultTimeout());
        this.searchResultsList = new ArrayList<>();
    }

    public void checkingTitleByText(String expectedTitle) {
        pageTitle = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(PAGE_TITLE)));

        assertTrue(pageTitle.getText().contains(expectedTitle),
                "Заголовок страницы не содержит ожидаемый текст.\n" +
                        "Ожидалось: '" + pageTitle.getText() + "'\n" +
                        "Фактический заголовок: '" + expectedTitle + "'");
    }

    public void inputPriceInterval(String minimum, String maximum) {
        wait.until(this::isJsReady);
        setPrice("min", minimum);
        setPrice("max", maximum);
    }

    private void setPrice(String priceBorder, String priceValue){
        WebElement priceField = wait.until(ExpectedConditions.visibilityOf((
                driver.findElement(By.xpath(String.format(PRICE_INTERVAL, priceBorder)))
        )));
        priceField.clear();
        priceField.click();
        priceField.sendKeys(priceValue + Keys.ENTER);
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[@data-auto = 'SerpStatic-loader']")));
        wait.until(ExpectedConditions.invisibilityOf(driver.findElement(By.xpath("//div[@data-auto = 'SerpStatic-loader']"))));
    }

    public void inputBrands(List<String> brands) {
        String brandSearchLocator = "//div[contains(@data-zone-data, 'Бренд')]//input";
        String brandShownLocator = "//div[contains(@data-zone-data, 'Бренд')]//div[contains(@data-zone-name, 'FilterValue')]";
        String brandShownLocatorAdditional = "//div[contains(@data-zone-data, 'Бренд')]//div[contains(@data-zone-name, 'FilterValue') " +
                "and not (contains(@data-baobab-name, 'showMoreFilters'))]";
        List<String> tempBrands = new ArrayList<>(brands);
        actions = new Actions(driver);
        Set<WebElement> brandElements = new HashSet<>();
        long startTime = System.currentTimeMillis();
        long timeout = 10000;
        while (brandElements.size() <= 5) {
            if (System.currentTimeMillis() - startTime > timeout) {
                System.out.println("Время ожидания истекло, выходим из цикла.");
                break;
            }

            List<WebElement> currentBrandElements = driver.findElements(By.xpath(brandShownLocatorAdditional));
            brandElements.addAll(currentBrandElements);

            for (WebElement element : currentBrandElements) {
                try {
                    String text = element.getText().trim();
                    for (String brand : brands) {
                        if (text.equalsIgnoreCase(brand)) {
                            WebElement checkbox = element.findElement(By.xpath(".//label"));
                            wait.until(ExpectedConditions.elementToBeClickable(checkbox));

                            if (!"true".equals(checkbox.getAttribute("aria-checked"))) {
                                actions.moveToElement(checkbox).click().perform();
                                wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[@data-auto = 'SerpStatic-loader']")));
                                wait.until(ExpectedConditions.invisibilityOf(driver.findElement(By.xpath("//div[@data-auto = 'SerpStatic-loader']"))));
                                tempBrands.remove(brand);
                            }
                        }
                    }
                } catch (StaleElementReferenceException e) {
                    System.out.println("Элемент устарел: " + element);
                }
            }
        }
        if (!tempBrands.isEmpty()) {
            try {
                Thread.sleep(1000);
            }catch (Exception exception){}
            inputBrandsShowMore(brandShownLocator, brandSearchLocator, tempBrands);
        }
    }

    public void inputBrandsShowMore(String brandShownLocator, String brandSearchLocator, List<String> tempBrands) {
        String additionalLocator = "[contains(@data-baobab-name, 'showMoreFilters')]";
        this.brandShowMoreButton = wait.until(ExpectedConditions.visibilityOf(driver.findElement(By.xpath(
                brandShownLocator + additionalLocator))));
        brandShowMoreButton.click();
        for (String element : tempBrands) {
            this.brandSearch = wait.until(ExpectedConditions.visibilityOf(driver.findElement(By.xpath(brandSearchLocator))));
            brandSearch.clear();
            brandSearch.click();
            brandSearch.sendKeys(element);
            By elementLocator = By.xpath(brandShownLocator +
                    "//span[(text()='" + element + "' )]");
            WebElement brandObject = wait.until(ExpectedConditions.elementToBeClickable(driver.findElement(elementLocator)));
            brandObject.click();
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[@data-auto = 'SerpStatic-loader']")));
            wait.until(ExpectedConditions.invisibilityOf(driver.findElement(By.xpath("//div[@data-auto = 'SerpStatic-loader']"))));
        }
    }

    public void takeElementsOnFirstPage() {
        wait.until(this::isJsReady);
        foundElements = new ArrayList<>();
        actions = new Actions(driver);
        long startTime = System.currentTimeMillis();
        long maxTime = 60000;
        while ((System.currentTimeMillis() - startTime) < maxTime) {
            List<WebElement> elements = driver.findElements(By.xpath("//div[contains(@data-auto, 'SerpList')]//div[contains(@data-auto-themename, 'listDetailed')]"));
            foundElements.addAll(elements);
            if (!driver.findElements(By.xpath("//div[contains(@data-baobab-name, 'pager')]")).isEmpty()) {
                break;
            }
            actions.sendKeys(org.openqa.selenium.Keys.PAGE_DOWN).perform();
        }
    }

    public void checkCountElementsOnFirstPage(int expectedElementsCount){
        takeElementsOnFirstPage();
        assertTrue(foundElements.size() > expectedElementsCount,
                "Количество элементов на первой странице меньше " + expectedElementsCount);
    }

    public void countOfElementsOnAllPages() {
        List<WebElement> foundElements = new ArrayList<>();
        actions = new Actions(driver);
        long startTime = System.currentTimeMillis();
        long timeLimit = 60000;

        while (System.currentTimeMillis() - startTime < timeLimit) {
            if (isLastPage()) {
                foundElements.addAll(driver.findElements(By.xpath("//div[contains(@data-auto, 'SerpList')]//div[contains(@data-auto-themename, 'listDetailed')]")));
                break;
            } else {
                actions.sendKeys(Keys.PAGE_DOWN).perform();
                try {
                    //  wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//div[contains(@data-auto, 'SerpList')]")));
                    wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[contains(@data-auto, 'SerpList')]//div[contains(@data-auto-themename, 'listDetailed')]")));
                } catch (TimeoutException e) {
                    System.out.println("Элемент не появился вовремя.");
                }
            }
        }

        searchResultsList = foundElements;
    }

    private boolean isLastPage() {
        return driver.findElements(By.xpath("//div[contains(@data-baobab-name, 'pager')]//div[contains(@data-baobab-name, 'next')]")).isEmpty();
    }

    public boolean checkFilters(String minimum, String maximum, List<String> namesOfBrand) {
        countOfElementsOnAllPages();
        double min = Double.parseDouble(minimum);
        double max = Double.parseDouble(maximum);
        List<WebElement> filteredProducts = filterProducts(min, max, namesOfBrand);

        List<WebElement> remainingProducts = searchResultsList.stream()
                .filter(product -> !filteredProducts.contains(product))
                .collect(Collectors.toList());

        checkBrandInCard(remainingProducts, namesOfBrand, filteredProducts);

        System.out.println("---------------------------------------------------------");
        System.out.println(filteredProducts.size());
        System.out.println(searchResultsList.size());
        return filteredProducts.size() == searchResultsList.size();
    }

    private List<WebElement> filterProducts(Double minimum, Double maximum, List<String> namesOfBrand) {
        return searchResultsList.stream()
                .filter(product -> {
                    String titleText = product.findElement(By.xpath(".//div[contains(@data-baobab-name, 'title')]//span")).getText().toLowerCase();
                    boolean isBrandMatch = namesOfBrand.stream().map(String::toLowerCase).anyMatch(titleText::contains);
                    double price = getPrice(product);
                    boolean isPriceInRange = price > minimum && price < maximum;
                    return isBrandMatch && isPriceInRange;
                })
                .collect(Collectors.toList());
    }

    private double getPrice(WebElement product) {
        String priceText = product.findElement(By.xpath(".//span[contains(@data-auto, 'snippet-price-current')]//*[1][name()='span']")).getText().replaceAll("[^\\d.]", "");
        return Double.parseDouble(priceText);
    }

    private void checkBrandInCard(List<WebElement> remainingProducts, List<String> namesOfBrand, List<WebElement> filteredProducts) {
        for (WebElement element : remainingProducts) {
            element.click();
            wait.until(this::isJsReady);
            ArrayList<String> tabs = new ArrayList<>(driver.getWindowHandles());
            driver.switchTo().window(tabs.get(tabs.size() - 1));

            List<WebElement> brandElements = driver.findElements(By.xpath("//div[contains(@data-zone-name, 'fullSpecs')]//div[contains(@aria-label, 'Характеристики')]//span[text()='Бренд']/../.. /following-sibling::div[1]//span"));
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

    public void saveFirstObject() {
        long startTime = System.currentTimeMillis();
        final long timeLimit = 60000;
        boolean reachedTop = false;
        WebElement body = driver.findElement(By.tagName("body"));
        actions = new Actions(driver);

        while (System.currentTimeMillis() - startTime < timeLimit && !reachedTop) {
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
        firstElement = driver.findElement(By.xpath("//div[contains(@data-auto, 'SerpList')]//div[contains(@data-auto-themename, 'listDetailed')]//span[@data-auto = 'snippet-title']")).getText();
    }

    public void inputSearchFirstName(){
        WebElement searchField = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[contains(@data-zone-name, 'search_block')]//input[@id='header-search']")));
        searchField.sendKeys(firstElement);
    }

    public void buttonSearchClick(){
        WebElement searchButton = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[contains(@data-zone-name, 'search_block')]//button[@data-auto='search-button']")));
        searchButton.click();
    }

    public void searchFirstElement() {
        takeElementsOnFirstPage();
        for(WebElement element:foundElements){
            if(element.findElement(By.xpath(".//div[contains(@data-baobab-name, 'title')]//span")).equals(firstElement)){
                System.out.println("Completed!");
                break;
            }
        }
    }

    private boolean isJsReady(WebDriver driver) {
        return ((JavascriptExecutor) driver)
                .executeScript("return document.readyState").equals("complete");
    }


}
