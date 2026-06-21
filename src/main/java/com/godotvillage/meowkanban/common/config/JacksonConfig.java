package com.godotvillage.meowkanban.common.config;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalTimeSerializer;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.TimeZone;

@Configuration
public class JacksonConfig {

    private static final String DATE_TIME_PATTERN = "yyyy-MM-dd HH:mm:ss";
    private static final String DATE_PATTERN = "yyyy-MM-dd";
    private static final String TIME_PATTERN = "HH:mm:ss";
    private static final String DEFAULT_TIME_ZONE = "Asia/Shanghai";

    @Bean
    public Jackson2ObjectMapperBuilderCustomizer jacksonObjectMapperBuilderCustomizer() {
        return builder -> builder
                .timeZone(TimeZone.getTimeZone(DEFAULT_TIME_ZONE))
                .simpleDateFormat(DATE_TIME_PATTERN)
                .featuresToDisable(
                        SerializationFeature.WRITE_DATES_AS_TIMESTAMPS,
                        DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES
                )
                .serializerByType(Long.class, ToStringSerializer.instance)
                .serializerByType(Long.TYPE, ToStringSerializer.instance)
                .serializerByType(
                        LocalDateTime.class,
                        new LocalDateTimeSerializer(DateTimeFormatter.ofPattern(DATE_TIME_PATTERN))
                )
                .serializerByType(
                        LocalDate.class,
                        new LocalDateSerializer(DateTimeFormatter.ofPattern(DATE_PATTERN))
                )
                .serializerByType(
                        LocalTime.class,
                        new LocalTimeSerializer(DateTimeFormatter.ofPattern(TIME_PATTERN))
                )
                .deserializerByType(
                        LocalDateTime.class,
                        new LocalDateTimeDeserializer(DateTimeFormatter.ofPattern(DATE_TIME_PATTERN))
                )
                .deserializerByType(
                        LocalDate.class,
                        new LocalDateDeserializer(DateTimeFormatter.ofPattern(DATE_PATTERN))
                )
                .deserializerByType(
                        LocalTime.class,
                        new LocalTimeDeserializer(DateTimeFormatter.ofPattern(TIME_PATTERN))
                );
    }
}
