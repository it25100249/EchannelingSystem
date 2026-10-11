package org.echanneling.designpattern.observer;
import org.echanneling.model.DoctorReview;
/** Observer participant: reacts when system feedback changes. */
public interface SystemFeedbackObserver {
    void update(String event, DoctorReview feedback);
}
