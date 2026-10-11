package org.echanneling.service;

import org.echanneling.model.Appointment;
import org.echanneling.model.DoctorSchedule;
import org.echanneling.model.Prescription;
import org.echanneling.model.PrescriptionItem;
import org.echanneling.designpattern.decorator.BasicPrescription;
import org.echanneling.designpattern.decorator.MedicineItemDecorator;
import org.echanneling.designpattern.decorator.PrescriptionComponent;

import org.echanneling.repository.AppointmentRepository;
import org.echanneling.repository.DoctorScheduleRepository;
import org.echanneling.repository.PrescriptionItemRepository;
import org.echanneling.repository.PrescriptionRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;

@Service
public class PrescriptionService {

    private final PrescriptionRepository prescriptionRepository;
    private final PrescriptionItemRepository prescriptionItemRepository;
    private final AppointmentRepository appointmentRepository;
    private final DoctorScheduleRepository doctorScheduleRepository;


    public PrescriptionService(
            PrescriptionRepository prescriptionRepository,
            PrescriptionItemRepository prescriptionItemRepository,
            AppointmentRepository appointmentRepository,
            DoctorScheduleRepository doctorScheduleRepository) {

        this.prescriptionRepository = prescriptionRepository;
        this.prescriptionItemRepository = prescriptionItemRepository;
        this.appointmentRepository = appointmentRepository;
        this.doctorScheduleRepository = doctorScheduleRepository;
    }


    // ==========================================
    // CREATE PRESCRIPTION
    // ==========================================
    @Transactional
    public Prescription createPrescription(
            Prescription prescription) {

        // Check appointment exists
        Appointment appointment =
                appointmentRepository.findById(
                        prescription.getAppointmentId()
                );


        // Doctor ID validation
        if (prescription.getDoctorId() <= 0) {

            throw new IllegalArgumentException(
                    "Doctor ID is required"
            );
        }


        // ==========================================
        // CHECK DOCTOR BELONGS TO THIS APPOINTMENT
        // ==========================================
        DoctorSchedule schedule =
                doctorScheduleRepository.findById(
                        appointment.getScheduleId()
                );


        if (schedule.getDoctorId()
                != prescription.getDoctorId()) {

            throw new IllegalArgumentException(
                    "This doctor is not assigned to this appointment"
            );
        }


        // Only one prescription per appointment
        if (prescriptionRepository
                .existsByAppointmentId(
                        prescription.getAppointmentId()
                )) {

            throw new IllegalStateException(
                    "Prescription already exists for this appointment"
            );
        }


        // Prescription must contain medicine
        if (prescription.getItems() == null ||
                prescription.getItems().isEmpty()) {

            throw new IllegalArgumentException(
                    "Prescription must contain at least one medicine"
            );
        }


        // Validate all items before saving
        for (PrescriptionItem item :
                prescription.getItems()) {

            validateItem(item);
        }


        // Decorator Pattern: dynamically add each medicine's descriptive behavior
        // around the base prescription without changing the Prescription model class.
        PrescriptionComponent decoratedPrescription = new BasicPrescription(prescription);
        for (PrescriptionItem item : prescription.getItems()) {
            decoratedPrescription = new MedicineItemDecorator(decoratedPrescription, item);
        }
        // Build the decorated description now so the decorator chain is exercised in the workflow.
        decoratedPrescription.describe();

        // Save parent Prescription first
        int prescriptionId =
                prescriptionRepository
                        .saveAndReturnId(
                                prescription
                        );


        // Save child PrescriptionItems
        for (PrescriptionItem item :
                prescription.getItems()) {

            item.setPrescriptionId(
                    prescriptionId
            );

            prescriptionItemRepository.save(
                    item
            );
        }


        return getPrescriptionById(
                prescriptionId
        );
    }


    // ==========================================
    // GET ALL PRESCRIPTIONS
    // ==========================================
    public ArrayList<Prescription>
    getAllPrescriptions() {

        ArrayList<Prescription> prescriptions =
                prescriptionRepository.findAll();


        for (Prescription prescription :
                prescriptions) {

            ArrayList<PrescriptionItem> items =
                    prescriptionItemRepository
                            .findByPrescriptionId(
                                    prescription
                                            .getPrescriptionId()
                            );

            prescription.setItems(items);
        }


        return prescriptions;
    }


