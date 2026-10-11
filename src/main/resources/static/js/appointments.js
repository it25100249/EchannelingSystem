const raw = sessionStorage.getItem('loggedInUser');

if (!raw) {
    location.href = '/login';
}

const loggedInUser = JSON.parse(raw);

if (loggedInUser.role !== 'PATIENT') {
    location.href = '/login';
}


// ==========================================
// USER INFORMATION
// ==========================================

const topUserName =
    document.getElementById('topUserName');

const userAvatar =
    document.getElementById('userAvatar');

topUserName.textContent =
    loggedInUser.fullName || 'Patient';

userAvatar.textContent =
    (loggedInUser.fullName || 'P')
        .charAt(0)
        .toUpperCase();


// ==========================================
// GLOBAL DATA
// ==========================================

let patientAppointments = [];
let enriched = [];

let cancelId = null;
let cancelPaymentStatus = null;


// ==========================================
// DOM ELEMENTS
// ==========================================

const list =
    document.getElementById('appointmentList');

const loading =
    document.getElementById('loadingState');

const empty =
    document.getElementById('emptyState');

const err =
    document.getElementById('errorMessage');

const refundReasonSection =
    document.getElementById('refundReasonSection');

const refundReasonInput =
    document.getElementById('refundReason');

const reasonCharacterCount =
    document.getElementById('reasonCharacterCount');

const cancelMessage =
    document.getElementById('cancelMessage');


// ==========================================
// API HELPER
// ==========================================

async function getJson(url, opt) {

    const response =
        await fetch(url, opt);

    const text =
        await response.text();

    if (!response.ok) {
        throw new Error(
            text || 'Request failed'
        );
    }

    try {
        return JSON.parse(text);
    } catch (e) {
        return text;
    }
}


// ==========================================
// LOAD APPOINTMENTS
// ==========================================

async function loadAppointments() {

    loading.classList.remove('hidden');
    empty.classList.add('hidden');
    err.classList.add('hidden');

    try {

        const apps =
            await getJson(
                '/api/appointments'
            );


        patientAppointments =
            apps
                .filter(
                    a =>
                        Number(a.patientId) ===
                        Number(loggedInUser.userId)
                )
                .sort(
                    (a, b) =>
                        b.appointmentId -
                        a.appointmentId
                );


        enriched =
            await Promise.all(

                patientAppointments.map(
                    async a => {

                        try {

                            const schedule =
                                await getJson(
                                    '/api/schedules/' +
                                    a.scheduleId +
                                    '/details'
                                );

                            return {
                                ...a,
                                schedule: schedule
                            };

                        } catch (e) {

                            return {
                                ...a,
                                schedule: null
                            };
                        }
                    }
                )
            );


        loading.classList.add('hidden');

        updateCounts();

        display(enriched);

    } catch (e) {

        loading.classList.add('hidden');

        err.classList.remove('hidden');

        err.querySelector('span')
            .textContent = e.message;
    }
}


// ==========================================
// UPDATE SUMMARY COUNTS
// ==========================================

function updateCounts() {

    document.getElementById(
        'totalAppointments'
    ).textContent =
        enriched.length;


    document.getElementById(
        'activeAppointments'
    ).textContent =
        enriched.filter(
            a =>
                ['PENDING', 'CONFIRMED']
                    .includes(
                        a.appointmentStatus
                    )
        ).length;


    document.getElementById(
        'paidAppointments'
    ).textContent =
        enriched.filter(
            a =>
                a.paymentStatus === 'PAID'
        ).length;


    document.getElementById(
        'completedAppointments'
    ).textContent =
        enriched.filter(
            a =>
                a.appointmentStatus ===
                'COMPLETED'
        ).length;
}


// ==========================================
// DISPLAY APPOINTMENTS
// ==========================================

