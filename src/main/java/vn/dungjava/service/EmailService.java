package vn.dungjava.service;

import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.Response;
import com.sendgrid.SendGrid;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;
import com.sendgrid.helpers.mail.objects.Personalization;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j(topic = "EMAIL-SERVICE")
public class EmailService {

    @Value("${spring.sendgrid.from-email}")
    private String from;

    @Value("${spring.sendgrid.templateId}")
    private String templateId;

    @Value("${spring.sendgrid.verificationLink}")
    private String verificationLink;

    private final SendGrid sendGrid;

    /**
     * Send email by SendGrid
     * @param to send email to someone
     * @param subject
     * @param body
     */
    public void send(String to, String subject, String body) {
        Email fromEmail = new Email(from);
        Email toEmail = new Email(to);

        Content content = new Content("text/plain", body);
        Mail mail = new Mail(fromEmail, subject, toEmail, content);

        Request request = new Request();
        try {
            request.setMethod(Method.POST);
            request.setEndpoint("mail/send");
            request.setBody(mail.build());

            Response response = sendGrid.api(request);
            if (response.getStatusCode() == 202) {
                log.info("Email sent successfully");
            } else {
                log.error("Email sent failed");
            }
        } catch (IOException e) {
            log.error("Error occurred sending email : {}", e.getMessage());
        }
    }

    /**
     * Email verificaion by SendGrid
     * @param to
     * @param name
     * @throws IOException
     */
    public void emailVerification(String to, String name) throws IOException {
        log.info("Email verification started");

        Email fromEmail = new Email(from, "Dung Java");
        Email toEmail = new Email(to);

        String subject = "Account Verification";

        String secretCode = String.format("?secretCode=%s", UUID.randomUUID());

        //generate secret code and save to db

        //Dinh nghia template
        Map<String, String> map = new HashMap<>();
        map.put("name", name);
        map.put("verification_link", verificationLink + secretCode);

        Mail mail = new Mail();
        mail.setFrom(fromEmail);
        mail.setSubject(subject);

        Personalization personalization = new Personalization();
        personalization.addTo(toEmail);

        //Add to dynamic data
        map.forEach(personalization::addDynamicTemplateData);

        mail.addPersonalization(personalization);
        mail.setTemplateId(templateId);

        Request request = new Request();
        request.setMethod(Method.POST);
        request.setEndpoint("mail/send");
        request.setBody(mail.build());

        Response response = sendGrid.api(request);
        if (response.getStatusCode() == 202) {
            log.info("Verification sent successfully");
        } else {
            log.error("Verification failed");
        }
    }
}
