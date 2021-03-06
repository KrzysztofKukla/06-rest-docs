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
import org.springframework.restdocs.constraints.ConstraintDescriptions;
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.payload.FieldDescriptor;
import org.springframework.restdocs.payload.PayloadDocumentation;
import org.springframework.restdocs.request.RequestDocumentation;
import org.springframework.restdocs.snippet.Attributes;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.util.StringUtils;
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
//this makes autoconfiguration REST docs for us
@ExtendWith(value = RestDocumentationExtension.class) //we need to add SpringExtension as well, but this is done int @WebMvcTest
//here we change default configuration for @AutoConfigureRestDocs
@AutoConfigureRestDocs(uriScheme = "https", uriHost = "dev.someserver", uriPort = 80)
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
            //this controller does not take any parameter, but for our goal - to demonstrate we adding 'isCold' parameter
            .param("isCold", "yes")
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            //document needs to have unique name, otherwise will be override
            .andDo(MockMvcRestDocumentation.document("v1/beer-get",
                RequestDocumentation.pathParameters(
                    RequestDocumentation.parameterWithName("beerId").description("UUID of desired beer to get") //this 'beerId' need to be included
                    // in uri "/api/v1/beer/{beerId}/"
                ),
                RequestDocumentation.requestParameters(
                    RequestDocumentation.parameterWithName("isCold").description("Is beer cold query param?")
                ),
                //in response we will receive whole BeerDto object which contains id, version, createdDate etc. parameters
                //all fields need to be specifiy - to make this test passed, we need to specify all parameters for BeerDto
                PayloadDocumentation.responseFields(
                    PayloadDocumentation.fieldWithPath("id").description("Id of beer"),
                    PayloadDocumentation.fieldWithPath("version").description("version of beer"),
                    PayloadDocumentation.fieldWithPath("createdDate").description("createdDate of beer"),
                    PayloadDocumentation.fieldWithPath("lastModifiedDate").description("lastModifiedDate of beer"),
                    PayloadDocumentation.fieldWithPath("beerName").description("beerName of beer"),
                    PayloadDocumentation.fieldWithPath("beerStyle").description("beerStyle of beer"),
                    PayloadDocumentation.fieldWithPath("upc").description("upc of beer"),
                    PayloadDocumentation.fieldWithPath("price").description("price of beer"),
                    PayloadDocumentation.fieldWithPath("quantityOnHand").description("quantityOnHand of beer")
                )
                )
            );

    }

    @Test
    void saveNewBeer() throws Exception {
        BeerDto beerDto = createBeerDto();
        String beerDtoJson = objectMapper.writeValueAsString(beerDto);

        //'constraints' field from 'test/resources/org/springframework/restdocs/templates/request-fields.snippet' file
        //this 'constraints' have to be configured since we added 'constraints' into 'request-fields.snippet' file
        ConstrainedFields fields = new ConstrainedFields(BeerDto.class);

        mockMvc.perform(RestDocumentationRequestBuilders.post("/api/v1/beer/")
            .contentType(MediaType.APPLICATION_JSON)
            .content(beerDtoJson))
            .andExpect(status().isCreated())
            //document needs to have unique name, otherwise will be override
            .andDo(MockMvcRestDocumentation.document("v1/beer-new",
                PayloadDocumentation.requestFields(
                    //and again we need to pass all required fields for BeerDto or explicitly specify field to be ignored
                    //four fields are specified as ignored, API users should never send those
                    // others are modifiable and should be passed
                    fields.withPath("id").ignored(),
                    fields.withPath("version").ignored(),
                    fields.withPath("createdDate").ignored(),
                    fields.withPath("lastModifiedDate").ignored(),
                    fields.withPath("beerName").description("beerName of beer"),
                    fields.withPath("beerStyle").description("beerStyle of beer"),
                    fields.withPath("upc").description("upc of beer").attributes(),
                    fields.withPath("price").description("price of beer"),
                    //this will be maintained by backend system API
                    fields.withPath("quantityOnHand").ignored()
                )
                )
            );

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

    //we added 'constraints' field to 'test/resources/org/springframework/restdocs/templates/request-fields.snippet' file
    //so we need to specify that field here
    private static class ConstrainedFields {

        private final ConstraintDescriptions constraintDescriptions;

        ConstrainedFields(Class<?> input) {
            this.constraintDescriptions = new ConstraintDescriptions(input);
        }

        private FieldDescriptor withPath(String path) {
            return PayloadDocumentation.fieldWithPath(path).attributes(Attributes.key("constraints").value(StringUtils
                .collectionToDelimitedString(this.constraintDescriptions
                    .descriptionsForProperty(path), ". ")));
        }

    }

}