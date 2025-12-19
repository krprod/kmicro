package com.kmicro.product;

import org.apache.kafka.common.serialization.*;

public interface KafkaConstants {

    String KEY_DESERIALIZER_CLASS_CONFIG = LongDeserializer.class.getName();

    String VALUE_DESERIALIZER_CLASS_CONFIG = StringDeserializer.class.getName();

    String KEY_SERIALIZER_CLASS_CONFIG = LongSerializer.class.getName();

    String VALUE_SERIALIZER_CLASS_CONFIG = StringSerializer.class.getName();

    String BYTE_ARRAY_VALUE_DESERIALIZER_CLASS_CONFIG = ByteArrayDeserializer.class.getName();

    boolean ENABLE_AUTO_COMMIT_CONFIG = false;

    Integer RETRIES_CONFIG = 2;

    String AUTO_OFFSET_RESET_CONFIG = "earliest";

    String AUTO_OFFSET_LATEST_CONFIG = "latest";

    String KAFKA_TOPIC_PROCESS_API = "process_api";

    String KAFKA_TOPIC_PROCESS_REVERSE_CONNECTOR = "process_reverse_connector";

}
