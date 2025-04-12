package com.kmicro.payment.service;

import com.kmicro.payment.dtos.OrderRequest;
import com.kmicro.payment.dtos.OrderResponse;
import com.kmicro.payment.utils.PaymentUtil;
import com.razorpay.*;
import com.zaxxer.hikari.pool.HikariProxyCallableStatement;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Hex;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

import static com.razorpay.Utils.verifySignature;

@Slf4j
@Service
public class PaymentService {

    private  static  final  String API_KEY = "rzp_test_fiaOechiVIrbmK";
    private  static  final  String SECRET_KEY = "DrdgBVbmgBgtYjSVAuOuphPd";

    @Autowired
    PaymentEventService paymentEventService;

    public OrderResponse proceedPayment(OrderRequest orderRequest){
        OrderResponse orderResponse = new OrderResponse();
         try {
             Order order = createOrderForPayment(orderRequest.getAmount().intValue());
             String transactionID = PaymentUtil.getTransactionID();
             String status = verifyPayment(order.get("id"),transactionID) ? "SUCCESS" : "FAILED";

             orderResponse.setOrderID(orderRequest.orderID);
             orderResponse.setPaymentStatus(status);
             orderResponse.setTransactionID(transactionID);
             paymentEventService.processPaymentKafka(orderResponse);
            return orderResponse;
         } catch (RazorpayException e) {
             log.error("doPayment Error: "+ e);
             e.printStackTrace();
         }
         return  orderResponse;
    }

    private   Order  createOrderForPayment(Integer amount)  {
        log.info("createOrderForPayment with amount: {}",amount);
        try {
                RazorpayClient razorpay = new RazorpayClient(API_KEY,SECRET_KEY);
                JSONObject orderRequest = PaymentUtil.createOrder(amount);
                Order order = razorpay.orders.create(orderRequest);
                return  order;
            } catch (RazorpayException e) {
                log.error("Error While createOrderForPayment:{}",e.getMessage());
                log.debug("Error While createOrderForPayment:{}",e.getStackTrace());
                throw new RuntimeException(e);
            }
    }

    private Boolean verifyPayment(String orderID, String transactionID ) throws RazorpayException {
            log.info("Verifying Payment status for orderID:{} and transactionID:{}",orderID,transactionID);
           String secretKey = SECRET_KEY;
        JSONObject verificationRequest = PaymentUtil.verificationObj(orderID,transactionID,SECRET_KEY);
           return Utils.verifyPaymentSignature(verificationRequest,secretKey);
    }

    public void getQRCode() throws RazorpayException {
        RazorpayClient razorpay = new RazorpayClient(API_KEY, SECRET_KEY);
        JSONObject qrRequest =  PaymentUtil.generateQR();
        QrCode qrcode = razorpay.qrCode.create(qrRequest);
    }

}//EC
