package demo;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;

public class ExpectedResponseCodeMatcher extends BaseMatcher {
    private final HttpStatus expectedCode;

    private ExpectedResponseCodeMatcher(HttpStatus expectedCode) {
        this.expectedCode = expectedCode;
    }

    @Override
    public boolean matches(Object item) {
        if (!(item instanceof HttpClientErrorException)) {
            return false;
        }
        return ((HttpClientErrorException)item).getStatusCode().equals(expectedCode);
    }

    @Override
    public void describeTo(Description description) {
        description.appendText("expectedCode(").appendValue(expectedCode).appendText(")");
    }

    public static ExpectedResponseCodeMatcher httpError(HttpStatus expectedCode) {
        return new ExpectedResponseCodeMatcher(expectedCode);
    }
}
