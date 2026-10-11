package org.echanneling.designpattern.factory;
import org.echanneling.model.Hospital;
public class HospitalManagedProfile implements ManagedProfile {
    private final Hospital hospital;
    public HospitalManagedProfile(Hospital hospital) { this.hospital = hospital; }
    public int getId() { return hospital.getHospitalId(); }
    public String getDisplayName() { return hospital.getHospitalName(); }
    public String getType() { return "HOSPITAL"; }
}
