package com.kmicro.payment.utils;

import com.razorpay.QrCode;
import com.razorpay.RazorpayClient;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Hex;
import org.json.JSONObject;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.util.Arrays;
import java.util.Map;

@Slf4j
public class PaymentUtil {

    private  static String paymentID(Integer length){
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            int randomIndex = (int) (Math.random() * characters.length());
            sb.append(characters.charAt(randomIndex));
        }
        return sb.toString();
    }

    public  static String paymentSign(String orderId, String paymentId, String secretKey) throws NoSuchAlgorithmException, UnsupportedEncodingException, InvalidKeyException {
        String payload = orderId + "|" + paymentId;
//        String keySecret = "";
        Mac mac = Mac.getInstance("HmacSHA256");
        SecretKeySpec secretKeySpec = new SecretKeySpec(secretKey.getBytes(String.valueOf(StandardCharsets.UTF_8)), "HmacSHA256");
        mac.init(secretKeySpec);
        byte[] macSha256 = mac.doFinal(payload.getBytes(StandardCharsets.UTF_8));
        return  new String(Hex.encodeHex(macSha256));
    }

    public  static JSONObject verificationObj(String orderId,String paymentId,  String  secretKey){
        JSONObject verificationRequest = new JSONObject();
        try {
            String signature = paymentSign(orderId, paymentId,secretKey);
            verificationRequest.put("razorpay_order_id", orderId);
            verificationRequest.put("razorpay_payment_id", paymentId);
            verificationRequest.put("razorpay_signature", signature);
            return verificationRequest;
        } catch (RuntimeException | NoSuchAlgorithmException | UnsupportedEncodingException | InvalidKeyException e) {
            log.error(e.getMessage());
            e.printStackTrace();
            return verificationRequest;
        }
    }

    public static  JSONObject  generateQR(){

        Long epochTimeStampPlus120 = Instant.now().getEpochSecond()+120;

        JSONObject qrRequest = new JSONObject();
//        for (Map.Entry<String, String> entry : data.entrySet()) {
//            qrRequest.put(entry.getKey(), entry.getValue());
//        }
        qrRequest.put("fixed_amount",true);
        qrRequest.put("payment_amount",10);
        qrRequest.put("type","upi_qr");
        qrRequest.put("name","Bhai Ki PaymentService");
        qrRequest.put("usage","single_use");
        qrRequest.put("description","descriptionof store");
//        qrRequest.put("customer_id","cust_HKsR5se84c5LTO");
//        qrRequest.put("close_by",epoch);

  /*      JSONObject notes = new JSONObject();
        notes.put("notes_key_1","Main Hu Notes 1");
//        notes.put("notes_key_2","Tea, Earl Grey… decaf.");
        qrRequest.put("notes",notes);*/

//        qrRequest.put("close_by",currentTime.toEpochSecond(200));

        return qrRequest;
    }

    public  static  JSONObject createOrder(Integer amount){
        JSONObject orderRequest = new JSONObject();
        orderRequest.put("amount",amount);
        orderRequest.put("currency","INR");
        orderRequest.put("receipt", "receipt#1");
        return  orderRequest;
    }

    public  static String getTransactionID(){
        return "pay_" + paymentID(14);
    }

}//EC
