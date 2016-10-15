package com.vmware.pso.samples.services.reservation.handler;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.vmware.pso.samples.core.dto.ErrorDto;

@ControllerAdvice
public class GlobalDefaultExceptionHandler {

    public static final String DEFAULT_ERROR_CHANNEL = "/services/errors";

    @Autowired
    @Qualifier("errorRedisTemplate")
    private RedisTemplate<String, ErrorDto> errorRedisTemplate;

    @ExceptionHandler(value = Exception.class)
    public @ResponseBody ErrorDto defaultErrorHandler(final HttpServletRequest req, final Exception e)
            throws Exception {

        final ErrorDto errorDto = new ErrorDto();
        // check for different types of exceptions to add to errors list
        errorDto.setCode(HttpStatus.INTERNAL_SERVER_ERROR.toString());
        errorDto.setMessage(StringUtils.isBlank(e.getMessage()) ? e.getClass().getName() : e.getMessage());

        // message error service of error
        errorRedisTemplate.convertAndSend(DEFAULT_ERROR_CHANNEL, errorDto);

        // If the exception is annotated with @ResponseStatus rethrow it and let
        // the framework handle it - like the OrderNotFoundException example
        // at the start of this post.
        // AnnotationUtils is a Spring Framework utility class.
        if (AnnotationUtils.findAnnotation(e.getClass(), ResponseStatus.class) != null) {
            throw e;
        }

        return errorDto;
    }
}