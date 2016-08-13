package demo;

import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static demo.ExpectedResponseCodeMatcher.httpError;
import static org.assertj.core.api.Assertions.assertThat;

import com.github.tomakehurst.wiremock.junit.WireMockRule;

import org.json.JSONObject;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.springframework.boot.context.embedded.LocalServerPort;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.Map;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class,
                properties = "proxy.url=http://localhost:9090/downstream",
                webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class ControllerTest {

    @Rule
    public WireMockRule wireMockRule = new WireMockRule(
            wireMockConfig()
                    .withRootDirectory("src/integration/resources/wiremock")
                    .port(9090));

    @LocalServerPort
    public int serverPort;

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    private RestTemplate restTemplate;

    @Before
    public void setup() {
        restTemplate = new RestTemplate();
        restTemplate.setInterceptors(
                Collections.singletonList(new SwaggerValidatingInterceptor("./example-swagger.yml")));
    }

    @Test
    public final void checkGreeting() throws Exception {
        restTemplate.getForObject("http://localhost:" + serverPort + "/greet/{user}", JSONObject.class, "me");
    }

    @Test
    public final void checkBadRequest() throws Exception {
        expectedException.expect(httpError(HttpStatus.BAD_REQUEST));

        restTemplate.getForObject("http://localhost:" + serverPort + "/greet/{user}", String.class, "bad");
    }

    @Test
    public final void checkProxyToWiremock() throws Exception {
        Map res = restTemplate.getForObject("http://localhost:" + serverPort + "/proxy", Map.class);
        assertThat(res).containsEntry("Hello", "fromDownstream" );
    }

}
