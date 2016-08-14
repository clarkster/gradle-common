package demo;

import com.atlassian.oai.validator.SwaggerRequestResponseValidator;
import com.atlassian.oai.validator.model.Request;
import com.atlassian.oai.validator.model.Response;
import com.atlassian.oai.validator.model.SimpleRequest;
import com.atlassian.oai.validator.model.SimpleResponse;
import com.atlassian.oai.validator.report.ValidationReport;
import com.atlassian.oai.validator.report.ValidationReportFormatter;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.util.StreamUtils;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.IOException;
import java.nio.charset.Charset;

public class SwaggerValidatingInterceptor implements ClientHttpRequestInterceptor {

    private final SwaggerRequestResponseValidator validator;

    public SwaggerValidatingInterceptor(String swaggerUrl) {
        this.validator = new SwaggerRequestResponseValidator(swaggerUrl);

    }

    private Request.Method translateMethod(HttpMethod method) {
        return Request.Method.valueOf(method.toString());
    }

    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution)
            throws IOException {

        Request swaggerRequest = new SimpleRequest.Builder(translateMethod(request.getMethod()),
                                                           request.getURI().getPath()).build();

        ClientHttpResponse httpResponse = new BufferingClientHttpResponseWrapper(execution.execute(request, body));

        Response swaggerResponse = SimpleResponse.Builder
                .status(httpResponse.getRawStatusCode())
                .withBody(StreamUtils.copyToString(httpResponse.getBody(), getCharset(httpResponse)))
                .build();

        ValidationReport report = validator.validate(swaggerRequest, swaggerResponse);

        if (report.hasErrors()) {
            throw new IllegalStateException(ValidationReportFormatter.format(report));
        }
        return httpResponse;
    }

    private Charset getCharset(ClientHttpResponse response) {
        HttpHeaders headers = response.getHeaders();
        MediaType contentType = headers.getContentType();
        return contentType != null ? contentType.getCharset() : Charset.forName("utf-8");
    }


    /**
     * Allows the response to be read more than once without consuming the output stream.
     */
    static final class BufferingClientHttpResponseWrapper implements ClientHttpResponse {

        private final ClientHttpResponse response;

        private byte[] body;


        BufferingClientHttpResponseWrapper(ClientHttpResponse response) {
            this.response = response;
        }


        @Override
        public HttpStatus getStatusCode() throws IOException {
            return this.response.getStatusCode();
        }

        @Override
        public int getRawStatusCode() throws IOException {
            return this.response.getRawStatusCode();
        }

        @Override
        public String getStatusText() throws IOException {
            return this.response.getStatusText();
        }

        @Override
        public HttpHeaders getHeaders() {
            return this.response.getHeaders();
        }

        @Override
        public InputStream getBody() throws IOException {
            if (this.body == null) {
                this.body = StreamUtils.copyToByteArray(this.response.getBody());
            }
            return new ByteArrayInputStream(this.body);
        }

        @Override
        public void close() {
            this.response.close();
        }

    }

}
