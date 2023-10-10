package ru.skillbox.post.api;

import feign.Feign;
import feign.FeignException;
import feign.Logger;
import feign.RetryableException;
import feign.codec.ErrorDecoder;
import feign.optionals.OptionalDecoder;
import feign.slf4j.Slf4jLogger;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.cloud.openfeign.support.*;
import org.springframework.stereotype.Component;

import static feign.FeignException.errorStatus;

@Component
@RequiredArgsConstructor
public class FeignClientFactory {
    private final ObjectFactory<HttpMessageConverters> messageConverters = HttpMessageConverters::new;

    public <T> T newFeignClient(Class<T> requiredType, String url){
        return Feign.builder()
                .encoder(new SpringEncoder(this.messageConverters))
                .decoder(new OptionalDecoder(new ResponseEntityDecoder(new SpringDecoder(this.messageConverters))))
                .errorDecoder(errorDecoder())
                .logger(new Slf4jLogger(requiredType))
                .logLevel(Logger.Level.FULL)
                .contract(new SpringMvcContract())
                .target(requiredType,url);
    }


    private ErrorDecoder errorDecoder() {
        return (methodKey, response) -> {
            FeignException exception = errorStatus(methodKey, response);
            if (exception.status() == 500 || exception.status() == 503) {
                return new RetryableException(
                        response.status(),
                        exception.getMessage(),
                        response.request().httpMethod(),
                        exception,
                        null,
                        response.request());
            }
            return exception;
        };
    }
}
