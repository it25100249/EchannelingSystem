let myAppointments = [];
let allPayments = [];
let paymentScheduleDetails = new Map();


// =====================================================
// LOAD DATA
// =====================================================

async function load() {

    try {

        const [apps, pays] = await Promise.all([
            api('/api/appointments'),
            api('/api/payments')
        ]);


        // Current patient's appointments
        myAppointments = apps.filter(
            a => Number(a.patientId) === Number(currentUser.userId)
        );


        const appointmentIds = new Set(
            myAppointments.map(
                a => Number(a.appointmentId)
            )
        );


        // Load schedule details so every appointment is clearly identifiable.
        const scheduleIds = [...new Set(myAppointments.map(a => Number(a.scheduleId)))];
        const schedulePairs = await Promise.all(scheduleIds.map(async id => {
            try { return [id, await api('/api/schedules/' + id + '/details')]; }
            catch (e) { return [id, null]; }
        }));
        paymentScheduleDetails = new Map(schedulePairs);

        // Current patient's payments
        allPayments = pays.filter(
            p => appointmentIds.has(
                Number(p.appointmentId)
            )
        );


        render();

    } catch (e) {

        msg(
            'msg',
            e.message,
            false
        );
    }
}


// =====================================================
// RENDER PAGE
// =====================================================

function render() {

    const unpaid = myAppointments.filter(
        a =>
            a.paymentStatus === 'UNPAID' &&
            a.appointmentStatus !== 'CANCELLED'
    );


    // =================================================
    // UNPAID APPOINTMENTS
    // =================================================

    const unpaidContainer =
        document.getElementById('unpaid');


    unpaidContainer.innerHTML =

        unpaid.length

            ? unpaid.map(a =>

                `
                <div class="unpaid-item">

                    <div>

                        <div class="unpaid-title">
                            Booking #${a.appointmentId} <span style="color:#64748b;font-weight:600">• Queue #${a.appointmentNumber}</span>
                            <span class="badge warn">
                                UNPAID
                            </span>
                        </div>

                        <div class="unpaid-sub">
                            ${appointmentLabel(a)}
                        </div>

                    </div>

                    <button
                        class="btn secondary"
                        onclick="payAppointment(${a.appointmentId})">

                        <i class="bi bi-credit-card"></i>
                        Pay Now

                    </button>

                </div>
                `

            ).join('')

            :

            `
            <div class="empty">
                No unpaid appointments.
            </div>
            `;


    // =================================================
    // APPOINTMENT DROPDOWN
    // =================================================

    const paymentAppointment =
        document.getElementById(
            'paymentAppointment'
        );


    const currentSelected =
        paymentAppointment.value;


    paymentAppointment.innerHTML =

        `
        <option value="">
            Select appointment
        </option>
        ` +

        unpaid.map(a =>

            `
            <option value="${a.appointmentId}">
                Booking #${a.appointmentId} • Queue #${a.appointmentNumber} • ${appointmentOptionLabel(a)}
            </option>
            `

        ).join('');


    // Keep selected appointment after reload
    if (
        currentSelected &&
        unpaid.some(
            a =>
                Number(a.appointmentId) ===
                Number(currentSelected)
        )
    ) {

        paymentAppointment.value =
            currentSelected;
    }

    // Real-life flow: if the patient has only one unpaid appointment,
    // select it automatically. With multiple appointments the patient chooses
    // from their own appointment list; no manual appointment number is typed.
    if (!paymentAppointment.value && unpaid.length === 1) {
        paymentAppointment.value = unpaid[0].appointmentId;
    }
    updateSelectedAppointmentSummary();


    // =================================================
    // PAYMENT HISTORY
    // =================================================

    const rows =
        document.getElementById(
            'paymentRows'
        );


    rows.innerHTML =

        allPayments.map(p => {

            let statusClass = '';


            if (
                p.paymentStatus === 'PENDING'
            ) {

                statusClass = 'warn';
            }


            if (
                p.paymentStatus === 'REJECTED'
            ) {

                statusClass = 'red';
            }


            let invoiceButton = '-';


            if (
                p.paymentStatus === 'SUCCESS' ||
                p.paymentStatus === 'PAID' ||
                p.paymentStatus === 'APPROVED'
            ) {

                invoiceButton =

                    `
                    <button
                        class="btn secondary"
                        onclick="invoice(${p.paymentId})">

                        Invoice

                    </button>
                    `;
            }


            let method =
                p.paymentMethod || '-';


            if (
                method === 'BANK_TRANSFER'
            ) {

                method =
                    '<i class="bi bi-bank"></i> Bank Transfer';
            }


            if (
                method === 'CARD'
            ) {

                method =
                    '<i class="bi bi-credit-card"></i> Card';
            }


            return (

                `
                <tr>

                    <td>
                        #${p.paymentId}
                    </td>

                    <td>
                        #${p.appointmentId}
                    </td>

                    <td>
                        ${method}
                    </td>

                    <td>
                        Rs. ${Number(p.amount).toFixed(2)}
                    </td>

                    <td>

                        <span class="badge ${statusClass}">
                            ${p.paymentStatus}
                        </span>

                    </td>

                    <td>
                        ${invoiceButton}
                    </td>

                </tr>
                `
            );

        }).join('')

        ||

        `
        <tr>
            <td colspan="6">
                No payments yet.
            </td>
        </tr>
        `;
}


