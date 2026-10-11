package org.echanneling.designpattern.factory;
import org.echanneling.model.Doctor;
public class DoctorManagedProfile implements ManagedProfile {
    private final Doctor doctor;
    public DoctorManagedProfile(Doctor doctor) { this.doctor = doctor; }
    public int getId() { return doctor.getDoctorId(); }
    public String getDisplayName() { return doctor.getFullName(); }
    public String getType() { return "DOCTOR"; }
}
