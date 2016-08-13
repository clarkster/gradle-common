package com.demo.error;

import static org.assertj.core.api.Assertions.assertThat;

import com.demo.OrionAutoConfiguration;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment= SpringBootTest.WebEnvironment.RANDOM_PORT)
public class OrionErrorControllerTest {

    @RestController
    public static class TestRestController {
        @RequestMapping("/")
        public String greet() {
            return "Hello";
        }

        @RequestMapping(path="/wrapErr", method=RequestMethod.GET)
        public String err() {
            throw new OrionException(HttpStatus.I_AM_A_TEAPOT, "Short and stout");
        }
    }

    /**
     * Test configuration - skip the main autoconfiguration and wire the classes within this text fixture instead.
     */
    @SpringBootApplication(exclude = OrionAutoConfiguration.class)
    public static class TestConfig {
        @Bean
        public TestRestController controller() {
            return new TestRestController();
        }
    }

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    public void okResponse() {
        assertThat(this.restTemplate.getForObject("/", String.class))
                .isEqualTo("Hello");
    }

    @Test
    public void notFoundResponseCode() {
        assertThat(this.restTemplate.getForEntity("/blah", ErrorMessage.class).getStatusCode())
                .isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    public void notFoundResponseObject() {
        ErrorMessage message = this.restTemplate.getForObject("/blah", ErrorMessage.class);
        assertThat(message.getErrors().size()).isEqualTo(1);
        assertThat(message.getErrors().get(0).getKey()).isEqualTo("Not Found");
        assertThat(message.getErrors().get(0).getMessage()).isEqualTo("No message available");
    }

    @Test
    public void notPermittedResponseCode() {
        assertThat(this.restTemplate.postForEntity("/wrapErr", new ErrorMessage("", ""), ErrorMessage.class).getStatusCode())
                .isEqualTo(HttpStatus.METHOD_NOT_ALLOWED);
    }

    @Test
    public void notPermittedResponseObject() {
        ErrorMessage message = this.restTemplate.postForObject("/wrapErr", new ErrorMessage("", ""), ErrorMessage.class);
        assertThat(message.getErrors().size()).isEqualTo(1);
        assertThat(message.getErrors().get(0).getKey()).isEqualTo("Method Not Allowed");
        assertThat(message.getErrors().get(0).getMessage()).isEqualTo("Request method 'POST' not supported");
    }

    @Test
    public void applicationResponseCode() {
        assertThat(this.restTemplate.getForEntity("/wrapErr", ErrorMessage.class).getStatusCode())
                .isEqualTo(HttpStatus.I_AM_A_TEAPOT);
    }

    @Test
    public void applicationResponseObject() {
        ErrorMessage message = this.restTemplate.getForObject("/wrapErr", ErrorMessage.class);
        assertThat(message.getErrors().size()).isEqualTo(1);
        assertThat(message.getErrors().get(0).getKey()).isEqualTo("I'm a teapot");
        assertThat(message.getErrors().get(0).getMessage()).isEqualTo("Short and stout");
    }
}
