package com.demo.error;

import org.springframework.boot.autoconfigure.web.ErrorAttributes;
import org.springframework.boot.autoconfigure.web.ErrorController;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Collections;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

/**
 * Overwrite Spring's BasicErrorController implementation with something that structures the Error response object
 * into one of our choosing.
 */
@Controller
@ControllerAdvice
@RequestMapping("${server.error.path:${error.path:/error}}")
public class OrionErrorController implements ErrorController {

    private final ErrorAttributes errorAttributes;
    private final ServerProperties serverProperties;

    public OrionErrorController(ServerProperties serverProperties, ErrorAttributes errorAttributes) {
        this.errorAttributes = errorAttributes;
        this.serverProperties = serverProperties;
    }

    @Override
    public String getErrorPath() {
        return serverProperties.getError().getPath();
    }


    @RequestMapping
    @ResponseBody
    public ErrorMessage handleException(HttpServletRequest request) {
        RequestAttributes requestAttributes = new ServletRequestAttributes(request);
        return defaultClientExcepion(requestAttributes);
    }

    private ErrorMessage defaultClientExcepion(RequestAttributes requestAttributes) {
        Map<String, Object> props = this.errorAttributes.getErrorAttributes(requestAttributes, false);
        return new ErrorMessage(Collections.singletonList(new Error((String) props.get("error"),
                                                                    (String) props.get("message"))));
    }

    @ExceptionHandler(OrionException.class)
    public ResponseEntity<ErrorMessage> handleOrionException(OrionException exception) {
        return new ResponseEntity<>(exception.getErrors(), exception.getHttpStatus());
    }

}
