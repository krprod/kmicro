package com.kmicro.order.config;

import com.kmicro.order.constants.AppConstants;
import org.apache.kafka.common.serialization.StringSerializer;

public class KafkaPfxSerializer extends StringSerializer {
    private static final String PREFIX = AppConstants.KAFKA_KEY_PREFIX;

    @Override
    public byte[] serialize(String topic, String data) {
        if (data == null) return null;
        return super.serialize(topic, PREFIX + data);
    }
}
