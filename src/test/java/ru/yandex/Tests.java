package ru.yandex;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;

import static helpers.Properties.testsProperties;
import static steps.StepsAll.*;

public class Tests extends BaseTest{

    @DisplayName("Проверка открытия сайта ЯндексМаркета")
    @ParameterizedTest(name="{displayName}: {arguments}")
    @MethodSource("helpers.DataProvider#providerCheckingMarket")
    public void testYandexMarket(String siteTitle,String catalogContent, String catalogSubItem, String minimumPrice, String maximumPrice, List<String> brandList) {
        openSite(testsProperties.yandexMarketUrl(), siteTitle, chromeDriver);
        findCatalog();
        catalogContentMouseOver(catalogContent);
        goToСatalogSubItem(catalogSubItem);
        checkingCatalogSubItemTitle(catalogSubItem);
        inputPriceFilter(minimumPrice, maximumPrice);
        inputBrandsFilter(brandList);
    }
}
//, "DELL", "BlackView", "azerty", "prestigio"


