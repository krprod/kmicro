package com.kmicro.payment.config;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.header.internals.RecordHeader;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Properties;
import java.util.concurrent.Future;

public class CustomKafkaProducer <K, V> extends KafkaProducer<K, V> {

    public CustomKafkaProducer(Properties properties) {
        super(properties);
    }

    @Override
    public Future<RecordMetadata> send(ProducerRecord<K, V> record) {

//        if (Boolean.FALSE.equals(TenantService.isMultiTenancyFeatureEnabled)) {
//            return super.send(record);
//        }

        /**
         * Add your custom logic here before sending the record
         * For example, you can log the record or modify its contents
         * Execute custom code before message processing (e.g., logging, pre-processing)
         */

        /**
         * Create a new ProducerRecord with the generic headers
         */
     /*   Object companyId  = SchemaHolder.get(Constants.COMPANY_ID) == null ? null : SchemaHolder.get(Constants.COMPANY_ID);
        String schema  = SchemaHolder.get(Constants.COMPANY_SCHEMA_NAME) == null ? null : SchemaHolder.get(Constants.COMPANY_SCHEMA_NAME).toString();

        if (StringUtils.isEmpty(schema)) {
            throw new RuntimeException("We are getting null schema to process the request");
        }

        if (companyId == null) {
            logger.error("we are getting null company Id for the schema : " + schema + " to process the request");
            if(SecurityUtils.getCurrentUser() != null && SecurityUtils.getCurrentUser().getCompany() != null){
                companyId = SecurityUtils.getCurrentUser().getCompany().getId();
            }
        }*/

        /**
         * Add your custom headers to the RecordHeaders
         */
//        RecordHeader companyIdRecHeader = null;
//        RecordHeader SchemaNameRecHeader = null;
//
//        SchemaNameRecHeader = new RecordHeader(Constants.COMPANY_SCHEMA_NAME, schema.getBytes());
//
//        try {
//            companyIdRecHeader = new RecordHeader(Constants.COMPANY_ID, objectToBytes(companyId));
//        } catch (Exception e) {
//            logger.error("Getting exception while convert object to byte : {}", e);
//            companyIdRecHeader = new RecordHeader(Constants.COMPANY_ID, null);
//        }
//
//        record.headers().add(companyIdRecHeader).add(SchemaNameRecHeader);

        /**
         *  Call the send method of the parent class with the record containing headers
         *  Then, call the parent class's send method to actually send the record
         */
        return super.send(record);
    }

    private byte[] objectToBytes(Object obj) throws IOException {
        if (obj == null) {
            return  null;
        }
        try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
             ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream)) {
            objectOutputStream.writeObject(obj);
            return byteArrayOutputStream.toByteArray();
        }
    }
}
