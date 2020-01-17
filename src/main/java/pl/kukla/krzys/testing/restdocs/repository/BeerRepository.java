package pl.kukla.krzys.testing.restdocs.repository;

import org.springframework.data.repository.PagingAndSortingRepository;
import pl.kukla.krzys.testing.restdocs.domain.Beer;

import java.util.UUID;

/**
 * @author Krzysztof Kukla
 */
public interface BeerRepository extends PagingAndSortingRepository<Beer, UUID> {
}
