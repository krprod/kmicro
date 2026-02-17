package com.kmicro.order.kafka.helpers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.kmicro.order.constants.AppConstants;
import com.kmicro.order.constants.KafkaConstants;
import com.kmicro.order.dtos.OrderDTO;
import com.kmicro.order.dtos.ProcessPaymentRecord;
import com.kmicro.order.entities.PaymentEntity;
import org.springframework.stereotype.Component;

@Component
public class EventDataCreator {

    private final ObjectMapper objectMapper;

    public EventDataCreator(ObjectMapper objectMapper){
        this.objectMapper = objectMapper;
    }

    public String orderConfirmMail(OrderDTO order){
            ObjectNode eventObj = objectMapper.createObjectNode();
            ObjectNode mailBody = objectMapper.createObjectNode();

            String tempaName = "TEMPA-"+ order.getUserId();
            String tempaEmail = "TEMPA-"+ order.getUserId();

            mailBody.set("totaling", this.getTotal(order));
            mailBody.set("details",this.getShippingDetails2(order));
            mailBody.set("items",this.getOrderItems(order));

//            if(order.getUserId() <= 0){
                    tempaName =  mailBody.get("details").get(KafkaConstants.DT_UNAME).asText();
                    tempaEmail = mailBody.get("details").get(KafkaConstants.DT_EMAIL).asText();
//            }

            mailBody.put("title","New Order Placed")
                    .put("greetingByName","Hi,"+tempaName+" ,  your order is placed successfully!")
                    .put("msgLine1","Thank you for shopping with us. Your order details are below.")
                    .put("trackingUrl", AppConstants.TRACKING_URL + order.getTrackingNumber());

            eventObj
                    .put(KafkaConstants.DT_SEND_TO, tempaEmail)
                    .put(KafkaConstants.DT_SUBJECT,"New Order Placed")
                    .set(KafkaConstants.DT_BODY, mailBody);

            return eventObj.toString();
    }

    public String orderStatusChangeMail(OrderDTO order){
            ObjectNode eventObj = objectMapper.createObjectNode();
            ObjectNode mailBody = objectMapper.createObjectNode();

        mailBody
                .put("title","Order Status Updated")
                .put("statusTitle","Your Order Status is updated to <strong>"+order.getOrderStatus()+"</strong>")
                .put("statusMessage","You can get lastest tracking updates for your package using below link and trackingNumber")
                .put("trackingNumber",order.getTrackingNumber())
                .put("carrierName","FedEx")
                .put("trackingUrl", AppConstants.TRACKING_URL + order.getTrackingNumber());


            eventObj
//                    .put(KafkaConstants.DT_SEND_TO,"TEMPA-"+ order.getUserId())
                    .put(KafkaConstants.DT_SEND_TO,order.getShippingAddress().getEmail())
                    .put(KafkaConstants.DT_SUBJECT,"Order Status Updated")
                    .set(KafkaConstants.DT_BODY, mailBody);

            return eventObj.toString();
    }

    public String requestPaymentET(OrderDTO order){
           try {
               ProcessPaymentRecord processPaymentRecord =  new ProcessPaymentRecord(
                       order.getId(),
                       order.getTotalAmount(),
                       order.getPaymentMethod(),
                       order.getUserId(),
                       order.getShippingFee());
               return objectMapper.writeValueAsString(processPaymentRecord);
           } catch (Exception e) {
               throw new RuntimeException(e);
           }
    }

