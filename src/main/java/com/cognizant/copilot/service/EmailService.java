package com.cognizant.copilot.service;

import com.cognizant.copilot.config.AppConfig;
import com.cognizant.copilot.model.TrackerResult;
import com.cognizant.copilot.model.UserInfo;
import com.cognizant.copilot.util.DateFormatUtils;
import com.cognizant.copilot.util.StringUtils;
import jakarta.mail.Authenticator;
import jakarta.mail.Message;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.util.Properties;

/**
 * Service for sending reminder and escalation emails.
 */
public class EmailService {

    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);

    private final boolean emailEnabled;
    private final String smtpHost;
    private final String smtpPort;
    private final String fromEmail;
    private final String fromPassword;
    private final int continuousMissingDays;

    public EmailService() {
        AppConfig config = AppConfig.getInstance();
        this.emailEnabled = config.getBoolean("email.enabled", false);
        this.smtpHost = config.getString("email.smtp.host", "smtp.office365.com");
        this.smtpPort = config.getString("email.smtp.port", "587");
        this.fromEmail = config.getString("email.from.address", "");
        this.fromPassword = config.getString("email.from.password", "");
        this.continuousMissingDays = config.getInt("tracker.continuous.missing.days", 5);
    }

    /**
     * Send reminder and escalation emails
     */
    public void sendReminderAndEscalationMails(
            TrackerResult result,
            LocalDate fromDate,
            LocalDate toDate) {

        logger.info("Processing reminder and escalation emails");

        for (UserInfo user : result.pendingUsers) {
            String subject = "Reminder: Please update GenAI Copilot tracker";

            String body = "Hi " + user.name + ",\n\n"
                    + "This is a gentle reminder to update your GenAI Copilot tracker entries "
                    + "for the period " + fromDate.format(DateFormatUtils.getDisplayFormatter())
                    + " to " + toDate.format(DateFormatUtils.getDisplayFormatter()) + ".\n\n"
                    + "Please update the tracker and confirm once done.\n\n"
                    + "Thanks,\n"
                    + "Copilot Tracker Monitor";

            sendMail(user.email, user.teamLeadEmail, subject, body);
        }

        for (UserInfo user : result.fiveDayDefaulters) {
            String subject = "Escalation: GenAI Copilot tracker not updated for continuous " + continuousMissingDays + " days";

            String body = "Hi " + user.managerPoc + ",\n\n"
                    + "The below resource has not updated the GenAI Copilot tracker "
                    + "for continuous " + continuousMissingDays + " days.\n\n"
                    + "Resource Name: " + user.name + "\n"
                    + "Team Name: " + user.teamName + "\n"
                    + "Team Lead: " + user.teamLead + "\n\n"
                    + "Kindly review and request the resource to update the tracker.\n\n"
                    + "Thanks,\n"
                    + "Copilot Tracker Monitor";

            sendMail(user.managerEmail, user.teamLeadEmail, subject, body);
        }

        logger.info("Processed {} reminder emails and {} escalation emails",
                result.pendingUsers.size(), result.fiveDayDefaulters.size());
    }

    /**
     * Send email
     */
    private void sendMail(String to, String cc, String subject, String body) {
        if (!emailEnabled) {
            logger.info("Email sending is disabled (SEND_EMAIL=false). Preview:");
            System.out.println("\n========= EMAIL PREVIEW =========");
            System.out.println("To      : " + to);
            System.out.println("CC      : " + cc);
            System.out.println("Subject : " + subject);
            System.out.println("Body    : \n" + body);
            return;
        }

        try {
            Properties props = new Properties();
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.starttls.enable", "true");
            props.put("mail.smtp.host", smtpHost);
            props.put("mail.smtp.port", smtpPort);

            Session session = Session.getInstance(props, new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(fromEmail, fromPassword);
                }
            });

            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(fromEmail));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));

            if (!StringUtils.isBlank(cc)) {
                message.setRecipients(Message.RecipientType.CC, InternetAddress.parse(cc));
            }

            message.setSubject(subject);
            message.setText(body);

            Transport.send(message);

            logger.info("Mail sent successfully to: {}", to);

        } catch (Exception e) {
            logger.error("Failed to send mail to: {}", to, e);
        }
    }
}