    // ==========================================
    // GET PRESCRIPTION BY ID
    // ==========================================
    public Prescription getPrescriptionById(
            int prescriptionId) {

        Prescription prescription =
                prescriptionRepository
                        .findById(
                                prescriptionId
                        );


        ArrayList<PrescriptionItem> items =
                prescriptionItemRepository
                        .findByPrescriptionId(
                                prescriptionId
                        );


        prescription.setItems(items);

        return prescription;
    }


    // ==========================================
    // GET PRESCRIPTION BY APPOINTMENT
    // ==========================================
    public Prescription getByAppointment(
            int appointmentId) {

        Prescription prescription =
                prescriptionRepository
                        .findByAppointmentId(
                                appointmentId
                        );


        ArrayList<PrescriptionItem> items =
                prescriptionItemRepository
                        .findByPrescriptionId(
                                prescription
                                        .getPrescriptionId()
                        );


        prescription.setItems(items);

        return prescription;
    }


    // ==========================================
    // UPDATE PRESCRIPTION
    // ==========================================
    @Transactional
    public Prescription updatePrescription(
            int prescriptionId,
            Prescription prescription) {

        // Check existing prescription
        Prescription existingPrescription =
                prescriptionRepository
                        .findById(
                                prescriptionId
                        );


        // Must contain at least one medicine
        if (prescription.getItems() == null ||
                prescription.getItems().isEmpty()) {

            throw new IllegalArgumentException(
                    "Prescription must contain at least one medicine"
            );
        }


        // Validate all new items BEFORE deleting old items
        for (PrescriptionItem item :
                prescription.getItems()) {

            validateItem(item);
        }


        // Keep original appointment
        prescription.setAppointmentId(
                existingPrescription
                        .getAppointmentId()
        );


        // Keep original doctor
        // Doctor cannot be changed during prescription update
        prescription.setDoctorId(
                existingPrescription
                        .getDoctorId()
        );


        // Update parent
        int result =
                prescriptionRepository.update(
                        prescriptionId,
                        prescription
                );


        if (result <= 0) {

            throw new RuntimeException(
                    "Prescription update failed"
            );
        }


        // Remove old child items
        prescriptionItemRepository
                .deleteByPrescriptionId(
                        prescriptionId
                );


        // Add updated child items
        for (PrescriptionItem item :
                prescription.getItems()) {

            item.setPrescriptionId(
                    prescriptionId
            );

            prescriptionItemRepository.save(
                    item
            );
        }


        return getPrescriptionById(
                prescriptionId
        );
    }


    // ==========================================
    // DELETE PRESCRIPTION
    // ==========================================
    @Transactional
    public String deletePrescription(
            int prescriptionId) {

        // Check prescription exists
        prescriptionRepository.findById(
                prescriptionId
        );


        int result =
                prescriptionRepository.delete(
                        prescriptionId
                );


        if (result <= 0) {

            throw new RuntimeException(
                    "Prescription deletion failed"
            );
        }


        return "Prescription deleted successfully";
    }


    // ==========================================
    // VALIDATE MEDICINE ITEM
    // ==========================================
    private void validateItem(
            PrescriptionItem item) {

        if (item.getMedicineName() == null ||
                item.getMedicineName().isBlank()) {

            throw new IllegalArgumentException(
                    "Medicine name is required"
            );
        }


        if (item.getDosage() == null ||
                item.getDosage().isBlank()) {

            throw new IllegalArgumentException(
                    "Dosage is required"
            );
        }


        if (item.getFrequency() == null ||
                item.getFrequency().isBlank()) {

            throw new IllegalArgumentException(
                    "Frequency is required"
            );
        }


        if (item.getDuration() == null ||
                item.getDuration().isBlank()) {

            throw new IllegalArgumentException(
                    "Duration is required"
            );
        }
    }
}