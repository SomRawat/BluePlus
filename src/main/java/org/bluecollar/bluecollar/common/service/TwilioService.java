package org.bluecollar.bluecollar.common.service;

import com.twilio.Twilio;
import com.twilio.exception.ApiException;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class TwilioService {

    private static final Logger logger = LoggerFactory.getLogger(TwilioService.class);

    @Value("${twilio.account.sid}")
    private String accountSid;

    @Value("${twilio.auth.token}")
    private String authToken;

    @Value("${twilio.phone.number:+12695570572}")
    private String fromPhoneNumber;

    @PostConstruct
    public void initTwilio() {
        Twilio.init(accountSid, authToken);
        logger.info("Twilio service initialized.");
    }

    public void sendSms(String toPhoneNumber, String messageBody) {
        try {
            Message message = Message.creator(
                    new PhoneNumber(toPhoneNumber),
                    new PhoneNumber(fromPhoneNumber),
                    messageBody
            ).create();

            logger.info("SMS sent successfully to {}. SID: {}", toPhoneNumber, message.getSid());
        } catch (ApiException e) {
            logger.error("Twilio API Error sending SMS to {}: {} - Code: {}. More info: {}", toPhoneNumber, e.getMessage(), e.getCode(), e.getMoreInfo(), e);
        } catch (Exception e) {
            logger.error("An unexpected error occurred when sending SMS to {}.", toPhoneNumber, e);
        }
    }
}