    public String responsePaymentET(PaymentEntity payment){
        try {
           ObjectNode paymentResponse = objectMapper.createObjectNode();

           paymentResponse
                    .put("payment_id", payment.getId())
                    .put("order_id", payment.getOrderId())
                    .put("transaction_id", payment.getTransactionId())
                    .put("user_id", payment.getUserId())
                    .put("payment_status", payment.getStatus())
                    .put("amount", payment.getTotalAmount());

        return paymentResponse.toString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private ObjectNode getTotal(OrderDTO order){
        ObjectNode totaling = objectMapper.createObjectNode();
        totaling
                .put("totalPrice",order.getTotalAmount())
                .put("subtotal",order.getSubtotal())
                .put("shippingFee",order.getShippingFee());

        return totaling;
    }

    private ObjectNode getShippingDetails(OrderDTO order){
        try {
            ObjectNode userData = objectMapper.createObjectNode();
            ObjectNode address = objectMapper.createObjectNode();
            ObjectNode handler = objectMapper.createObjectNode();

            var shippingDetails = order.getShippingAddress();

            userData
                    .put(KafkaConstants.DT_UNAME, shippingDetails.getName())
                    .put(KafkaConstants.DT_CONTACT, shippingDetails.getContact())
                    .put(KafkaConstants.DT_EMAIL, shippingDetails.getEmail());

            address
                    .put(KafkaConstants.DT_COUNTRY, shippingDetails.getCountry())
                    .put(KafkaConstants.DT_CITY, shippingDetails.getCity())
                    .put(KafkaConstants.DT_STATE, shippingDetails.getState())
                    .put(KafkaConstants.DT_ADDRS_ID, shippingDetails.getAddressID())
                    .put(KafkaConstants.DT_SHIP_ADDRS, shippingDetails.getStreet())
                    .put(KafkaConstants.DT_ZCODE, shippingDetails.getZipCode());

            handler.set(KafkaConstants.DT_USER_DATA, userData);
            handler.set(KafkaConstants.DT_ADDRESS_NODE, address);

            return handler;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private ObjectNode getShippingDetails2(OrderDTO order){
        try {
            ObjectNode shippingAddress = objectMapper.createObjectNode();
            var shippingDetails = order.getShippingAddress();
            shippingAddress
                    .put(KafkaConstants.DT_UNAME, shippingDetails.getName())
                    .put(KafkaConstants.DT_CONTACT, shippingDetails.getContact())
                    .put(KafkaConstants.DT_EMAIL, shippingDetails.getEmail())
                    .put(KafkaConstants.DT_COUNTRY, shippingDetails.getCountry())
                    .put(KafkaConstants.DT_CITY, shippingDetails.getCity())
                    .put(KafkaConstants.DT_STATE, shippingDetails.getState())
                    .put(KafkaConstants.DT_ADDRS_ID, shippingDetails.getAddressID())
                    .put(KafkaConstants.DT_SHIP_ADDRS, shippingDetails.getStreet())
                    .put(KafkaConstants.DT_ZCODE, shippingDetails.getZipCode());

            return shippingAddress;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private ArrayNode getOrderItems(OrderDTO order){
        ArrayNode itemsList = objectMapper.createArrayNode();

        order.getOrderItems().forEach(
                item->{
                    itemsList.addObject()
                                    .put("id", item.getId())
                                    .put("quantity", item.getQuantity())
                                    .put("price", item.getPrice())
                                    .put("product_id", item.getProductId())
                                    .put("img",AppConstants.PRODUCT_URL + item.getItemImg())
                                    .put("link", AppConstants.PRODUCT_URL + item.getProductId())
                                    .put("name", item.getItemName()) ;
                }
        );
        return itemsList;
    }

    public String paymentStatus(PaymentEntity payment, boolean paymentSuccess, OrderDTO order) {
        ObjectNode eventObj = objectMapper.createObjectNode();
        ObjectNode mailBody = objectMapper.createObjectNode();
        ObjectNode paymentResponse = objectMapper.createObjectNode();

        String tempaName = "TEMPA-"+ payment.getUserId();
        String tempaEmail = "TEMPA-"+ payment.getUserId();

        paymentResponse
                .put("payment_id", payment.getId())
                .put("order_id", payment.getOrderId())
                .put("transaction_id", payment.getTransactionId())
                .put("user_id", payment.getUserId())
                .put("payment_status", payment.getStatus())
                .put("amount", payment.getTotalAmount());

        mailBody.set("payment",paymentResponse);
        mailBody.set("totaling", this.getTotal(order));
        mailBody.set("details",this.getShippingDetails(order));
        mailBody.set("items",this.getOrderItems(order));

//        if(payment.getUserId() <= 0){
            tempaName =  mailBody.get("details").get(KafkaConstants.DT_USER_DATA).get(KafkaConstants.DT_UNAME).asText();
            tempaEmail = mailBody.get("details").get(KafkaConstants.DT_USER_DATA).get(KafkaConstants.DT_EMAIL).asText();
//        }

        mailBody
                .put("title","Payment Success")
                .put("greetingByName","Hi "+ tempaName+", payment processed successfully  for Order <strong>#"+order.getId()+"</strong>.")
                .put("msgLine1","Thank you for shopping with us. Details are mentioned below.");

      if(!paymentSuccess){
          mailBody
                  .put("title","Payment Failed")

                  .put("greetingByName","Hi "+ tempaName+
                                  ", We were unable to process your payment for Order <strong>#"+order.getId()+"</strong>. Don't worry, your items are still reserved.")
                  .put("msgLine1","Please retry with updated your payment method to complete the purchase. Last Payment details are below.");
      }

//        mailBody.put("payment", this.responsePaymentET(payment));

        eventObj
                .put(KafkaConstants.DT_SEND_TO, tempaEmail)
                .put(KafkaConstants.DT_SUBJECT,"Payment Status Update")
                .set(KafkaConstants.DT_BODY, mailBody);

        return eventObj.toString();
    }
}//EC
