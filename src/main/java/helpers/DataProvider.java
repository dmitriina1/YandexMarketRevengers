package helpers;

import net.bytebuddy.asm.Advice;
import org.junit.jupiter.params.provider.Arguments;

import java.util.List;
import java.util.stream.Stream;

public class DataProvider {

    public static Stream<Arguments> providerCheckingMarket(){
        List<String> brandsNames = List.of("Lenovo", "HP");
        return Stream.of(
                Arguments.of("Яндекс Маркет", "Электроника", "Ноутбуки", "10000", "30000", brandsNames, 12)
        );
    }
}
