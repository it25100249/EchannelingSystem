package org.echanneling.designpattern.factory;

import org.echanneling.model.Appointment;

/** Factory Method participant for appointment creation. */
public final class AppointmentFactory {
    private AppointmentFactory() {}

    public static Appointment createPatientAppointment(Appointment request, int appointmentNumber) {
        request.setAppointmentNumber(appointmentNumber);
        request.setAppointmentStatus("PENDING");
        request.setPaymentStatus("UNPAID");
        return request;
    }
}
