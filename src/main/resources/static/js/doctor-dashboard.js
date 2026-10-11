let doctor = null;
let myApps = [];


// =====================================================
// ESCAPE HTML
// =====================================================

const esc = v =>
    String(v ?? '').replace(
        /[&<>"']/g,
        c => ({
            '&': '&amp;',
            '<': '&lt;',
            '>': '&gt;',
            '"': '&quot;',
            "'": '&#39;'
        }[c])
    );


// =====================================================
// LOAD DOCTOR DASHBOARD
// =====================================================

async function loadDoctor() {

    try {

        // Only doctors can access this page
        if (currentUser.role !== 'DOCTOR') {

            location.href = '/login';

            return;
        }


        // Load existing dashboard data
        const [
            docs,
            schedules,
            apps,
            notes,
            pres
        ] = await Promise.all([

            api('/api/doctors'),

            api('/api/schedules'),

            api('/api/appointments'),

            api('/api/consultation-notes'),

            api('/api/prescriptions')

        ]);


        // =================================================
        // FIND LOGGED-IN DOCTOR
        // =================================================

        doctor = docs.find(
            d =>
                Number(d.userId) ===
                Number(currentUser.userId)
        );


        if (!doctor) {

            throw new Error(
                'Doctor profile not found'
            );
        }


        // =================================================
        // DOCTOR SCHEDULES
        // =================================================

        const mySchedules =
            schedules.filter(
                s =>
                    Number(s.doctorId) ===
                    Number(doctor.doctorId)
            );


        const scheduleIds =
            new Set(
                mySchedules.map(
                    s => Number(s.scheduleId)
                )
            );


        // =================================================
        // DOCTOR APPOINTMENTS
        // =================================================

        myApps =
            apps.filter(
                a =>
                    scheduleIds.has(
                        Number(a.scheduleId)
                    )
            );


        // =================================================
        // DASHBOARD COUNTS
        // =================================================

        scheduleCount.textContent =
            mySchedules.length;


        appointmentCount.textContent =
            myApps.length;


        noteCount.textContent =
            notes.filter(
                n =>
                    Number(n.doctorId) ===
                    Number(doctor.doctorId)
            ).length;


        prescriptionCount.textContent =
            pres.filter(
                p =>
                    Number(p.doctorId) ===
                    Number(doctor.doctorId)
            ).length;


        // =================================================
        // APPOINTMENTS TABLE
        // =================================================

        doctorAppointments.innerHTML =

            myApps.length

                ? `
                    <div class="table-wrap">

                        <table>

                            <thead>

                            <tr>

                                <th>
                                    Appointment
                                </th>

                                <th>
                                    Schedule
                                </th>

                                <th>
                                    Reason
                                </th>

                                <th>
                                    Status
                                </th>

                                <th>
                                    Payment
                                </th>

                            </tr>

                            </thead>


                            <tbody>

                            ${
                    myApps.map(
                        a => `
                                        <tr>

                                            <td>
                                                <b>
                                                    #${a.appointmentId}
                                                </b>
                                            </td>

                                            <td>
                                                #${a.scheduleId}
                                            </td>

                                            <td>
                                                ${esc(a.reason || '-')}
                                            </td>

                                            <td>

                                                <span class="badge">
                                                    ${esc(a.appointmentStatus)}
                                                </span>

                                            </td>

                                            <td>
                                                ${esc(a.paymentStatus)}
                                            </td>

                                        </tr>
                                    `
                    ).join('')
                }

                            </tbody>

                        </table>

                    </div>
                `

                :

                '<div class="empty">' +
                'No appointments found.' +
                '</div>';


        // =================================================
        // LOAD THIS DOCTOR'S REVIEWS
        // =================================================

        await loadDoctorReviews();


    } catch (e) {

        doctorAppointments.innerHTML =
            `
                <div class="message err">
                    ${esc(e.message)}
                </div>
            `;


        console.error(
            'Doctor dashboard error:',
            e
        );
    }
}


// =====================================================
// LOAD LOGGED-IN DOCTOR REVIEWS
// =====================================================

async function loadDoctorReviews() {

    const reviewTable =
        document.getElementById(
            'doctorReviews'
        );


    if (!reviewTable) {

        return;
    }


    try {

        if (!doctor) {

            throw new Error(
                'Doctor profile not loaded'
            );
        }


        // IMPORTANT:
        // Only reviews belonging to this doctor
        const reviews =
            await api(
                '/api/reviews/doctor/' +
                doctor.doctorId
            );


        // =================================================
        // NO REVIEWS
        // =================================================

        if (
            !reviews ||
            reviews.length === 0
        ) {

            reviewTable.innerHTML = `
                <tr>

                    <td colspan="6">

                        No patient reviews yet.

                    </td>

                </tr>
            `;

            return;
        }


        // =================================================
        // SHOW REVIEWS
        // =================================================

        reviewTable.innerHTML =

            reviews.map(r => {


                // Star rating
                const rating =
                    Number(r.rating);


                const stars =
                    '★'.repeat(rating) +
                    '☆'.repeat(5 - rating);


                // Date
                let reviewDate = '-';


                if (r.createdAt) {

                    reviewDate =
                        new Date(
                            r.createdAt
                        ).toLocaleString();
                }


                return `

                    <tr>

                        <td>
                            #${r.reviewId}
                        </td>


                        <td>
                            #${r.appointmentId}
                        </td>


                        <td>
                            Patient #${r.patientId}
                        </td>


                        <td>

                            <span title="${rating}/5">

                                ${stars}

                            </span>

                            (${rating}/5)

                        </td>


                        <td>
                            ${esc(r.comment || '-')}
                        </td>


                        <td>
                            ${esc(reviewDate)}
                        </td>

                    </tr>

                `;

            }).join('');


    } catch (e) {

        reviewTable.innerHTML = `

            <tr>

                <td colspan="6">

                    Failed to load reviews:
                    ${esc(e.message)}

                </td>

            </tr>
        `;


        console.error(
            'Doctor review loading error:',
            e
        );
    }
}


// =====================================================
// CREATE CONSULTATION NOTE
// =====================================================

saveNote.onclick =
    async () => {

        try {

            if (!doctor) {

                throw new Error(
                    'Doctor profile not loaded'
                );
            }


            const appointmentId =
                Number(
                    noteAppointment.value
                );


            if (!appointmentId) {

                throw new Error(
                    'Please enter an appointment ID'
                );
            }


            await api(
                '/api/consultation-notes',
                {

                    method: 'POST',

                    headers: {
                        'Content-Type':
                            'application/json'
                    },

                    body: JSON.stringify({

                        appointmentId:
                        appointmentId,

                        doctorId:
                        doctor.doctorId,

                        symptoms:
                        symptoms.value,

                        diagnosis:
                        diagnosis.value,

                        consultationNotes:
                        notes.value

                    })

                }
            );


            msg(
                'doctorMsg',
                'Consultation note saved.'
            );


            noteAppointment.value = '';

            symptoms.value = '';

            diagnosis.value = '';

            notes.value = '';


            await loadDoctor();


        } catch (e) {

            msg(
                'doctorMsg',
                e.message,
                false
            );
        }
    };


// =====================================================
// PRESCRIPTION CRUD + MULTIPLE MEDICINES
// =====================================================

let editingPrescriptionId = null;
const medicineRows = document.getElementById('medicineRows');
const addMedicineButton = document.getElementById('addMedicineButton');
const cancelPrescriptionEdit = document.getElementById('cancelPrescriptionEdit');
const doctorPrescriptionRows = document.getElementById('doctorPrescriptionRows');

function medicineRowHtml(item = {}) {
    const row = document.createElement('div');
    row.className = 'medicine-entry';
    row.style.cssText = 'border:1px solid #e2e8f0;border-radius:14px;padding:14px;margin-bottom:12px;background:#f8fafc';
    row.innerHTML = `
        <div class="form-grid">
            <div class="field"><label>Medicine</label><select class="med-name"><option value="">Select medicine</option><option>Paracetamol</option><option>Amoxicillin</option><option>Azithromycin</option><option>Cetirizine</option><option>Loratadine</option><option>Omeprazole</option><option>Metformin</option><option>Amlodipine</option><option>Losartan</option><option>Atorvastatin</option><option>Salbutamol</option><option>Ibuprofen</option><option>Diclofenac</option><option>Vitamin C</option><option>ORS</option></select></div>
            <div class="field"><label>Dosage</label><input class="med-dosage" type="text" placeholder="Example: 500mg"></div>
            <div class="field"><label>Frequency</label><input class="med-frequency" type="text" placeholder="Example: Twice daily"></div>
            <div class="field"><label>Duration</label><input class="med-duration" type="text" placeholder="Example: 5 days"></div>
            <div class="field"><label>Instructions</label><input class="med-instructions" type="text" placeholder="Example: After meals"></div>
        </div>
        <button type="button" class="remove-medicine" style="margin-top:10px;border:0;background:transparent;color:#dc2626;cursor:pointer">
            <i class="bi bi-trash"></i> Remove Medicine
        </button>`;
    row.querySelector('.med-name').value = item.medicineName || '';
    row.querySelector('.med-dosage').value = item.dosage || '';
    row.querySelector('.med-frequency').value = item.frequency || '';
    row.querySelector('.med-duration').value = item.duration || '';
    row.querySelector('.med-instructions').value = item.instructions || '';
    row.querySelector('.remove-medicine').onclick = () => {
        if (medicineRows.querySelectorAll('.medicine-entry').length > 1) row.remove();
        else msg('prescriptionMsg', 'A prescription must contain at least one medicine.', false);
    };
    medicineRows.appendChild(row);
}

function resetPrescriptionForm() {
    editingPrescriptionId = null;
    document.getElementById('prescriptionFormTitle').textContent = 'Create prescription';
    prescriptionAppointment.disabled = false;
    prescriptionAppointment.value = '';
    medicineRows.innerHTML = '';
    medicineRowHtml();
    savePrescription.innerHTML = '<i class="bi bi-capsule"></i> Save Prescription';
    cancelPrescriptionEdit.style.display = 'none';
}

function collectMedicines() {
    return Array.from(medicineRows.querySelectorAll('.medicine-entry')).map(row => ({
        medicineName: row.querySelector('.med-name').value.trim(),
        dosage: row.querySelector('.med-dosage').value.trim(),
        frequency: row.querySelector('.med-frequency').value.trim(),
        duration: row.querySelector('.med-duration').value.trim(),
        instructions: row.querySelector('.med-instructions').value.trim()
    }));
}

async function loadDoctorPrescriptions() {
    if (!doctor || !doctorPrescriptionRows) return;
    try {
        const all = await api('/api/prescriptions');
        const mine = all.filter(p => Number(p.doctorId) === Number(doctor.doctorId));
        if (!mine.length) {
            doctorPrescriptionRows.innerHTML = '<tr><td colspan="5">No prescriptions issued yet.</td></tr>';
            return;
        }
        doctorPrescriptionRows.innerHTML = mine.map(p => `
            <tr>
                <td>#${p.prescriptionId}</td>
                <td>#${p.appointmentId}</td>
                <td>${(p.items || []).map(i => escapePrescriptionHtml(i.medicineName)).join(', ') || '-'}</td>
                <td>${p.createdAt ? new Date(p.createdAt).toLocaleDateString() : '-'}</td>
                <td>
                    <button class="btn" type="button" onclick="editPrescription(${p.prescriptionId})">Edit</button>
                    <button class="btn" type="button" onclick="deletePrescription(${p.prescriptionId})">Delete</button>
                </td>
            </tr>`).join('');
    } catch (e) {
        doctorPrescriptionRows.innerHTML = `<tr><td colspan="5">${escapePrescriptionHtml(e.message)}</td></tr>`;
    }
}

window.editPrescription = async function(id) {
    try {
        const p = await api('/api/prescriptions/' + id);
        if (Number(p.doctorId) !== Number(doctor.doctorId)) throw new Error('You can edit only your own prescriptions');
        editingPrescriptionId = id;
        document.getElementById('prescriptionFormTitle').textContent = 'Edit prescription #' + id;
        prescriptionAppointment.value = p.appointmentId;
        prescriptionAppointment.disabled = true;
        medicineRows.innerHTML = '';
        (p.items || []).forEach(medicineRowHtml);
        if (!(p.items || []).length) medicineRowHtml();
        savePrescription.innerHTML = '<i class="bi bi-check-circle"></i> Update Prescription';
        cancelPrescriptionEdit.style.display = 'inline-flex';
        document.getElementById('prescription').scrollIntoView({behavior:'smooth'});
    } catch(e) { msg('prescriptionMsg', e.message, false); }
};

window.deletePrescription = async function(id) {
    if (!confirm('Delete prescription #' + id + '? This will also delete its medicines.')) return;
    try {
        await api('/api/prescriptions/' + id, {method:'DELETE'});
        msg('prescriptionMsg', 'Prescription deleted successfully.');
        if (editingPrescriptionId === id) resetPrescriptionForm();
        await loadDoctorPrescriptions();
        await loadDoctor();
    } catch(e) { msg('prescriptionMsg', e.message, false); }
};

addMedicineButton.onclick = () => medicineRowHtml();
cancelPrescriptionEdit.onclick = resetPrescriptionForm;

savePrescription.onclick = async () => {
    try {
        if (!doctor) throw new Error('Doctor profile not loaded');
        const appointmentId = Number(prescriptionAppointment.value);
        if (!appointmentId) throw new Error('Please enter an appointment ID');
        const items = collectMedicines();
        if (!items.length) throw new Error('Please add at least one medicine');
        items.forEach((i, index) => {
            if (!i.medicineName || !i.dosage || !i.frequency || !i.duration) {
                throw new Error('Complete Medicine ' + (index + 1) + ' name, dosage, frequency and duration');
            }
        });
        const url = editingPrescriptionId ? '/api/prescriptions/' + editingPrescriptionId : '/api/prescriptions';
        const method = editingPrescriptionId ? 'PUT' : 'POST';
        await api(url, {
            method,
            headers:{'Content-Type':'application/json'},
            body:JSON.stringify({appointmentId, doctorId:doctor.doctorId, items})
        });
        msg('prescriptionMsg', editingPrescriptionId ? 'Prescription updated successfully.' : 'Prescription saved successfully.');
        resetPrescriptionForm();
        await loadDoctorPrescriptions();
        await loadDoctor();
    } catch(e) { msg('prescriptionMsg', e.message, false); }
};

function escapePrescriptionHtml(value) {
    const d = document.createElement('div'); d.textContent = String(value == null ? '' : value); return d.innerHTML;
}

medicineRowHtml();
setTimeout(loadDoctorPrescriptions, 500);

// =====================================================
// START
// =====================================================

loadDoctor();