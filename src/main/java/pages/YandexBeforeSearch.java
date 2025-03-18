package pages;

import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.WebDriverWait;

import static helpers.Properties.testsProperties;
import static org.openqa.selenium.support.ui.ExpectedConditions.visibilityOfElementLocated;

public class YandexBeforeSearch {
    private WebDriver driver;
    private WebElement catalogContent;
    private WebElement catalogSubItem;
    private WebElement catalogButton;
    private WebDriverWait wait;

    public YandexBeforeSearch(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, testsProperties.defaultTimeout());
    }

    public void catalogButtonClick() {
        wait.until(this::isJsReady);
        wait.until(visibilityOfElementLocated(By.xpath("//div[(@data-baobab-name='catalog')]")));
        this.catalogButton = driver.findElement(By.xpath("//div[(@data-baobab-name='catalog')]"));
        catalogButton.click();
    }

    public void catalogContentMouseOver(String catalogContent) {
        wait.until(this::isJsReady);
        wait.until(visibilityOfElementLocated(By.xpath(String.format("//div[contains(@data-zone-name, 'catalog-content')]//ul//a//span[contains(text(), '%s')]", catalogContent))));
        this.catalogContent = driver.findElement(By.xpath(String.format("//div[contains(@data-zone-name, 'catalog-content')]//ul//a//span[contains(text(), '%s')]", catalogContent)));
        Actions actions = new Actions(driver);
        actions.moveToElement(this.catalogContent).perform();
    }

    public void catalogSubItemClick(String subItem) {
        wait.until(visibilityOfElementLocated(By.xpath(String.format("//li//a[contains(text(), '%s')]", subItem))));
        this.catalogSubItem = driver.findElement(By.xpath(String.format("//li//a[contains(text(), '%s')]", subItem)));
        catalogSubItem.click();
    }

    private boolean isJsReady(WebDriver driver) {
        return ((JavascriptExecutor) driver)
                .executeScript("return document.readyState").equals("complete");
    }

}