function appointmentOptionLabel(a) {
    const s = paymentScheduleDetails.get(Number(a.scheduleId));
    if (!s) return a.reason || 'Consultation';
    return `${s.doctorName || 'Doctor'} • ${s.hospitalName || 'Hospital'} • ${s.scheduleDate || ''}`;
}

function appointmentLabel(a) {
    const s = paymentScheduleDetails.get(Number(a.scheduleId));
    if (!s) return a.reason || 'Consultation';
    const fee = Number(s.consultationFee || 0).toFixed(2);
    return `${s.doctorName || 'Doctor'} • ${s.hospitalName || 'Hospital'} • ${s.scheduleDate || ''} ${String(s.startTime || '').substring(0,5)} • Rs. ${fee}`;
}

function updateSelectedAppointmentSummary() {
    const box = document.getElementById('selectedAppointmentSummary');
    if (!box) return;
    const id = Number(document.getElementById('paymentAppointment').value);
    const a = myAppointments.find(x => Number(x.appointmentId) === id);
    box.innerHTML = a ? `<b>Booking #${a.appointmentId} • Queue #${a.appointmentNumber}</b><br><span>${appointmentLabel(a)} • ${a.paymentStatus}</span>` : 'Choose one of your unpaid appointments.';
}

document.addEventListener('change', e => {
    if (e.target && e.target.id === 'paymentAppointment') updateSelectedAppointmentSummary();
});

// =====================================================
// PAY NOW FROM UNPAID APPOINTMENT
// =====================================================

function payAppointment(
    appointmentId
) {

    const select =
        document.getElementById(
            'paymentAppointment'
        );


    select.value =
        appointmentId;
    updateSelectedAppointmentSummary();


    // Reset method selection
    resetPaymentMethod();


    // Scroll to payment section
    document.getElementById(
        'paymentSection'
    ).scrollIntoView({
        behavior: 'smooth',
        block: 'start'
    });
}


// =====================================================
// PAYMENT METHOD SELECTION
// =====================================================

function selectPaymentMethod(
    method
) {

    const appointmentId =
        Number(
            document.getElementById(
                'paymentAppointment'
            ).value
        );


    if (!appointmentId) {

        msg(
            'msg',
            'Please select an appointment first.',
            false
        );

        return;
    }


    const cardMethod =
        document.getElementById(
            'cardMethod'
        );


    const bankMethod =
        document.getElementById(
            'bankMethod'
        );


    const cardBox =
        document.getElementById(
            'cardPaymentBox'
        );


    const bankBox =
        document.getElementById(
            'bankPaymentBox'
        );


    if (
        method === 'CARD'
    ) {

        cardMethod.classList.add(
            'active'
        );

        bankMethod.classList.remove(
            'active'
        );


        cardBox.classList.add(
            'show'
        );

        bankBox.classList.remove(
            'show'
        );
    }


    if (
        method === 'BANK'
    ) {

        bankMethod.classList.add(
            'active'
        );

        cardMethod.classList.remove(
            'active'
        );


        bankBox.classList.add(
            'show'
        );

        cardBox.classList.remove(
            'show'
        );
    }
}


// =====================================================
// RESET PAYMENT METHOD
// =====================================================

function resetPaymentMethod() {

    document.getElementById(
        'cardMethod'
    ).classList.remove(
        'active'
    );


    document.getElementById(
        'bankMethod'
    ).classList.remove(
        'active'
    );


    document.getElementById(
        'cardPaymentBox'
    ).classList.remove(
        'show'
    );


    document.getElementById(
        'bankPaymentBox'
    ).classList.remove(
        'show'
    );
}


