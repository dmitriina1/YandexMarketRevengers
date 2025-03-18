package pages;

import org.junit.jupiter.api.Assertions;
import org.junit.platform.commons.function.Try;
import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import javax.swing.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static helpers.Assertions.assertTrue;
import static helpers.Properties.testsProperties;

public class YandexAfterSearch{
    private WebDriver driver;
    private WebElement pageTitle;
    private WebElement brandSearch;
    private WebElement brandShowMoreButton;
    private List<WebElement> searchResultsList;
    private WebDriverWait wait;
    private static String PAGE_TITLE = "//div[contains(@data-zone-name, 'searchTitle')]//h1";
    private static String PRICE_INTERVAL = "//span[contains(@data-auto, 'filter-range-%s')]//label[contains(text(), 'Цена')]/..//input";


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
        Actions actions = new Actions(driver);
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
//
//        wait.ignoring(StaleElementReferenceException.class)
//                .until(driver -> {
//                    wait.until(elementToBeClickable(driver.findElement(By.xpath("//div[contains(@data-auto, 'SerpList')]" +
//                            "//div[contains(@data-auto-themename, 'listDetailed')]"))));
//                    {
//                        boolean allChecked = true;
//                        List<WebElement> brandElements = driver.findElements(By.xpath(brandShownLocator));
//
//                        for (WebElement element : brandElements) {
//                            String text = element.getText().trim();
//                            for (String brand : brands) {
//                                if (text.equalsIgnoreCase(brand)) {
//                                    WebElement checkbox = element.findElement(By.xpath(".//label"));
//                                    wait.until(ExpectedConditions.elementToBeClickable(checkbox));
//
//                                    if (!"true".equals(checkbox.getAttribute("aria-checked"))) {
//                                        actions.moveToElement(checkbox).click().perform();
//                                        someBrands.remove(brand);
//                                        allChecked = false;
//                                    }
//                                }
//                            }
//                        }
//                        return allChecked;
//                    }
//                });

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

    private boolean isJsReady(WebDriver driver) {
        return ((JavascriptExecutor) driver)
                .executeScript("return document.readyState").equals("complete");
    }


}
