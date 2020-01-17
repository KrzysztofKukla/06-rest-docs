package pl.kukla.krzys.testing.restdocs.web.mapper;

import org.mapstruct.Mapper;
import pl.kukla.krzys.testing.restdocs.domain.Beer;
import pl.kukla.krzys.testing.restdocs.web.model.BeerDto;

/**
 * @author Krzysztof Kukla
 */
//this is standard Mapstruct Mapper that provides implementation for converting Beer to BeerDto and reverse
    //DateMapper class provide special methods for dates, because in database we use Java Sql Timestamps
    // but on web we use offset date in time so we have to convert between these
@Mapper(uses = {DateMapper.class})
public interface BeerMapper {

    BeerDto BeerToBeerDto(Beer beer);

    Beer BeerDtoToBeer(BeerDto dto);
}