// =====================================================
// CHANGE APPOINTMENT
// =====================================================

document.getElementById(
    'paymentAppointment'
).addEventListener(
    'change',
    function () {

        resetPaymentMethod();

    }
);


// =====================================================
// CARD NUMBER FORMATTING
// =====================================================

const cardInput =
    document.getElementById(
        'card'
    );


cardInput.addEventListener(
    'input',
    function () {

        // Numbers only
        let value =
            this.value.replace(
                /\D/g,
                ''
            );


        // Maximum 16 digits
        value =
            value.substring(
                0,
                16
            );


        // Add space every 4 digits
        this.value =
            value.replace(
                /(.{4})/g,
                '$1 '
            ).trim();


        detectCardBrand(
            value
        );
    }
);


// =====================================================
// DETECT VISA / MASTERCARD
// =====================================================

function detectCardBrand(
    cardNumber
) {

    const brand =
        document.getElementById(
            'cardBrand'
        );


    // VISA
    if (
        /^4/.test(cardNumber)
    ) {

        brand.innerHTML =
            `
            <img
                src="/payment/visa.png"
                alt="Visa">
            `;

        return;
    }


    // MASTERCARD
    //
    // Traditional range:
    // 51 - 55
    //
    // New Mastercard range:
    // 2221 - 2720

    const firstTwo =
        Number(
            cardNumber.substring(
                0,
                2
            )
        );


    const firstFour =
        Number(
            cardNumber.substring(
                0,
                4
            )
        );


    const mastercard =
        (
            firstTwo >= 51 &&
            firstTwo <= 55
        )
        ||
        (
            firstFour >= 2221 &&
            firstFour <= 2720
        );


    if (
        mastercard
    ) {

        brand.innerHTML =
            `
            <img
                src="/payment/mastercard.png"
                alt="Mastercard">
            `;

        return;
    }


    // UNKNOWN / EMPTY
    brand.innerHTML =
        `
        <span class="card-brand-text">
            CARD
        </span>
        `;
}


// =====================================================
// EXPIRY DATE AUTO FORMAT
// =====================================================

const expiryInput =
    document.getElementById(
        'expiry'
    );


expiryInput.addEventListener(
    'input',
    function () {

        let value =
            this.value.replace(
                /\D/g,
                ''
            );


        value =
            value.substring(
                0,
                4
            );


        if (
            value.length >= 3
        ) {

            value =
                value.substring(
                    0,
                    2
                )
                +
                '/'
                +
                value.substring(
                    2
                );
        }


        this.value =
            value;
    }
);


// =====================================================
// CVV NUMBERS ONLY
// =====================================================

document.getElementById(
    'cvv'
).addEventListener(
    'input',
    function () {

        this.value =
            this.value
                .replace(
                    /\D/g,
                    ''
                )
                .substring(
                    0,
                    4
                );
    }
);


// =====================================================
// CARD PAYMENT
// =====================================================

document.getElementById(
    'payCard'
).onclick = async () => {

    try {

        const appointmentId =
            Number(
                document.getElementById(
                    'paymentAppointment'
                ).value
            );


        const holder =
            document.getElementById(
                'holder'
            ).value.trim();


        const cardNumber =
            document.getElementById(
                'card'
            ).value.replace(
                /\s/g,
                ''
            );


        const expiry =
            document.getElementById(
                'expiry'
            ).value.trim();


        const cvv =
            document.getElementById(
                'cvv'
            ).value.trim();


        // =================================================
        // BASIC FRONTEND VALIDATION
        // =================================================

        if (!appointmentId) {

            throw new Error(
                'Select an appointment.'
            );
        }


        if (!holder) {

            throw new Error(
                'Enter the cardholder name.'
            );
        }


        if (
            cardNumber.length !== 16
        ) {

            throw new Error(
                'Enter a valid 16 digit card number.'
            );
        }


        const cardType =
            getCardType(
                cardNumber
            );


        if (
            cardType === 'UNKNOWN'
        ) {

            throw new Error(
                'Only Visa and Mastercard are supported.'
            );
        }


        if (
            !/^(0[1-9]|1[0-2])\/\d{2}$/.test(
                expiry
            )
        ) {

            throw new Error(
                'Enter expiry date as MM/YY.'
            );
        }


        if (
            !/^\d{3,4}$/.test(
                cvv
            )
        ) {

            throw new Error(
                'Enter a valid CVV.'
            );
        }


        // =================================================
        // SEND PAYMENT
        // =================================================

        await api(
            '/api/payments/card',
            {

                method: 'POST',

                headers: {

                    'Content-Type':
                        'application/json'
                },

                body: JSON.stringify({

                    appointmentId:
                    appointmentId,

                    cardHolderName:
                    holder,

                    cardNumber:
                    cardNumber,

                    expiryDate:
                    expiry,

                    cvv:
                    cvv
                })
            }
        );


        msg(
            'msg',
            cardType +
            ' card payment successful. Invoice is ready.'
        );


        // Clear card fields
        document.getElementById(
            'holder'
        ).value = '';


        document.getElementById(
            'card'
        ).value = '';


        document.getElementById(
            'expiry'
        ).value = '';


        document.getElementById(
            'cvv'
        ).value = '';


        detectCardBrand('');


        resetPaymentMethod();


        await load();


    } catch (e) {

        msg(
            'msg',
            e.message,
            false
        );
    }
};


