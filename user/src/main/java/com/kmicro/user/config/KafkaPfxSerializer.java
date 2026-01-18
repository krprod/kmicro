package com.kmicro.user.config;

import com.kmicro.user.constants.AppContants;
import org.apache.kafka.common.serialization.StringSerializer;

public class KafkaPfxSerializer extends StringSerializer {
    private static final String PREFIX = AppContants.KAFKA_KEY_PREFIX;

    @Override
    public byte[] serialize(String topic, String data) {
        if (data == null) return null;
        return super.serialize(topic, PREFIX + data);
    }
}
