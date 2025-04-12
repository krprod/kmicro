package com.kmicro.notification.service;

import com.kmicro.notification.dtos.Address;
import com.kmicro.notification.dtos.Customer;
import com.kmicro.notification.dtos.Order;
import com.kmicro.notification.dtos.OrderItem;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.File;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Slf4j
@Service
public class EmailServiceHelper {

    @Autowired
    private TemplateEngine templateEngine;

    protected   String createCntx(Order order){
        Context context = new Context(Locale.getDefault());
        context.setVariable("subject", "Thanks for your Kmicro Order #" + order.getOrderNumber() + "!");
        context.setVariable("logoUrl", ""); // Replace with your actual logo URL
        context.setVariable("headerText", "Thanks for your order!");
        context.setVariable("customerName", order.getCustomer().getName());
        context.setVariable("orderReceivedText", "Just to let you know — we've received your order #" + order.getOrderNumber() + ", and it is now being processed.");
        context.setVariable("orderNumber", order.getOrderNumber());
        context.setVariable("orderDate", order.getOrderDate().format(DateTimeFormatter.ofPattern("MMMM d, yyyy")));
        context.setVariable("productLabel", "Product");
        context.setVariable("quantityLabel", "Quantity");
        context.setVariable("priceLabel", "Price");
        context.setVariable("orderItems", order.getOrderItems());
        context.setVariable("subtotal", order.getSubtotal());
        context.setVariable("shippingCost", order.getShippingCostDescription()); // Or the actual cost if numeric
        context.setVariable("paymentMethod", order.getPaymentMethod());
        context.setVariable("totalAmount", order.getTotalAmount());
        context.setVariable("igstAmount", order.getIgstAmount());
        context.setVariable("billingAddressLabel", "Billing address");
        context.setVariable("billingAddress", order.getBillingAddress());
        context.setVariable("shippingAddressLabel", "Shipping address");
        context.setVariable("shippingAddress", order.getShippingAddress());
        context.setVariable("orderDetailsUrl", "/orders/" + order.getOrderNumber()); // Replace with your actual order details URL
        context.setVariable("viewOrderDetailsLabel", "Click to view order details");
        context.setVariable("shippingNotificationText", "We'll send you another email notification when your order ships.");
        context.setVariable("contactUsText", "If you have any questions, please don't hesitate to contact our support team.");
        context.setVariable("thankYouText", "Thanks again for choosing Kmicro!");
        context.setVariable("sincerelyText", "Sincerely,");
        context.setVariable("companyName", "The Kmicro Team");
        context.setVariable("websiteUrl", "https://www.Kmicro.com"); // Replace with your website URL
        context.setVariable("websiteName", "Kmicro.com");
        context.setVariable("socialMediaLinks", "<a href=\"#\">Facebook</a> | <a href=\"#\">Twitter</a>"); // Example

        String htmlContent = templateEngine.process("thyEmailTemplate.html", context);

        return  htmlContent;
    }

    protected void  addAttachment(MimeMessageHelper helper, String attachmentPath){
        try {
            log.info(" MimeMessageHelper:{}",helper);
            ClassPathResource resource = new ClassPathResource("Alarm_Double.png");
            log.info("filePath: {}",resource);
            FileSystemResource file = new FileSystemResource(new File(attachmentPath));
            helper.addAttachment(file.getFilename(), file);
            log.info("Attachment File:{}",file);
        } catch (Exception e) {
            log.info("error while sending mail:{}",e.getMessage());
            log.info("detail error msg: {}",e.getStackTrace());
        }
    }

    protected  Order getOrderDetails(){
        // Create a Customer
        Customer customer = new Customer();
        customer.setName("xman");
        customer.setEmail("xman@gmail.com");

        // Create Billing Address
        Address billingAddress = new Address();
        billingAddress.setName("x x");
        billingAddress.setStreet1("x-1,x no.4, x x, x");
        billingAddress.setStreet2("x, x x");
        billingAddress.setCity("x");
        billingAddress.setPostalCode("x");
        billingAddress.setState("x");
        billingAddress.setPhone("xxxxxxxxxxxx");
        billingAddress.setEmail("x@gmail.com"); // You might not want to duplicate this in Address

        // Create Shipping Address (same as billing in this case)
        Address shippingAddress = new Address();
        shippingAddress.setName("x x");
        shippingAddress.setStreet1("x-x,x no.4, x x, x");
        shippingAddress.setStreet2("x, x x");
        shippingAddress.setCity("x");
        shippingAddress.setPostalCode("x");
        shippingAddress.setState("x");

        // Create Order Items
        List<OrderItem> orderItems = new ArrayList<>();
        OrderItem item1 = new OrderItem();
        item1.setName("Keychron K4 Wireless Mechanical Keyboard (Version 2)\nSwitches: Blue Switch\nSWITCH TYPE: Gateron\nVERSION: RGB Backlight Aluminium");
        item1.setQuantity(1);
        item1.setPrice(9999.00);
        orderItems.add(item1);

        // Set Order Details
        Order order = new Order();
        order.setOrderNumber("268544");
        order.setOrderDate(LocalDate.of(2024, 7, 2));
        order.setCustomer(customer);
        order.setOrderItems(orderItems);
        order.setSubtotal(9999.00);
        order.setShippingCostDescription("Free shipping");
        order.setPaymentMethod("Paytm Payment Gateway");
        order.setTotalAmount(9999.00);
        order.setIgstAmount(1525.27); // As mentioned in the email
        order.setBillingAddress(billingAddress);
        order.setShippingAddress(shippingAddress);

        return order;
    }

}//EC