// =====================================================
// GET CARD TYPE
// =====================================================

function getCardType(
    cardNumber
) {

    if (
        /^4/.test(cardNumber)
    ) {

        return 'VISA';
    }


    const firstTwo =
        Number(
            cardNumber.substring(
                0,
                2
            )
        );


    const firstFour =
        Number(
            cardNumber.substring(
                0,
                4
            )
        );


    if (
        (
            firstTwo >= 51 &&
            firstTwo <= 55
        )
        ||
        (
            firstFour >= 2221 &&
            firstFour <= 2720
        )
    ) {

        return 'MASTERCARD';
    }


    return 'UNKNOWN';
}


// =====================================================
// BANK TRANSFER / RESUBMIT
// =====================================================

document.getElementById(
    'uploadSlip'
).onclick = async () => {

    try {

        const appointmentId =
            Number(
                document.getElementById(
                    'paymentAppointment'
                ).value
            );


        const file =
            document.getElementById(
                'slip'
            ).files[0];


        if (!appointmentId) {

            throw new Error(
                'Select an appointment.'
            );
        }


        if (!file) {

            throw new Error(
                'Select a bank slip.'
            );
        }


        // Find previous bank payment
        const existingPayment =
            allPayments.find(

                p =>

                    Number(
                        p.appointmentId
                    )
                    ===
                    Number(
                        appointmentId
                    )

                    &&

                    p.paymentMethod ===
                    'BANK_TRANSFER'
            );


        const formData =
            new FormData();


        formData.append(
            'appointmentId',
            appointmentId
        );


        formData.append(
            'bankSlip',
            file
        );


        // =================================================
        // REJECTED -> RESUBMIT
        // =================================================

        if (
            existingPayment &&
            existingPayment.paymentStatus ===
            'REJECTED'
        ) {

            await api(
                '/api/payments/bank-transfer/resubmit',
                {

                    method: 'POST',

                    body: formData
                }
            );


            msg(
                'msg',
                'Bank slip resubmitted successfully. Waiting for admin approval.'
            );
        }


            // =================================================
            // NEW BANK PAYMENT
        // =================================================

        else if (
            !existingPayment
        ) {

            await api(
                '/api/payments/bank-transfer',
                {

                    method: 'POST',

                    body: formData
                }
            );


            msg(
                'msg',
                'Bank slip uploaded successfully. Waiting for admin approval.'
            );
        }


            // =================================================
            // ALREADY PENDING
        // =================================================

        else if (
            existingPayment.paymentStatus ===
            'PENDING'
        ) {

            throw new Error(
                'This bank transfer is already waiting for admin approval.'
            );
        }


            // =================================================
            // ALREADY COMPLETED
        // =================================================

        else {

            throw new Error(
                'This appointment already has a completed payment.'
            );
        }


        document.getElementById(
            'slip'
        ).value = '';


        resetPaymentMethod();


        await load();


    } catch (e) {

        msg(
            'msg',
            e.message,
            false
        );
    }
};


// =====================================================
// INVOICE
// =====================================================

async function invoice(
    paymentId
) {

    try {

        const invoiceData =
            await api(
                '/api/invoices/payment/' +
                paymentId
            );


        window.open(
            '/api/invoices/' +
            invoiceData.invoiceId +
            '/pdf',
            '_blank'
        );


    } catch (e) {

        msg(
            'msg',
            'Invoice is available after successful payment.',
            false
        );
    }
}


// =====================================================
// START
// =====================================================

load();