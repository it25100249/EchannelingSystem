let adminData = {
    docs: [],
    hos: [],
    sch: [],
    pays: [],
    reviews: []
};

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
// DASHBOARD MODALS (no browser prompt/confirm popups)
// =====================================================
function dashboardModal(title, fields, dangerText = null) {
    return new Promise(resolve => {
        const overlay = document.createElement('div');
        overlay.style.cssText = 'position:fixed;inset:0;background:rgba(15,23,42,.55);display:flex;align-items:center;justify-content:center;z-index:9999;padding:20px';
        const inputs = fields.map((f,i) => `<label style="display:block;margin:12px 0 6px;font-weight:700">${esc(f.label)}</label><input id="dashModal${i}" type="${f.type||'text'}" value="${esc(f.value ?? '')}" style="width:100%;padding:11px;border:1px solid #cbd5e1;border-radius:9px">`).join('');
        overlay.innerHTML = `<div style="background:white;width:min(520px,100%);border-radius:16px;padding:24px;box-shadow:0 24px 60px rgba(0,0,0,.25)"><h2 style="margin:0 0 8px">${esc(title)}</h2>${dangerText?`<p style="color:#b91c1c">${esc(dangerText)}</p>`:''}${inputs}<div style="display:flex;justify-content:flex-end;gap:10px;margin-top:22px"><button class="btn secondary" id="dashCancel">Cancel</button><button class="btn ${dangerText?'danger':''}" id="dashSave">${dangerText?'Confirm':'Save Changes'}</button></div></div>`;
        document.body.appendChild(overlay);
        const close = v => { overlay.remove(); resolve(v); };
        overlay.querySelector('#dashCancel').onclick = () => close(null);
        overlay.onclick = e => { if(e.target===overlay) close(null); };
        overlay.querySelector('#dashSave').onclick = () => close(fields.map((_,i)=>overlay.querySelector('#dashModal'+i).value));
    });
}

async function dashboardConfirm(message) {
    const result = await dashboardModal('Please Confirm', [], message);
    return result !== null;
}

// =====================================================
// LOAD ADMIN DATA
// =====================================================

