# Design Patterns - Group 26-MTR-SE2030-05

All six major functions have a design-pattern implementation that is connected to the real service workflow (not standalone demo classes).

1. Doctor Search & Appointment Booking — Factory Method
   - `designpattern/factory/AppointmentFactory.java`
   - Used by `AppointmentService.bookAppointment()` to construct/default a new appointment.

2. Online Payment & Invoice — Strategy
   - `PaymentStrategy`, `CardPaymentStrategy`, `BankTransferPaymentStrategy`, `PaymentStrategyContext`
   - `PaymentService` explicitly injects both concrete strategies, selects one in each payment flow, and executes it through the context.
   - IntelliJ Find Usages on either concrete strategy should show `PaymentService`.

3. Doctor Schedule Management — Singleton
   - `SchedulePolicy.java`
   - `DoctorScheduleService` calls `SchedulePolicy.getInstance().validate(...)` for create/update schedule flows.

4. Doctor & Hospital Profile Management — Factory
   - `ManagedProfile`, `DoctorManagedProfile`, `HospitalManagedProfile`, `ProfileFactory`
   - `DoctorService` and `HospitalService` create their managed profile through `ProfileFactory`.

5. Digital Prescription & Consultation Notes — Decorator
   - `PrescriptionComponent`, `BasicPrescription`, `PrescriptionDecorator`, `MedicineItemDecorator`
   - `PrescriptionService` builds and executes a decorator chain for every medicine in the prescription.

6. System Feedback — Observer
   - `SystemFeedbackObserver`, `SystemFeedbackAuditObserver`, `SystemFeedbackSubject`
   - `DoctorReviewService` (legacy service name retained for API compatibility) notifies observers on CREATE/UPDATE/DELETE feedback events.

## IntelliJ verification
Right-click the named pattern class -> Find Usages. Each pattern has a real usage under `service/`.
