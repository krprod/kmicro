package com.kmicro.payment.service;

import com.kmicro.payment.dtos.OrderRequest;
import com.kmicro.payment.dtos.OrderResponse;
import com.kmicro.payment.utils.PaymentUtil;
import com.razorpay.*;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class PaymentService {

    private  static  final  String API_KEY = "";
    private  static  final  String SECRET_KEY = "";

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
