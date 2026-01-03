package com.kmicro.order.config;

/*
@Configuration
public class JacksonConfig {

    @Bean
    public Jackson2ObjectMapperBuilderCustomizer jacksonCustomizer() {
        return builder -> {
//            builder.timeZone(TimeZone.getTimeZone(AppConstants.ASIA_ZONE_ID));
            builder.simpleDateFormat(AppConstants.HR_24_FORMAT);

            builder.modules(new JavaTimeModule());
            builder.featuresToDisable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern(AppConstants.HR_24_FORMAT);
            builder.serializers(new LocalDateTimeSerializer(formatter));
          */
/*  builder.serializers(new InstantSerializer(
                    InstantSerializer.INSTANCE, false,
                    new DateTimeFormatterBuilder().appendInstant(0).toFormatter().withZone(AppConstants.ASIA_ZONE_ID))
            );*//*

            builder.serializerByType(Instant.class, new com.fasterxml.jackson.datatype.jsr310.ser.InstantSerializer(
                    com.fasterxml.jackson.datatype.jsr310.ser.InstantSerializer.INSTANCE,
                    false,
                    false,
                    formatter
            ));
        };
    }
}*/