async function loadAdmin() {

    try {

        if (currentUser.role !== 'ADMIN') {
            location.href = '/login';
            return;
        }

        const [docs, hos, sch, pays, reviews] = await Promise.all([
            api('/api/doctors'),
            api('/api/hospitals'),
            api('/api/schedules'),
            api('/api/payments'),
            api('/api/reviews')
        ]);

        adminData = {
            docs,
            hos,
            sch,
            pays,
            reviews
        };


        // =================================================
        // COUNTS
        // =================================================

        doctorCount.textContent = docs.length;
        hospitalCount.textContent = hos.length;
        scheduleCount.textContent = sch.length;


        // =================================================
        // PENDING BANK PAYMENTS
        // =================================================

        const pending = pays.filter(
            p =>
                p.paymentMethod === 'BANK_TRANSFER' &&
                p.paymentStatus === 'PENDING'
        );

        pendingCount.textContent = pending.length;


        adminPayments.innerHTML =
            pending.map(p => `
                <tr>

                    <td>
                        #${p.paymentId}
                    </td>

                    <td>
                        #${p.appointmentId}
                    </td>

                    <td>
                        Rs. ${Number(p.amount).toFixed(2)}
                    </td>

                    <td>
                        <span class="badge warn">
                            PENDING
                        </span>
                    </td>

                    <td>
                        <a
                            class="btn secondary"
                            target="_blank"
                            href="/api/payments/${p.paymentId}/bank-slip"
                        >
                            View slip
                        </a>
                    </td>

                    <td class="actions">

                        <button
                            class="btn"
                            onclick="decide(${p.paymentId}, true)"
                        >
                            Approve
                        </button>

                        <button
                            class="btn danger"
                            onclick="decide(${p.paymentId}, false)"
                        >
                            Reject
                        </button>

                    </td>

                </tr>
            `).join('') ||

            `
                <tr>
                    <td colspan="6">
                        No pending bank transfers.
                    </td>
                </tr>
            `;


        // =================================================
        // REFUND REQUESTS
        // =================================================

        const refundRequests = pays.filter(
            p => p.refundStatus === 'REFUND_PENDING'
        );

        const adminRefunds =
            document.getElementById('adminRefunds');


        if (adminRefunds) {

            adminRefunds.innerHTML =
                refundRequests.map(p => {

                    let requestedDate = '-';

                    if (p.refundRequestedAt) {

                        requestedDate =
                            new Date(
                                p.refundRequestedAt
                            ).toLocaleString();
                    }


                    return `
                        <tr>

                            <td>
                                #${p.paymentId}
                            </td>

                            <td>
                                #${p.appointmentId}
                            </td>

                            <td>
                                ${esc(p.paymentMethod)}
                            </td>

                            <td>
                                Rs. ${Number(p.amount).toFixed(2)}
                            </td>

                            <td>
                                ${esc(requestedDate)}
                            </td>

                            <td style="max-width:260px; white-space:normal;">
                                ${esc(p.refundReason || '-')}
                            </td>

                            <td>
                                <span class="badge warn">
                                    REFUND PENDING
                                </span>
                            </td>

                            <td>
                                <button
                                    class="btn"
                                    onclick="processRefund(${p.paymentId})"
                                >
                                    Process Refund
                                </button>
                            </td>

                        </tr>
                    `;

                }).join('')

                ||

                `
                    <tr>
                        <td colspan="8">
                            No pending refund requests.
                        </td>
                    </tr>
                `;
        }


        // =================================================
        // HOSPITALS
        // =================================================

        adminHospitals.innerHTML =
            hos.map(h => `
                <div class="card" style="margin:10px 0">

                    <b>
                        ${esc(h.hospitalName)}
                    </b>

                    <div class="sub">
                        ${esc(h.city)} · ${esc(h.address)}
                    </div>

                    <div class="actions">

                        <button
                            class="btn secondary"
                            onclick="editHospital(${h.hospitalId})"
                        >
                            Edit
                        </button>

                        <button
                            class="btn danger"
                            onclick="removeHospital(${h.hospitalId})"
                        >
                            Delete
                        </button>

                    </div>

                </div>
            `).join('');


        // =================================================
        // DOCTORS
        // =================================================

        adminDoctors.innerHTML =
            docs.map(d => `
                <div class="card" style="margin:10px 0">

                    <b>
                        ${esc(d.fullName)}
                    </b>

                    <div class="sub">

                        ${esc(d.specialization)}
                        ·
                        ${esc(d.licenseNumber)}
                        ·
                        Rs. ${Number(d.consultationFee).toFixed(2)}

                    </div>

                    <div class="actions">

                        <button
                            class="btn secondary"
                            onclick="editDoctor(${d.doctorId})"
                        >
                            Edit
                        </button>

                        <button
                            class="btn danger"
                            onclick="removeDoctor(${d.doctorId})"
                        >
                            Delete
                        </button>

                    </div>

                </div>
            `).join('');


        // =================================================
        // SCHEDULES
        // =================================================

        adminSchedules.innerHTML =
            '<div class="table-wrap">' +
            '<table>' +

            '<thead>' +
            '<tr>' +
            '<th>ID</th>' +
            '<th>Doctor</th>' +
            '<th>Hospital</th>' +
            '<th>Date</th>' +
            '<th>Time</th>' +
            '<th>Max Patients</th>' +
            '<th>Status</th>' +
            '<th>Action</th>' +
            '</tr>' +
            '</thead>' +

            '<tbody>' +

            sch.map(s => `
                <tr>

                    <td>
                        #${s.scheduleId}
                    </td>

                    <td>
                        #${s.doctorId}
                    </td>

                    <td>
                        #${s.hospitalId}
                    </td>

                    <td>
                        ${esc(s.scheduleDate)}
                    </td>

                    <td>
                        ${esc(s.startTime)} - ${esc(s.endTime)}
                    </td>

                    <td>
                        ${s.maxPatients}
                    </td>

                    <td>
                        <span class="badge">
                            ${esc(s.status)}
                        </span>
                    </td>

                    <td class="actions">

                        <button
                            class="btn secondary"
                            onclick="editSchedule(${s.scheduleId})"
                        >
                            Edit
                        </button>

                        <button
                            class="btn danger"
                            onclick="removeSchedule(${s.scheduleId})"
                        >
                            Delete
                        </button>

                    </td>

                </tr>
            `).join('') +

            '</tbody>' +
            '</table>' +
            '</div>';


        // =================================================
        // DOCTOR SELECT
        // =================================================

        sDoctor.innerHTML =
            '<option value="">Select doctor</option>' +

            docs.map(d => `
                <option value="${d.doctorId}">
                    ${esc(d.fullName)} - ${esc(d.specialization)}
                </option>
            `).join('');


        // =================================================
        // HOSPITAL SELECT
        // =================================================

        sHospital.innerHTML =
            '<option value="">Select hospital</option>' +

            hos.map(h => `
                <option value="${h.hospitalId}">
                    ${esc(h.hospitalName)}
                </option>
            `).join('');


    } catch (e) {

        console.error(
            'Admin loading error:',
            e
        );

        msg(
            'adminMsg',
            e.message,
            false
        );
    }
}


