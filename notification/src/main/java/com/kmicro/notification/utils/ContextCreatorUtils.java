package com.kmicro.notification.utils;

import com.kmicro.notification.constansts.Templates;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.PropertyAccessorFactory;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.util.Currency;
import java.util.Locale;
import java.util.Map;

@Slf4j
@Service
public class ContextCreatorUtils {

    public Context createInitialCtx(){
        Context context = new Context(Locale.getDefault());
//        context.setVariable("logoUrl", "https://id-mail-assets.atlassian.com/shared/logo/adg3/product/jira/logo-blue-2x.png"); // Replace with your actual logo URL
        context.setVariable("logoUrl", "https://kmicro-base-bucket-s3.s3.ap-south-1.amazonaws.com/SC-GRAD_TOP.png"); // Replace with your actual logo URL
        context.setVariable("title", Templates.DEFAULT_LAYOUT.name());
        context.setVariable("currencySymbol", Currency.getInstance("INR").getSymbol(new Locale("en", "IN")) );
        context.setVariable("logoUrlAlt", "KMICRO_LOGO"); // Replace with your actual logo URL
        context.setVariable("siteUrl", "https://www.Kmicro.com"); // Replace with your website URL
//        context.setVariable("footerLogoUrl", "https://id-mail-assets.atlassian.com/shared/id-summit/id-summit-email_logo_360x80_NEW.png"); // Replace with your website URL
        context.setVariable("footerLogoUrl", "https://kmicro-base-bucket-s3.s3.ap-south-1.amazonaws.com/SC-GRAD_Botm.png"); // Replace with your website URL
        context.setVariable("footerLogoUrlAlt", "KMICRO_LOGO"); // Replace with your website URL
        context.setVariable("footerSentBy", "This message was sent to you by The Kmicro Team"); // Replace with your website URL
        context.setVariable("companyName", "The Kmicro Team");
        context.setVariable("websiteName", "Kmicro.com");
        context.setVariable("socialMediaLinks", "<a href=\"#\">Facebook</a> | <a href=\"#\">Twitter</a>"); // Example
        log.info("Context Initialized Successfully");
        return context;
    }

    public <T> Context generateCtxByData(T data){
        Context context = this.createInitialCtx();

        if (data == null) {
            return context;
        }

        // Case 1: Handle Map types
        if (data instanceof Map<?, ?> map) {
            this.addMapInExistingContext(context,map);
        }// Case 2: Handle Objects (POJOs/Records) via Reflection
        else {
            this.addDTOInExistingContext(context,data);
        }
        log.info("Context Generation Successful");
        return context;
    }

    public void addMapInExistingContext(Context cxt, Map<?, ?> data){
        if (data instanceof Map<?, ?> map) {
            map.forEach((key, value) -> {
                cxt.setVariable(String.valueOf(key), value);
            });
        }
        log.info("Generated Context Using Map");
    }

    public Context getNewContextForMap(Map<?, ?> data){
        Context context = this.createInitialCtx();
        if (data == null) return context;
        this.addMapInExistingContext(context, data);
        return context;
    }

    public <T> Context getNewContextForDTO(T data){
        Context context = this.createInitialCtx();
        if (data == null) return context;
        this.addDTOInExistingContext(context, data);
        return context;
    }

    public <T> void addDTOInExistingContext(Context cxt, T data){
        Class<?> clazz = data.getClass();
        // Iterate through fields of the class and its superclasses
        while (clazz != null && clazz != Object.class) {
            for (Field field : clazz.getDeclaredFields()) {
                // Skip synthetic fields (like those added by JaCoCo) or static fields
                if (field.isSynthetic() || java.lang.reflect.Modifier.isStatic(field.getModifiers())) {
                    continue;
                }

                try {
                    field.setAccessible(true);
                    Object value = field.get(data);
                    cxt.setVariable(field.getName(), value);
                } catch (IllegalAccessException e) {
                    // Log error: standard practice is to use a logger like SLF4J
                    // logger.error("Failed to access field: {}", field.getName(), e);
                }
            }
            clazz = clazz.getSuperclass();
        }
        log.info("Generated Context Using DTO");
    }

    public void setFragment(Context cxt, String fragment){
        cxt.setVariable("content", fragment);
        log.info("Setting Fragment: {}", fragment);
    }

    //---------- POC Failed NOT WORKING
    public <T> Context generateCtxByGenricData(T data) {
        Context context = this.createInitialCtx();

        if (data == null) return context;

        // Use Spring's high-level BeanWrapper instead of raw Field reflection
        BeanWrapper beanWrapper = PropertyAccessorFactory.forBeanPropertyAccess(data);
        PropertyDescriptor[] descriptors = beanWrapper.getPropertyDescriptors();

        for (PropertyDescriptor descriptor : descriptors) {
            String name = descriptor.getName();

            // Skip the 'class' property which exists on every Java object
            if ("class".equals(name)) continue;

            try {
                // This will automatically find the getter method or field
                Object value = beanWrapper.getPropertyValue(name);
                context.setVariable(name, value);
            } catch (Exception e) {
                // Log warning: could not read property
            }
        }

        return context;
    }


}//EC
