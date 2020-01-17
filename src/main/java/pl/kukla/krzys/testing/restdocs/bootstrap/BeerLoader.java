package pl.kukla.krzys.testing.restdocs.bootstrap;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import pl.kukla.krzys.testing.restdocs.domain.Beer;
import pl.kukla.krzys.testing.restdocs.repository.BeerRepository;

import java.math.BigDecimal;

/**
 * @author Krzysztof Kukla
 */
@Component
@RequiredArgsConstructor
public class BeerLoader implements CommandLineRunner {

    private final BeerRepository beerRepository;

    @Override
    public void run(String... args) throws Exception {
        loadBeerObjects();
    }

    private void loadBeerObjects() {
        if (beerRepository.count() == 0) {

            beerRepository.save(Beer.builder()
                .beerName("Mango Bobs")
                .beerStyle("IPA")
                .quantityToBrew(200)
                .minOnHand(12)
                .upc(337010000001L)
                .price(new BigDecimal("12.95"))
                .build());

            beerRepository.save(Beer.builder()
                .beerName("Galaxy Cat")
                .beerStyle("PALE_ALE")
                .quantityToBrew(200)
                .minOnHand(12)
                .upc(337010000002L)
                .price(new BigDecimal("11.95"))
                .build());
        }
    }
}