// =====================================================
// APPROVE / REJECT BANK PAYMENT
// =====================================================

async function decide(id, approve) {

    try {

        const action =
            approve
                ? 'approve'
                : 'reject';

        await api(
            '/api/payments/' +
            id +
            '/' +
            action,
            {
                method: 'PUT'
            }
        );

        msg(
            'adminMsg',
            approve
                ? 'Payment approved.'
                : 'Payment rejected.'
        );

        await loadAdmin();

    } catch (e) {

        msg(
            'adminMsg',
            e.message,
            false
        );
    }
}


// =====================================================
// ADMIN - PROCESS REFUND
// =====================================================

async function processRefund(paymentId) {

    const confirmed = await dashboardConfirm('Process refund for Payment #' + paymentId + '?');

    if (!confirmed) {
        return;
    }


    try {

        await api(
            '/api/payments/' +
            paymentId +
            '/refund',
            {
                method: 'PUT'
            }
        );

        msg(
            'adminMsg',
            'Refund processed successfully for Payment #' +
            paymentId +
            '.'
        );

        await loadAdmin();

    } catch (e) {

        msg(
            'adminMsg',
            e.message,
            false
        );
    }
}


// =====================================================
// CREATE HOSPITAL
// =====================================================

addHospital.onclick = async () => {

    try {

        await api(
            '/api/hospitals',
            {
                method: 'POST',

                headers: {
                    'Content-Type': 'application/json'
                },

                body: JSON.stringify({

                    hospitalName: hName.value,
                    address: hAddress.value,
                    city: hCity.value,
                    phone: hPhone.value,
                    email: hEmail.value

                })
            }
        );

        msg(
            'adminMsg',
            'Hospital created.'
        );

        await loadAdmin();

    } catch (e) {

        msg(
            'adminMsg',
            e.message,
            false
        );
    }
};


// =====================================================
// CREATE DOCTOR
// =====================================================

addDoctor.onclick = async () => {

    try {

        if (!dPassword.value) {

            throw new Error(
                'Temporary password is required'
            );
        }

        await api(
            '/api/doctors?password=' +
            encodeURIComponent(
                dPassword.value
            ),
            {
                method: 'POST',

                headers: {
                    'Content-Type': 'application/json'
                },

                body: JSON.stringify({

                    fullName: dName.value,
                    email: dEmail.value,
                    phone: dPhone.value,
                    specialization: dSpec.value,
                    licenseNumber: dLicense.value,
                    consultationFee:
                        Number(dFee.value)

                })
            }
        );

        msg(
            'adminMsg',
            'Doctor created.'
        );

        await loadAdmin();

    } catch (e) {

        msg(
            'adminMsg',
            e.message,
            false
        );
    }
};


// =====================================================
// ASSIGN DOCTOR TO HOSPITAL
// =====================================================

assignDoctor.onclick = async () => {

    try {

        if (
            !sDoctor.value ||
            !sHospital.value
        ) {

            throw new Error(
                'Select doctor and hospital'
            );
        }

        await api(
            '/api/hospitals/' +
            sHospital.value +
            '/doctors/' +
            sDoctor.value,
            {
                method: 'POST'
            }
        );

        msg(
            'adminMsg',
            'Doctor assigned to hospital.'
        );

    } catch (e) {

        msg(
            'adminMsg',
            e.message,
            false
        );
    }
};


