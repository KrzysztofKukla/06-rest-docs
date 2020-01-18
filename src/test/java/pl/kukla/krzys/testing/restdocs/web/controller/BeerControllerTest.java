package pl.kukla.krzys.testing.restdocs.web.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.request.RequestDocumentation;
import org.springframework.test.web.servlet.MockMvc;
import pl.kukla.krzys.testing.restdocs.domain.Beer;
import pl.kukla.krzys.testing.restdocs.repository.BeerRepository;
import pl.kukla.krzys.testing.restdocs.web.model.BeerDto;
import pl.kukla.krzys.testing.restdocs.web.model.BeerStyleEnum;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @author Krzysztof Kukla
 */
//autoconfiguration REST docs for us
@ExtendWith(value = RestDocumentationExtension.class) //we need to add SpringExtension as well, but this is done int @WebMvcTest
@AutoConfigureRestDocs
@WebMvcTest(controllers = BeerController.class)
@ComponentScan(value = "pl.kukla.krzys.testing.restdocs.web.mapper")
class BeerControllerTest {

    @Autowired
    private ObjectMapper objectMapper;

    //here we are mocking repository, because we don't want to bring up database layer
    @MockBean
    private BeerRepository beerRepository;

    @Autowired
    private MockMvc mockMvc;

    @Test
    void getBeerById() throws Exception {
        Beer beer = Beer.builder().build();
        BDDMockito.given(beerRepository.findById(any(UUID.class))).willReturn(Optional.of(beer));

        mockMvc.perform(RestDocumentationRequestBuilders.get("/api/v1/beer/{beerId}/", UUID.randomUUID().toString())
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andDo(MockMvcRestDocumentation.document("v1/beer",
                RequestDocumentation.pathParameters(RequestDocumentation.parameterWithName("beerId").description("UUID of desired beer to get"))));

    }

    @Test
    void saveNewBeer() throws Exception {
        BeerDto beerDto = createBeerDto();

        String beerDtoJson = objectMapper.writeValueAsString(beerDto);

        mockMvc.perform(RestDocumentationRequestBuilders.post("/api/v1/beer/")
            .contentType(MediaType.APPLICATION_JSON)
            .content(beerDtoJson))
            .andExpect(status().isCreated());

    }

    @Test
    void updateBeerById() throws Exception {
        BeerDto beerDto = createBeerDto();

        String beerDtoJson = objectMapper.writeValueAsString(beerDto);

        mockMvc.perform(RestDocumentationRequestBuilders.put("/api/v1/beer/" + UUID.randomUUID())
            .contentType(MediaType.APPLICATION_JSON)
            .content(beerDtoJson))
            .andExpect(status().isNoContent());
    }

    private BeerDto createBeerDto() {
        return BeerDto.builder()
            .beerName("beer name")
            .beerStyle(BeerStyleEnum.LAGER)
            .price(new BigDecimal("9.99"))
            .upc(12345678L)
            .build();
    }

}