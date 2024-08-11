package sia.tcloud3.service.email;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.mail.javamail.JavaMailSender;

@Slf4j
@Service
public class EmailService implements EmailSender {

    private final JavaMailSender mailSender;


    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    // TODO: Check the hard coded values in this method
    @Override
    @Async
    public void sendEmail(String to, String email) {
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "utf-8");
            helper.setText(email);
            helper.setTo(to);
            helper.setSubject("Confirm your email.");
            helper.setFrom("TCLOUD CLIENT.");
            mailSender.send(mimeMessage);
            log.info("email sent from email service.");
        } catch (MessagingException e) {
            log.error("Failed to send email for: {} \n {}", email, e.getMessage());
            throw new IllegalArgumentException("Failed to send email for: " + email);
        }
    }
}