// =====================================================
// CREATE SCHEDULE
// =====================================================

addSchedule.onclick = async () => {

    try {

        if (
            !sDoctor.value ||
            !sHospital.value
        ) {

            throw new Error(
                'Select doctor and hospital'
            );
        }

        if (
            !sDate.value ||
            !sStart.value ||
            !sEnd.value ||
            !sMax.value
        ) {

            throw new Error(
                'Complete all schedule fields'
            );
        }

        await api(
            '/api/schedules',
            {
                method: 'POST',

                headers: {
                    'Content-Type': 'application/json'
                },

                body: JSON.stringify({

                    doctorId:
                        Number(sDoctor.value),

                    hospitalId:
                        Number(sHospital.value),

                    scheduleDate:
                    sDate.value,

                    startTime:
                    sStart.value,

                    endTime:
                    sEnd.value,

                    maxPatients:
                        Number(sMax.value),

                    status:
                        'AVAILABLE'

                })
            }
        );

        msg(
            'adminMsg',
            'Schedule created.'
        );

        await loadAdmin();

    } catch (e) {

        msg(
            'adminMsg',
            e.message,
            false
        );
    }
};


// =====================================================
// EDIT HOSPITAL
// =====================================================

async function editHospital(id) {
    const h = adminData.hos.find(x => Number(x.hospitalId) === Number(id));
    if (!h) return;
    const v = await dashboardModal('Edit Hospital', [
        {label:'Hospital Name', value:h.hospitalName}, {label:'City', value:h.city},
        {label:'Address', value:h.address}, {label:'Phone', value:h.phone}, {label:'Email', value:h.email}
    ]);
    if (!v) return;
    try {
        await api('/api/hospitals/' + id, {method:'PUT', headers:{'Content-Type':'application/json'}, body:JSON.stringify({...h,hospitalName:v[0].trim(),city:v[1].trim(),address:v[2].trim(),phone:v[3].trim(),email:v[4].trim()})});
        msg('adminMsg','Hospital updated.'); await loadAdmin();
    } catch(e) { msg('adminMsg',e.message,false); }
}

// =====================================================
// EDIT DOCTOR
// =====================================================

async function editDoctor(id) {
    const d = adminData.docs.find(x => Number(x.doctorId) === Number(id));
    if (!d) return;
    const v = await dashboardModal('Edit Doctor', [{label:'Specialization',value:d.specialization},{label:'Consultation Fee',value:d.consultationFee,type:'number'}]);
    if (!v) return; const fee=Number(v[1]);
    if (!v[0].trim() || Number.isNaN(fee) || fee < 0) { msg('adminMsg','Enter valid doctor details.',false); return; }
    try { await api('/api/doctors/'+id,{method:'PUT',headers:{'Content-Type':'application/json'},body:JSON.stringify({...d,specialization:v[0].trim(),consultationFee:fee})}); msg('adminMsg','Doctor updated.'); await loadAdmin(); } catch(e){msg('adminMsg',e.message,false);}
}

// =====================================================
// EDIT SCHEDULE
// =====================================================

async function editSchedule(id) {
    const s = adminData.sch.find(x => Number(x.scheduleId) === Number(id));
    if (!s) return;
    const v=await dashboardModal('Edit Schedule',[{label:'Schedule Date',value:s.scheduleDate,type:'date'},{label:'Start Time',value:String(s.startTime).substring(0,5),type:'time'},{label:'End Time',value:String(s.endTime).substring(0,5),type:'time'},{label:'Max Patients',value:s.maxPatients,type:'number'}]);
    if(!v)return; const max=Number(v[3]); if(!v[0]||!v[1]||!v[2]||Number.isNaN(max)||max<=0){msg('adminMsg','Enter valid schedule details.',false);return;}
    try{await api('/api/schedules/'+id,{method:'PUT',headers:{'Content-Type':'application/json'},body:JSON.stringify({...s,scheduleDate:v[0],startTime:v[1],endTime:v[2],maxPatients:max})});msg('adminMsg','Schedule updated successfully.');await loadAdmin();}catch(e){msg('adminMsg',e.message,false);}
}

