package org.echanneling.designpattern.factory;
import org.echanneling.model.Doctor;
import org.echanneling.model.Hospital;

/** Factory Pattern: centralizes creation of a common profile representation. */
public final class ProfileFactory {
    private ProfileFactory() {}
    public static ManagedProfile createDoctorProfile(Doctor doctor) { return new DoctorManagedProfile(doctor); }
    public static ManagedProfile createHospitalProfile(Hospital hospital) { return new HospitalManagedProfile(hospital); }
}
