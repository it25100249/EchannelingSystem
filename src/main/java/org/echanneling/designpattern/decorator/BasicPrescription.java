package org.echanneling.designpattern.decorator;
import org.echanneling.model.Prescription;
public class BasicPrescription implements PrescriptionComponent {
    private final Prescription prescription;
    public BasicPrescription(Prescription prescription) { this.prescription = prescription; }
    public String describe() { return "Prescription for appointment " + prescription.getAppointmentId(); }
}
