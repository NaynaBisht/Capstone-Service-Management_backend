package com.app.notification.service;

import java.util.Map;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    public void sendBookingCreatedEmail(
            String to,
            Map<String, Object> data
    ) {

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("Booking Confirmed");

        message.setText(
            "Your booking is confirmed.\n\n" +
            "Booking ID: " + data.get("bookingId") + "\n" +
            "Service: " + data.get("serviceName") + "\n" +
            "Date: " + data.get("scheduledDate") + "\n" +
            "Time Slot: " + data.get("timeSlot")
        );

        mailSender.send(message);
        log.info("Booking confirmation email sent to {}", to);
    }
    
    public void sendBookingRescheduledEmail(
            String to,
            Map<String, Object> data
    ) {

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("Booking Rescheduled");

        message.setText(
            "Your booking has been rescheduled.\n\n" +
            "Booking ID: " + data.get("bookingId") + "\n" +
            "Service: " + data.get("serviceName") + "\n" +
            "New Date: " + data.get("scheduledDate") + "\n" +
            "New Time Slot: " + data.get("timeSlot")
        );

        mailSender.send(message);
        log.info("Booking rescheduled email sent to {}", to);
    }

    public void sendBookingCancelledEmail(
            String to,
            Map<String, Object> data
    ) {

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("Booking Cancelled");

        message.setText(
            "Your booking has been cancelled.\n\n" +
            "Booking ID: " + data.get("bookingId") + "\n" +
            "Service: " + data.get("serviceName")
        );

        mailSender.send(message);
        log.info("Booking cancellation email sent to {}", to);
    }
    
    public void sendTechnicianAssignedEmail(
            String to,
            Map<String, Object> data
    ) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("New Job Assigned");

        message.setText(
            "You have been assigned a new job.\n\n" +
            "Assignment ID: " + data.get("assignmentId") + "\n" +
            "Booking ID: " + data.get("bookingId") + "\n" +
            "Service: " + data.get("serviceName")
        );

        mailSender.send(message);
        log.info("Technician assignment email sent to {}", to);
    }

    public void sendAssignmentStartedEmail(
            String to,
            Map<String, Object> data
    ) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("Service Started");

        message.setText(
            "Your service has started.\n\n" +
            "Booking ID: " + data.get("bookingId") + "\n" +
            "Service: " + data.get("serviceName")
        );

        mailSender.send(message);
        log.info("Assignment started email sent to {}", to);
    }

    public void sendAssignmentCompletedEmail(
            String to,
            Map<String, Object> data
    ) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("Service Completed");

        message.setText(
            "Your service has been completed successfully.\n\n" +
            "Booking ID: " + data.get("bookingId") + "\n" +
            "Service: " + data.get("serviceName")
        );

        mailSender.send(message);
        log.info("Assignment completed email sent to {}", to);
    }
    
    public void sendTechnicianApprovedEmail(String to, Map<String, Object> data) {

        String name = (String) data.get("name");
        String tempPassword = (String) data.get("temporaryPassword");

        String subject = "Your NexaHome Technician Account is Approved";

        String body = """
            Hello %s,

            Congratulations! Your technician account has been approved.

            You can now log in using the following temporary password:

            Temporary Password: %s

            Please change your password after your first login.

            Regards,
            NexaHome Team
            """.formatted(name, tempPassword);

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(subject);
        message.setText(body);

        mailSender.send(message);
        log.info("SUCCESS: Technician approval email sent to {}", to);
    }
}