function display(arr) {

    list.innerHTML = '';


    if (!arr.length) {

        empty.classList.remove(
            'hidden'
        );

        return;
    }


    empty.classList.add(
        'hidden'
    );


    arr.forEach(a => {

        const s =
            a.schedule || {};


        const card =
            document.createElement(
                'article'
            );


        card.className =
            'appointment-card';


        card.innerHTML = `

            <div class="doctor-section">

                <div class="doctor-avatar">
                    ${initials(s.doctorName)}
                </div>

                <div>

                    <h3>
                        ${esc(
            s.doctorName ||
            'Doctor'
        )}
                    </h3>

                    <p>
                        ${esc(
            s.specialization ||
            'Specialist'
        )}
                    </p>

                    <span class="appointment-number">
                        Appointment #${a.appointmentNumber}
                        · ID #${a.appointmentId}
                    </span>

                </div>

            </div>


            <div class="appointment-info">

                <i class="bi bi-hospital"></i>

                <div>

                    <span>
                        Hospital
                    </span>

                    <strong>
                        ${esc(
            (s.hospitalName || '-') +
            (
                s.city
                    ? ' · ' + s.city
                    : ''
            )
        )}
                    </strong>

                </div>

            </div>


            <div class="appointment-info">

                <i class="bi bi-calendar3"></i>

                <div>

                    <span>
                        Date & Time
                    </span>

                    <strong>
                        ${fmtDate(
            s.scheduleDate
        )}
                        ·
                        ${fmtTime(
            s.startTime
        )}
                    </strong>

                </div>

            </div>


            <div class="status-group">

                <span class="
                    status-badge
                    ${String(
            a.appointmentStatus
        ).toLowerCase()}
                ">
                    ${a.appointmentStatus}
                </span>


                <span class="
                    payment-badge
                    ${String(
            a.paymentStatus
        ).toLowerCase()}
                ">
                    ${a.paymentStatus}
                </span>

            </div>


            <div class="appointment-actions">

                ${
            a.paymentStatus ===
            'UNPAID' &&
            a.appointmentStatus !==
            'CANCELLED'

                ? `
                            <a
                                class="action-button pay-button"
                                href="/patient/payments">

                                <i class="bi bi-credit-card"></i>

                                Pay Now

                            </a>
                          `

                : ''
        }


                ${
            ['PENDING', 'CONFIRMED']
                .includes(
                    a.appointmentStatus
                )

                ? `
                            <button
                                class="action-button cancel-action-button"
                                onclick="openCancel(
                                    ${a.appointmentId},
                                    ${a.appointmentNumber},
                                    '${a.paymentStatus}'
                                )">

                                <i class="bi bi-x-circle"></i>

                                Cancel

                            </button>
                          `

                : ''
        }

            </div>
        `;


        list.appendChild(card);
    });
}


// ==========================================
// HELPERS
// ==========================================

function initials(name) {

    if (!name) {
        return 'DR';
    }

    return name
        .replace(
            /^Dr\.?\s*/i,
            ''
        )
        .split(' ')
        .filter(Boolean)
        .slice(0, 2)
        .map(x => x[0])
        .join('')
        .toUpperCase();
}


function esc(value) {

    const div =
        document.createElement(
            'div'
        );

    div.textContent =
        String(value ?? '');

    return div.innerHTML;
}


function fmtDate(value) {

    if (!value) {
        return '-';
    }

    return new Date(
        value + 'T00:00:00'
    ).toLocaleDateString(
        'en-GB',
        {
            day: '2-digit',
            month: 'short',
            year: 'numeric'
        }
    );
}


function fmtTime(value) {

    if (!value) {
        return '-';
    }

    let [hour, minute] =
        value.split(':');

    hour =
        Number(hour);

    return (
        (hour % 12 || 12) +
        ':' +
        minute +
        ' ' +
        (
            hour >= 12
                ? 'PM'
                : 'AM'
        )
    );
}


// ==========================================
// FILTER BUTTONS
// ==========================================

document
    .querySelectorAll(
        '.filter-button'
    )
    .forEach(button => {

        button.onclick =
            () => {

                document
                    .querySelectorAll(
                        '.filter-button'
                    )
                    .forEach(
                        item =>
                            item.classList
                                .remove(
                                    'active'
                                )
                    );


                button.classList.add(
                    'active'
                );


                const filter =
                    button.dataset.filter;


                if (filter === 'ALL') {

                    display(
                        enriched
                    );

                    return;
                }


                if (filter === 'ACTIVE') {

                    display(
                        enriched.filter(
                            a =>
                                [
                                    'PENDING',
                                    'CONFIRMED'
                                ].includes(
                                    a.appointmentStatus
                                )
                        )
                    );

                    return;
                }


                display(
                    enriched.filter(
                        a =>
                            a.appointmentStatus ===
                            filter
                    )
                );
            };
    });


// ==========================================
// OPEN CANCEL MODAL
// ==========================================

