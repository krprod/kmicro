package com.kmicro.payment.controller;

import com.kmicro.payment.dtos.OrderRequest;
import com.kmicro.payment.dtos.OrderResponse;
import com.kmicro.payment.service.PaymentService;
import com.razorpay.RazorpayException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class PaymentController {

    @Autowired
    PaymentService paymentService;

    @PostMapping("/payment")
    public ResponseEntity<OrderResponse> makePayment(@RequestBody OrderRequest orderRequest){
        OrderResponse orderServiceResponse = paymentService.proceedPayment(orderRequest);
        System.out.println("Request Came");
        return   ResponseEntity.status(200).body(orderServiceResponse) ;
    }

    @PostMapping("/getqr")
    public String getQRCode(){
      try {
          paymentService.getQRCode();
      } catch (RazorpayException e) {
          throw new RuntimeException(e);
      }
        return "Payment Done";
    }

    @PostMapping("/razor/webhook")
    public void webHookHit(@RequestBody String rebody){
        System.out.println("HOOKREpy-->"+rebody);
    }
}
