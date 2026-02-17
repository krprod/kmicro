package com.kmicro.order.utils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.concurrent.ThreadLocalRandom;

@Slf4j
@Component
@RequiredArgsConstructor
public class CommonUtils {

    public void chaosMonkey(Boolean enabled){
        if(!enabled) return;
        try {
            Thread.sleep(5000);
            double  failureProbability = 0.5;
            double chance = ThreadLocalRandom.current().nextDouble();
            if (chance < failureProbability) {

                log.error(
                        "❌ RANDOM ERROR: Chaos Monkey triggered! (Chance: {} < Probability: {})",
                        String.format("%.2f", chance), failureProbability
                );

                throw  new RuntimeException("---- CHAOS CREATED ----");
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        //            log.info("✅ SUCCESS: Operation completed. (Chance: {} >= Probability: {})",
//                    String.format("%.2f", chance), failureProbability);
//            String Text = getTextFromMessage(mailMessage);
//           log.info("Text: {}",Text);
    }
}
