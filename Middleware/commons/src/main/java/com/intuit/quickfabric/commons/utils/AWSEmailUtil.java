package com.intuit.quickfabric.commons.utils;

import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailService;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailServiceClientBuilder;
import com.amazonaws.services.simpleemail.model.*;
import com.intuit.quickfabric.commons.helper.ConfigHelper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Component
public class AWSEmailUtil {

    @Autowired
    ConfigHelper configHelper;

    final String TEXTBODY = "This email is from QuickFabric. Enable HTML to view the message.";

    Logger logger = LogManager.getLogger(AWSEmailUtil.class);

    public void sendEmail(String htmlbody, String subject, String... toAddresses) {
        try {
            logger.info("sending email to: " + Arrays.toString(toAddresses));
            SendEmailRequest request = buildEmail(htmlbody, subject, toAddresses);
            sendEmail(request);
        } catch (Exception e) {
            logger.error("error in sending email", e);
        }
    }

    private void sendEmail(SendEmailRequest email) {
        Region region = Regions.getCurrentRegion();
        if (region == null) {
            logger.warn("no region found");
            region = Region.getRegion(Regions.US_WEST_2);
        }

        logger.info("sending email with region:" + region.getName());
        Regions regions = Regions.fromName(region.getName());
        AmazonSimpleEmailService client = AmazonSimpleEmailServiceClientBuilder
                .standard()
                .withRegion(regions)
                .build();

        logger.info("sending email to AWS simple email service");
        SendEmailResult sendEmailResult = client.sendEmail(email);
        logger.info("send email result:" + sendEmailResult.toString());
        logger.info("Emails are sent to these recipients: {}",
                String.join(",", email.getDestination().getToAddresses()));

    }

    private SendEmailRequest buildEmail(String htmlbody, String subject, String... toAddresses) {
        logger.info("building email");
        
        List<String> recipients = new ArrayList<>();
        
        for(String email : toAddresses) {
            if(email.contains(",")) {
                for(String e : email.split(",")) {
                    recipients.add(e);
                }
            } else {
                recipients.add(email);
            }
            
        }
        
        SendEmailRequest request = new SendEmailRequest()
                .withDestination(
                        new Destination().withToAddresses(recipients))
                .withMessage(new Message()
                        .withBody(new Body()
                                .withHtml(new Content()
                                        .withCharset("UTF-8").withData(htmlbody))
                                .withText(new Content()
                                        .withCharset("UTF-8").withData(TEXTBODY)))
                        .withSubject(new Content()
                                .withCharset("UTF-8").withData(subject)))
                .withSource(configHelper.getConfigValue("from_email_address"));

        return request;
    }
}
