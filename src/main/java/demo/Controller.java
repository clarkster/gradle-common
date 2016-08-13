package demo;

import com.demo.error.OrionException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.Map;

@RestController
public class Controller {

    @Value("${proxy.url}")
    private String proxyUrl;

    @RequestMapping("/greet/{user}")
    public Map<String, String> greet(@PathVariable("user") String user) {
        if ("bad".equals(user)) {
            throw new OrionException(HttpStatus.BAD_REQUEST, "Bad user");
        }
        return Collections.singletonMap("Hello", user);
    }

    @RequestMapping("/proxy")
    public Object proxy() {
        return new RestTemplate().getForObject(proxyUrl, Object.class);
    }

}
