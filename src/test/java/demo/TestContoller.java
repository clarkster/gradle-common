package demo;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.request;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

import com.demo.error.OrionErrorController;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpMethod;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
public class TestContoller {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();
    private MockMvc mockMvc;

    @Before
    public void setup() {
        mockMvc = standaloneSetup(new Controller())
                .setControllerAdvice(new OrionErrorController(null, null))
                .build();
    }

    @Test
    public void okResponse() throws Exception {
        mockMvc.perform(request(HttpMethod.GET, "/greet/{me}", "me"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.Hello", equalTo("me")));
    }

    @Test
    public void badResponse() throws Exception {
        mockMvc.perform(request(HttpMethod.GET, "/greet/{me}", "bad"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[0].message", equalTo("Bad user")));
    }
}
