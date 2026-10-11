let eligible = [];
let mine = [];

// null = creating a new review
// number = editing an existing review
let editingReviewId = null;


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
// LOAD REVIEWS
// =====================================================

async function loadReviews() {

    try {

        const [apps, revs] = await Promise.all([
            api('/api/appointments'),
            api('/api/reviews')
        ]);


        // Only logged-in patient's appointments
        const myApps = apps.filter(
            a =>
                Number(a.patientId) ===
                Number(currentUser.userId)
        );


        // Only logged-in patient's reviews
        mine = revs.filter(
            r =>
                Number(r.patientId) ===
                Number(currentUser.userId)
        );


        // Find already reviewed appointments
        const reviewed = new Set(
            mine.map(
                r => Number(r.appointmentId)
            )
        );


        eligible = [];


        // =================================================
        // FIND APPOINTMENTS AVAILABLE FOR REVIEW
        // =================================================

        for (const a of myApps) {

            if (
                !reviewed.has(
                    Number(a.appointmentId)
                ) &&
                a.appointmentStatus !== 'CANCELLED'
            ) {

                try {

                    const schedule =
                        await api(
                            '/api/schedules/' +
                            a.scheduleId +
                            '/details'
                        );


                    eligible.push({
                        a: a,
                        s: schedule
                    });


                } catch (e) {

                    console.error(
                        'Schedule loading error:',
                        e
                    );
                }
            }
        }


        // =================================================
        // APPOINTMENT SELECT
        // =================================================

        const appointmentSelect =
            document.getElementById(
                'reviewAppointment'
            );


        appointmentSelect.innerHTML =
            '<option value="">' +
            'Select appointment' +
            '</option>' +

            eligible.map(x =>

                '<option value="' +
                x.a.appointmentId +
                '">' +

                'Booking #' + x.a.appointmentId +
                ' • Queue #' + x.a.appointmentNumber +
                ' • ' + (x.s.hospitalName || 'Hospital') +
                ' • ' + (x.s.scheduleDate || '') +

                '</option>'

            ).join('');


        // =================================================
        // DISPLAY MY REVIEWS
        // =================================================

        const reviewContainer =
            document.getElementById(
                'reviews'
            );


        if (mine.length === 0) {

            reviewContainer.innerHTML =
                '<div class="empty">' +
                'No system feedback submitted yet.' +
                '</div>';

            return;
        }


        reviewContainer.innerHTML =
            mine.map(r => {

                const stars =
                    '★'.repeat(
                        Number(r.rating)
                    ) +
                    '☆'.repeat(
                        5 - Number(r.rating)
                    );


                let reviewDate = '';

                if (r.createdAt) {

                    reviewDate =
                        new Date(
                            r.createdAt
                        ).toLocaleString();
                }


                return `

                    <div class="card"
                         style="margin:12px 0">

                        <div style="
                            display:flex;
                            justify-content:space-between;
                            gap:15px;
                            align-items:flex-start;
                            flex-wrap:wrap;
                        ">

                            <div>

                                <b style="
                                    font-size:18px;
                                ">
                                    ${stars}
                                </b>

                                <span style="
                                    color:#64748b;
                                    margin-left:6px;
                                ">
                                    ${r.rating}/5
                                </span>

                                <p style="
                                    margin:10px 0 6px;
                                ">
                                    ${esc(
                    r.comment ||
                    'No comment'
                )}
                                </p>

                                <small style="
                                    color:#64748b;
                                ">
                                    Appointment
                                    #${r.appointmentId}
                                </small>

                                ${
                    reviewDate
                        ? `
                                            <br>
                                            <small style="
                                                color:#94a3b8;
                                            ">
                                                ${esc(reviewDate)}
                                            </small>
                                        `
                        : ''
                }

                            </div>


                            <div style="
                                display:flex;
                                gap:8px;
                            ">

                                <button
                                    class="btn"
                                    type="button"
                                    onclick="editReview(${r.reviewId})">

                                    <i class="bi bi-pencil"></i>

                                    Edit

                                </button>


                                <button
                                    class="btn"
                                    type="button"
                                    onclick="deleteReview(${r.reviewId})">

                                    <i class="bi bi-trash"></i>

                                    Delete

                                </button>

                            </div>

                        </div>

                    </div>

                `;

            }).join('');


    } catch (e) {

        msg(
            'reviewMsg',
            e.message,
            false
        );
    }
}


// =====================================================
// CREATE / UPDATE REVIEW
// =====================================================

