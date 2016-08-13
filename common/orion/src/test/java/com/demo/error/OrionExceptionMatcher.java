package com.demo.error;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.springframework.http.HttpStatus;

public class OrionExceptionMatcher extends BaseMatcher<OrionException> {

    private final HttpStatus expectedCode;

    private OrionExceptionMatcher(HttpStatus expectedCode) {
        this.expectedCode = expectedCode;
    }

    @Override
    public boolean matches(Object item) {
        if (!(item instanceof OrionException)) {
            return false;
        }
        return ((OrionException)item).getHttpStatus().equals(expectedCode);
    }

    @Override
    public void describeTo(Description description) {
        description.appendText("expectedCode(").appendValue(expectedCode).appendText(")");
    }

    public static OrionExceptionMatcher codeMatches(HttpStatus expectedCode) {
        return new OrionExceptionMatcher(expectedCode);
    }
}
