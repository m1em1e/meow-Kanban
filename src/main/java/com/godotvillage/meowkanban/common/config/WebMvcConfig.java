package com.godotvillage.meowkanban.common.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Override
    public void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
        MediaType jsonUtf8 = new MediaType(MediaType.APPLICATION_JSON, StandardCharsets.UTF_8);
        MediaType problemJsonUtf8 = new MediaType("application", "problem+json", StandardCharsets.UTF_8);

        converters.stream()
                .filter(MappingJackson2HttpMessageConverter.class::isInstance)
                .map(MappingJackson2HttpMessageConverter.class::cast)
                .forEach(converter -> {
                    List<MediaType> supportedMediaTypes = new ArrayList<>(converter.getSupportedMediaTypes());
                    if (!supportedMediaTypes.contains(jsonUtf8)) {
                        supportedMediaTypes.add(0, jsonUtf8);
                    }
                    if (!supportedMediaTypes.contains(problemJsonUtf8)) {
                        supportedMediaTypes.add(problemJsonUtf8);
                    }
                    converter.setSupportedMediaTypes(supportedMediaTypes);
                });
    }
}