document.getElementById(
    'submitReview'
).onclick = async () => {

    try {

        const rating =
            Number(
                document.getElementById(
                    'rating'
                ).value
            );


        const comment =
            document.getElementById(
                'comment'
            ).value;


        // =================================================
        // UPDATE MODE
        // =================================================

        if (editingReviewId !== null) {

            const existingReview =
                mine.find(
                    r =>
                        Number(r.reviewId) ===
                        Number(editingReviewId)
                );


            if (!existingReview) {

                throw new Error(
                    'Review not found'
                );
            }


            await api(
                '/api/reviews/' +
                editingReviewId,
                {

                    method: 'PUT',

                    headers: {
                        'Content-Type':
                            'application/json'
                    },

                    body: JSON.stringify({

                        patientId:
                        existingReview.patientId,

                        appointmentId:
                        existingReview.appointmentId,

                        rating:
                        rating,

                        comment:
                        comment

                    })

                }
            );


            msg(
                'reviewMsg',
                'Feedback updated successfully.'
            );


            cancelEdit();

            await loadReviews();

            return;
        }


        // =================================================
        // CREATE MODE
        // =================================================

        const appointmentId =
            Number(
                document.getElementById(
                    'reviewAppointment'
                ).value
            );


        const selected =
            eligible.find(
                v =>
                    Number(
                        v.a.appointmentId
                    ) === appointmentId
            );


        if (!selected) {

            throw new Error(
                'Select an appointment'
            );
        }


        await api(
            '/api/reviews',
            {

                method: 'POST',

                headers: {
                    'Content-Type':
                        'application/json'
                },

                body: JSON.stringify({

                    patientId:
                    currentUser.userId,

                    appointmentId:
                    appointmentId,

                    rating:
                    rating,

                    comment:
                    comment

                })

            }
        );


        msg(
            'reviewMsg',
            'Review submitted successfully.'
        );


        document.getElementById(
            'comment'
        ).value = '';


        document.getElementById(
            'rating'
        ).value = '5';


        await loadReviews();


    } catch (e) {

        msg(
            'reviewMsg',
            e.message,
            false
        );
    }
};


// =====================================================
// EDIT REVIEW
// =====================================================

function editReview(reviewId) {

    const review =
        mine.find(
            r =>
                Number(r.reviewId) ===
                Number(reviewId)
        );


    if (!review) {

        msg(
            'reviewMsg',
            'Review not found.',
            false
        );

        return;
    }


    editingReviewId =
        review.reviewId;


    // Set current values
    document.getElementById(
        'rating'
    ).value =
        String(review.rating);


    document.getElementById(
        'comment'
    ).value =
        review.comment || '';


    // Disable appointment selection
    // Appointment should not change during edit
    const appointmentSelect =
        document.getElementById(
            'reviewAppointment'
        );


    appointmentSelect.innerHTML = `

        <option value="${review.appointmentId}">

            Appointment #${review.appointmentId}

        </option>

    `;


    appointmentSelect.value =
        String(review.appointmentId);


    appointmentSelect.disabled =
        true;


    // Change button
    const submitButton =
        document.getElementById(
            'submitReview'
        );


    submitButton.innerHTML = `

        <i class="bi bi-check-circle"></i>

        Update Review

    `;


    // Add cancel button if not already there
    if (
        !document.getElementById(
            'cancelReviewEdit'
        )
    ) {

        const cancelButton =
            document.createElement(
                'button'
            );


        cancelButton.id =
            'cancelReviewEdit';


        cancelButton.type =
            'button';


        cancelButton.className =
            'btn';


        cancelButton.style.marginLeft =
            '8px';


        cancelButton.innerHTML = `

            <i class="bi bi-x-circle"></i>

            Cancel

        `;


        cancelButton.onclick =
            cancelEdit;


        submitButton.insertAdjacentElement(
            'afterend',
            cancelButton
        );
    }


    msg(
        'reviewMsg',
        'Editing Review #' + reviewId
    );


    // Move user to form
    window.scrollTo({
        top: 0,
        behavior: 'smooth'
    });
}


// =====================================================
// CANCEL EDIT
// =====================================================

function cancelEdit() {

    editingReviewId = null;


    document.getElementById(
        'rating'
    ).value = '5';


    document.getElementById(
        'comment'
    ).value = '';


    const appointmentSelect =
        document.getElementById(
            'reviewAppointment'
        );


    appointmentSelect.disabled =
        false;


    document.getElementById(
        'submitReview'
    ).innerHTML =
        'Submit Review';


    const cancelButton =
        document.getElementById(
            'cancelReviewEdit'
        );


    if (cancelButton) {

        cancelButton.remove();
    }
}


// =====================================================
// DELETE REVIEW
// =====================================================

async function deleteReview(reviewId) {

    const review =
        mine.find(
            r =>
                Number(r.reviewId) ===
                Number(reviewId)
        );


    if (!review) {

        msg(
            'reviewMsg',
            'Review not found.',
            false
        );

        return;
    }


    // Extra frontend ownership check
    if (
        Number(review.patientId) !==
        Number(currentUser.userId)
    ) {

        msg(
            'reviewMsg',
            'You cannot delete this review.',
            false
        );

        return;
    }


    const confirmed =
        confirm(
            'Are you sure you want to delete this review?'
        );


    if (!confirmed) {

        return;
    }


    try {

        await api(
            '/api/reviews/' +
            reviewId,
            {
                method: 'DELETE'
            }
        );


        msg(
            'reviewMsg',
            'Review deleted successfully.'
        );


        // If currently editing deleted review
        if (
            Number(editingReviewId) ===
            Number(reviewId)
        ) {

            cancelEdit();
        }


        await loadReviews();


    } catch (e) {

        msg(
            'reviewMsg',
            e.message,
            false
        );
    }
}


// =====================================================
// START
// =====================================================

loadReviews();