function openCancel(
    id,
    appointmentNumber,
    paymentStatus) {

    cancelId = id;

    cancelPaymentStatus =
        paymentStatus;


    document.getElementById(
        'cancelAppointmentNumber'
    ).textContent =
        '#' + appointmentNumber;


    // Clear old messages
    cancelMessage.textContent = '';

    cancelMessage.className =
        'cancel-message';


    // Clear previous reason
    refundReasonInput.value = '';

    reasonCharacterCount.textContent =
        '0 / 500';


    // ======================================
    // PAID APPOINTMENT
    // ======================================

    if (paymentStatus === 'PAID') {

        refundReasonSection
            .classList.remove(
            'hidden'
        );

    } else {

        // ==================================
        // UNPAID APPOINTMENT
        // ==================================

        refundReasonSection
            .classList.add(
            'hidden'
        );
    }


    document.getElementById(
        'cancelModal'
    ).classList.remove(
        'hidden'
    );
}


// ==========================================
// CLOSE CANCEL MODAL
// ==========================================

function closeCancel() {

    document.getElementById(
        'cancelModal'
    ).classList.add(
        'hidden'
    );


    cancelId = null;

    cancelPaymentStatus = null;

    refundReasonInput.value = '';

    reasonCharacterCount.textContent =
        '0 / 500';

    cancelMessage.textContent = '';

    cancelMessage.className =
        'cancel-message';
}


// ==========================================
// CHARACTER COUNTER
// ==========================================

refundReasonInput.addEventListener(
    'input',
    () => {

        reasonCharacterCount.textContent =
            refundReasonInput
                .value
                .length +
            ' / 500';
    }
);


// ==========================================
// MODAL BUTTONS
// ==========================================

document.getElementById(
    'closeCancelModal'
).onclick =
    closeCancel;


document.getElementById(
    'keepAppointmentButton'
).onclick =
    closeCancel;


// ==========================================
// CONFIRM CANCELLATION
// ==========================================

document.getElementById(
    'confirmCancelButton'
).onclick =
    async () => {

        if (!cancelId) {
            return;
        }


        let refundReason = null;


        // ======================================
        // PAID APPOINTMENT VALIDATION
        // ======================================

        if (
            cancelPaymentStatus ===
            'PAID'
        ) {

            refundReason =
                refundReasonInput
                    .value
                    .trim();


            if (!refundReason) {

                cancelMessage.className =
                    'cancel-message error';

                cancelMessage.textContent =
                    'Please enter a cancellation / refund reason.';

                refundReasonInput.focus();

                return;
            }


            if (
                refundReason.length >
                500
            ) {

                cancelMessage.className =
                    'cancel-message error';

                cancelMessage.textContent =
                    'Refund reason cannot exceed 500 characters.';

                return;
            }
        }


        const confirmButton =
            document.getElementById(
                'confirmCancelButton'
            );


        const originalButtonContent =
            confirmButton.innerHTML;


        confirmButton.disabled = true;

        confirmButton.innerHTML =
            '<i class="bi bi-hourglass-split"></i> Cancelling...';


        try {

            const options = {
                method: 'PUT'
            };


            // Send JSON reason only
            // for PAID appointments
            if (
                cancelPaymentStatus ===
                'PAID'
            ) {

                options.headers = {
                    'Content-Type':
                        'application/json'
                };


                options.body =
                    JSON.stringify({
                        refundReason:
                        refundReason
                    });
            }


            const result =
                await getJson(
                    '/api/appointments/' +
                    cancelId +
                    '/cancel',
                    options
                );


            cancelMessage.className =
                'cancel-message success';


            if (
                cancelPaymentStatus ===
                'PAID'
            ) {

                cancelMessage.textContent =
                    'Appointment cancelled. Your refund request has been sent to the administrator.';

            } else {

                cancelMessage.textContent =
                    result ||
                    'Appointment cancelled successfully.';
            }


            setTimeout(
                async () => {

                    closeCancel();

                    await loadAppointments();

                },
                900
            );


        } catch (e) {

            cancelMessage.className =
                'cancel-message error';

            cancelMessage.textContent =
                e.message;

        } finally {

            confirmButton.disabled =
                false;

            confirmButton.innerHTML =
                originalButtonContent;
        }
    };


// ==========================================
// LOGOUT
// ==========================================

document.getElementById(
    'logoutButton'
).onclick =
    () => {

        sessionStorage.removeItem(
            'loggedInUser'
        );

        location.href =
            '/login';
    };


// ==========================================
// MOBILE MENU
// ==========================================

const mobileMenuButton =
    document.getElementById(
        'mobileMenuButton'
    );

const sidebar =
    document.querySelector(
        '.sidebar'
    );


if (mobileMenuButton) {

    mobileMenuButton.onclick =
        () =>
            sidebar.classList.toggle(
                'open'
            );
}


// ==========================================
// START PAGE
// ==========================================

loadAppointments();