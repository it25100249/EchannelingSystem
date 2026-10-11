package org.echanneling.designpattern.observer;
import org.echanneling.model.DoctorReview;
import org.springframework.stereotype.Component;
/** Concrete Observer used to audit feedback events. */
@Component
public class SystemFeedbackAuditObserver implements SystemFeedbackObserver {
    @Override public void update(String event, DoctorReview feedback) {
        System.out.println("[SystemFeedbackObserver] " + event +
                " feedbackId=" + feedback.getReviewId() +
                " appointmentId=" + feedback.getAppointmentId());
    }
}