// =====================================================
// DELETE HOSPITAL
// =====================================================

async function removeHospital(id) {

    if (
        !(await dashboardConfirm('Delete this hospital?'))
    ) {
        return;
    }


    try {

        await api(
            '/api/hospitals/' + id,
            {
                method: 'DELETE'
            }
        );

        msg(
            'adminMsg',
            'Hospital deleted.'
        );

        await loadAdmin();

    } catch (e) {

        msg(
            'adminMsg',
            e.message,
            false
        );
    }
}


// =====================================================
// DELETE DOCTOR
// =====================================================

async function removeDoctor(id) {

    if (
        !(await dashboardConfirm('Delete this doctor?'))
    ) {
        return;
    }


    try {

        await api(
            '/api/doctors/' + id,
            {
                method: 'DELETE'
            }
        );

        msg(
            'adminMsg',
            'Doctor deleted.'
        );

        await loadAdmin();

    } catch (e) {

        msg(
            'adminMsg',
            e.message,
            false
        );
    }
}


// =====================================================
// DELETE SCHEDULE
// =====================================================

async function removeSchedule(id) {

    if (
        !(await dashboardConfirm('Delete this schedule?'))
    ) {
        return;
    }


    try {

        await api(
            '/api/schedules/' + id,
            {
                method: 'DELETE'
            }
        );

        msg(
            'adminMsg',
            'Schedule deleted.'
        );

        await loadAdmin();

    } catch (e) {

        msg(
            'adminMsg',
            e.message,
            false
        );
    }
}


// =====================================================
// LOAD DOCTOR REVIEWS FOR ADMIN
// =====================================================

async function loadAdminReviews() {

    try {

        const reviews =
            await api('/api/reviews');

        const doctors =
            adminData.docs;


        const reviewTable =
            document.getElementById(
                'adminReviews'
            );


        if (!reviewTable) {
            return;
        }


        if (
            !reviews ||
            reviews.length === 0
        ) {

            reviewTable.innerHTML = `
                <tr>
                    <td colspan="8">
                        No system feedback available.
                    </td>
                </tr>
            `;

            return;
        }


        reviewTable.innerHTML =
            reviews.map(r => {

                const feedbackType = 'MediConnect System';


                const ratingNumber =
                    Math.max(
                        0,
                        Math.min(
                            5,
                            Number(r.rating) || 0
                        )
                    );


                const stars =
                    '★'.repeat(ratingNumber) +
                    '☆'.repeat(
                        5 - ratingNumber
                    );


                let date = '-';

                if (r.createdAt) {

                    date =
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
                            ${feedbackType}
                        </td>

                        <td>
                            Patient #${r.patientId}
                        </td>

                        <td>
                            #${r.appointmentId}
                        </td>

                        <td>
                            ${stars}
                            (${ratingNumber}/5)
                        </td>

                        <td>
                            ${esc(r.comment || '-')}
                        </td>

                        <td>
                            ${esc(date)}
                        </td>
                        <td><button class="btn danger" onclick="deleteSystemFeedback(${r.reviewId})">Delete</button></td>

                    </tr>
                `;

            }).join('');


    } catch (e) {

        const reviewTable =
            document.getElementById(
                'adminReviews'
            );


        if (reviewTable) {

            reviewTable.innerHTML = `
                <tr>
                    <td colspan="8">
                        Failed to load reviews.
                    </td>
                </tr>
            `;
        }

        console.error(
            'Review loading error:',
            e
        );
    }
}



window.deleteSystemFeedback = async function(reviewId) {
    if (!(await dashboardConfirm('Delete feedback #' + reviewId + '?'))) return;
    try {
        await api('/api/reviews/' + reviewId, {method:'DELETE'});
        msg('adminMsg', 'System feedback deleted successfully.');
        await loadAdminReviews();
    } catch (e) {
        msg('adminMsg', e.message, false);
    }
};

// =====================================================
// START ADMIN DASHBOARD
// =====================================================

async function startAdminDashboard() {

    await loadAdmin();

    await loadAdminReviews();
}

startAdminDashboard();