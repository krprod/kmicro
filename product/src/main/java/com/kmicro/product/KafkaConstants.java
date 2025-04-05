package com.kmicro.product;

import org.apache.kafka.common.serialization.*;

public interface KafkaConstants {

    String GROUP_ID_CONFIG = "fareye-mobi";


    String KEY_DESERIALIZER_CLASS_CONFIG = LongDeserializer.class.getName();

    String VALUE_DESERIALIZER_CLASS_CONFIG = StringDeserializer.class.getName();

    String KEY_SERIALIZER_CLASS_CONFIG = LongSerializer.class.getName();

    String VALUE_SERIALIZER_CLASS_CONFIG = StringSerializer.class.getName();

    String BYTE_ARRAY_VALUE_DESERIALIZER_CLASS_CONFIG = ByteArrayDeserializer.class.getName();

    boolean ENABLE_AUTO_COMMIT_CONFIG = false;

    String CLIENT_ID_CONFIG = "fareye-mobi";

    Integer RETRIES_CONFIG = 2;

    String AUTO_OFFSET_RESET_CONFIG = "earliest";

    String AUTO_OFFSET_LATEST_CONFIG = "latest";

    String KAFKA_TOPIC_PROCESS_API = "process_api";

    String KAFKA_TOPIC_PROCESS_REVERSE_CONNECTOR = "process_reverse_connector";

//    String KAFKA_TOPIC_PROCESS_EXCEL= "process_excel";
/*
    String KAFKA_TOPIC_TRANSACTION_STATUS_API= "transaction_status_api";

    String KAFKA_TOPIC_TRANSACTION_STATUS_EXCEL= "transaction_status_excel";

    String CATCH_EVENT_RETRY_TOPIC = "catch_event_retry";

    String KAFKA_TOPIC_ON_PAGE_REPORT="graphql_on_page_report";

    String PAGE_REPORT_DOWNLOAD_RESPONSE_TOPIC="graphql_page_report_response";

    String  POST_HOOK_TRACK_LOG_TOPIC= "track_log_topic";

    String TRIP_JOBS = "trip_jobs";

    String KAFKA_TOPIC_CARRIER_ALLOCATION_RESPONSE= "carrier_allocation_response";

    String KAFKA_TOPIC_CARRIER_ALLOCATION_REQUEST  = "carrier_allocation_request";

    String KAFKA_TOPIC_RATE_CALCULATION_REQUEST  = "rate_calculation_request";
    String KAFKA_TOPIC_RATE_CALCULATION_RESPONSE= "rate_calculation_response";

    String KAFKA_TOPIC_RATE_DISCOUNT_CALCULATION_RESPONSE= "rate_discount_calculation_response";

    String KAFKA_TOPIC_RATE_DISCOUNT_CALCULATION_RESPONSE_RETRY= "rate_discount_calculation_response_retry";

    String INTEGRATION_CALL_LOG_TOPIC = "fareye-call-log-topic";

    String KAFKA_TOPIC_DEBUG_PROCESS = "debug_process";

    String KAFKA_TOPIC_DYNAMIC_SLOT_RESPONSE = "ds_response_data";

    String KAFKA_TOPIC_ROUTE_BROADCAST = "route_broadcast_topic_update_for_routing";

    String KAFKA_TOPIC_SCHEDULE_REPLAY_TEST_CASE = "replay_scheduled_test_cases";

    String KAFKA_TOPIC_REPLAY_TEST_CASE = "replay_test_cases";

    String KAFKA_TOPIC_REPLAY_TEST_CASE_V2 = "replay_test_cases_v2";

    String KAFKA_TOPIC_REPLAY_TEST_CASE_V2_RETRY = "replay_test_cases_v2_retry";

    String KAFKA_TOPIC_EXTERNAL_SCHEDULER = "external_scheduler";
    String SCHEDULED_ANNOTATED_CONSUMER = "scheduled_annotated_consumer";

    String KAFKA_TOPIC_SCAN_FROM_ANYWHERE= "scan_from_anywhere";

    String KAFKA_TOPIC_PROCESS_SCHEDULER = "process_scheduler_queue";
    String KAFKA_TOPIC_DATABRICKS_VIEW = "databricks_view";

    String KAFKA_TOPIC_FEATURE_PUB_SUB = "feature_pub_sub-topic";

    String KAFKA_TOPIC_KAFKA_PUB_SUB = "kafka_pub_sub";


    String KAFKA_TOPIC_FOR_DATABRICKS_LOG = "databricks_view";

    String GROUP_ID_CONFIG_FOR_PROCESS_SCHEDULER_CONSUMER = "fareye-mobi-process-scheduler-consumer";

    String KAFKA_TOPIC_ROUTING_RESPONSE_CONSUMER = "routing_response";

    String GROUP_ID_CONFIG_FOR_VIEW_ACTIVITY_LOGS = "fareye-mobi-view-activity-consumer";*/
}
