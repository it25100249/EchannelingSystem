package org.echanneling.designpattern.observer;
import org.echanneling.model.DoctorReview;
import org.springframework.stereotype.Component;
import java.util.List;
/** Subject participant: notifies all registered feedback observers. */
@Component
public class SystemFeedbackSubject {
    private final List<SystemFeedbackObserver> observers;
    public SystemFeedbackSubject(List<SystemFeedbackObserver> observers) { this.observers = observers; }
    public void notifyObservers(String event, DoctorReview feedback) {
        observers.forEach(observer -> observer.update(event, feedback));
    }
}
