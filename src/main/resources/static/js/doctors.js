// ==========================================
// LOGGED-IN PATIENT
// ==========================================

const storedUser = sessionStorage.getItem("loggedInUser");

let loggedInUser = null;

if (!storedUser) {

    window.location.href = "/login";

} else {

    loggedInUser = JSON.parse(storedUser);

    if (loggedInUser.role !== "PATIENT") {

        window.location.href = "/login";

    } else {

        loadPatientDetails(loggedInUser);
    }
}


// ==========================================
// DISPLAY PATIENT DETAILS
// ==========================================

function loadPatientDetails(user) {

    const fullName = user.fullName || "Patient";

    const topUserName =
        document.getElementById("topUserName");

    const userAvatar =
        document.getElementById("userAvatar");


    if (topUserName) {

        topUserName.textContent = fullName;
    }


    if (userAvatar) {

        userAvatar.textContent =
            fullName.charAt(0).toUpperCase();
    }
}


// ==========================================
// PAGE ELEMENTS
// ==========================================

const hospitalSelect =
    document.getElementById("hospitalSelect");

const specializationSelect =
    document.getElementById("specializationSelect");

const scheduleDate =
    document.getElementById("scheduleDate");

const searchDoctorButton =
    document.getElementById("searchDoctorButton");

const doctorGrid =
    document.getElementById("doctorGrid");

const emptyState =
    document.getElementById("emptyState");

const loadingState =
    document.getElementById("loadingState");

const errorMessage =
    document.getElementById("errorMessage");

const resultMessage =
    document.getElementById("resultMessage");

const doctorCount =
    document.getElementById("doctorCount");


// ==========================================
// SET MINIMUM DATE
// ==========================================

const today = new Date();

const year = today.getFullYear();

const month =
    String(today.getMonth() + 1).padStart(2, "0");

const day =
    String(today.getDate()).padStart(2, "0");

const todayString =
    year + "-" + month + "-" + day;

scheduleDate.min = todayString;


// ==========================================
// LOAD HOSPITALS FROM BACKEND
// ==========================================

async function loadHospitals() {

    try {

        const response =
            await fetch("/api/hospitals");


        if (!response.ok) {

            throw new Error(
                "Failed to load hospitals."
            );
        }


        const hospitals =
            await response.json();


        hospitalSelect.innerHTML =
            '<option value="">Select hospital</option>';


        hospitals.forEach(function (hospital) {

            const option =
                document.createElement("option");


            option.value =
                hospital.hospitalId;


            option.textContent =
                hospital.hospitalName +
                " - " +
                hospital.city;


            hospitalSelect.appendChild(option);
        });


    } catch (error) {

        console.error(
            "Hospital loading error:",
            error
        );


        hospitalSelect.innerHTML =
            '<option value="">Unable to load hospitals</option>';
    }
}


// ==========================================
// LOAD SPECIALIZATIONS
// ==========================================

async function loadSpecializations() {

    try {

        const response =
            await fetch("/api/doctors");


        if (!response.ok) {

            throw new Error(
                "Failed to load doctors."
            );
        }


        const doctors =
            await response.json();


        const specializations = [];


        doctors.forEach(function (doctor) {

            if (
                doctor.specialization &&
                !specializations.includes(
                    doctor.specialization
                )
            ) {

                specializations.push(
                    doctor.specialization
                );
            }
        });


        specializations.sort();


        specializationSelect.innerHTML =
            '<option value="">All specializations</option>';


        specializations.forEach(
            function (specialization) {

                const option =
                    document.createElement("option");


                option.value =
                    specialization;


                option.textContent =
                    specialization;


                specializationSelect.appendChild(
                    option
                );
            }
        );


    } catch (error) {

        console.error(
            "Specialization loading error:",
            error
        );
    }
}


// ==========================================
// LOGOUT
// ==========================================

const logoutButton =
    document.getElementById("logoutButton");


if (logoutButton) {

    logoutButton.addEventListener(
        "click",
        function () {

            sessionStorage.removeItem(
                "loggedInUser"
            );

            window.location.href =
                "/login";
        }
    );
}


// ==========================================
// MOBILE SIDEBAR
// ==========================================

const mobileMenuButton =
    document.getElementById(
        "mobileMenuButton"
    );

const sidebar =
    document.querySelector(".sidebar");


if (mobileMenuButton && sidebar) {

    mobileMenuButton.addEventListener(
        "click",
        function () {

            sidebar.classList.toggle(
                "open"
            );
        }
    );
}

// ==========================================
// SEARCH AVAILABLE DOCTORS
// ==========================================

searchDoctorButton.addEventListener(
    "click",
    async function () {

        const hospitalId =
            hospitalSelect.value;

        const date =
            scheduleDate.value;

        const selectedSpecialization =
            specializationSelect.value;


        // Validation
        if (!hospitalId || !date) {

            showSearchError(
                "Please select a hospital and appointment date."
            );

            return;
        }


        // Reset UI
        errorMessage.classList.add("hidden");

        emptyState.classList.add("hidden");

        doctorGrid.innerHTML = "";

        loadingState.classList.remove("hidden");

        doctorCount.textContent = "0 doctors";

        resultMessage.textContent =
            "Searching available doctors...";


        try {

            const url =
                "/api/schedules/search/details" +
                "?hospitalId=" +
                encodeURIComponent(hospitalId) +
                "&date=" +
                encodeURIComponent(date);


            const response =
                await fetch(url);


            if (!response.ok) {

                throw new Error(
                    "Unable to search available doctors."
                );
            }


            let schedules =
                await response.json();


            // ==================================
            // SPECIALIZATION FILTER
            // ==================================

            if (selectedSpecialization) {

                schedules =
                    schedules.filter(
                        function (schedule) {

                            return (
                                schedule.specialization &&
                                schedule.specialization
                                    .toLowerCase() ===
                                selectedSpecialization
                                    .toLowerCase()
                            );
                        }
                    );
            }


            loadingState.classList.add("hidden");


            // No doctors
            if (schedules.length === 0) {

                doctorCount.textContent =
                    "0 doctors";

                resultMessage.textContent =
                    "No available doctors found for your selection.";

                emptyState.classList.remove(
                    "hidden"
                );

                emptyState.querySelector("h2")
                    .textContent =
                    "No doctors available";

                emptyState.querySelector("p")
                    .textContent =
                    "Try another hospital, date, or specialization.";

                return;
            }


            // Doctors found
            doctorCount.textContent =
                schedules.length +
                (schedules.length === 1
                    ? " doctor"
                    : " doctors");


            resultMessage.textContent =
                "Available doctors for " +
                formatDate(date);


            schedules.forEach(
                function (schedule) {

                    createDoctorCard(schedule);
                }
            );


        } catch (error) {

            console.error(
                "Doctor search error:",
                error
            );


            loadingState.classList.add(
                "hidden"
            );


            showSearchError(
                error.message
            );
        }
    }
);


// ==========================================
// CREATE DOCTOR CARD
// ==========================================

function createDoctorCard(schedule) {

    const card =
        document.createElement("article");

    card.className = "doctor-card";


    const doctorName =
        schedule.doctorName || "Doctor";

    const specialization =
        schedule.specialization ||
        "General Medicine";

    const hospitalName =
        schedule.hospitalName ||
        "Hospital";


    const initials =
        getDoctorInitials(doctorName);


    const fee =
        Number(
            schedule.consultationFee || 0
        ).toFixed(2);


    const startTime =
        formatTime(schedule.startTime);

    const endTime =
        formatTime(schedule.endTime);


    card.innerHTML =
        '<span class="available-badge">' +
        '<i class="bi bi-check-circle-fill"></i> Available' +
        '</span>' +

        '<div class="doctor-card-header">' +

        '<div class="doctor-avatar">' +
        initials +
        '</div>' +

        '<div>' +
        '<h3>' +
        escapeHtml(doctorName) +
        '</h3>' +

        '<p class="doctor-specialization">' +
        escapeHtml(specialization) +
        '</p>' +
        '</div>' +

        '</div>' +

        '<div class="doctor-details">' +

        '<div class="doctor-detail">' +
        '<i class="bi bi-hospital"></i>' +
        '<span>' +
        escapeHtml(hospitalName) +
        '</span>' +
        '</div>' +

        '<div class="doctor-detail">' +
        '<i class="bi bi-calendar3"></i>' +
        '<span>' +
        formatDate(
            schedule.scheduleDate
        ) +
        '</span>' +
        '</div>' +

        '<div class="doctor-detail">' +
        '<i class="bi bi-clock"></i>' +
        '<span>' +
        startTime +
        ' - ' +
        endTime +
        '</span>' +
        '</div>' +

        '</div>' +

        '<div class="doctor-card-footer">' +

        '<div class="doctor-fee">' +
        '<span>Consultation Fee</span>' +
        '<strong>Rs. ' +
        fee +
        '</strong>' +
        '</div>' +

        '<button class="book-button">' +
        '<i class="bi bi-calendar2-plus"></i>' +
        'Book Appointment' +
        '</button>' +

        '</div>';


    // BOOK BUTTON
    const bookButton =
        card.querySelector(
            ".book-button"
        );


    bookButton.addEventListener(
        "click",
        function () {

            openBookingModal(schedule);
        }
    );


    doctorGrid.appendChild(card);
}


// ==========================================
// ERROR MESSAGE
// ==========================================

function showSearchError(message) {

    loadingState.classList.add("hidden");

    errorMessage.classList.remove("hidden");


    const messageSpan =
        errorMessage.querySelector("span");


    if (messageSpan) {

        messageSpan.textContent = message;
    }
}


// ==========================================
// DATE FORMAT
// ==========================================

function formatDate(dateString) {

    if (!dateString) {
        return "-";
    }


    const date =
        new Date(dateString + "T00:00:00");


    return date.toLocaleDateString(
        "en-US",
        {
            year: "numeric",
            month: "short",
            day: "numeric"
        }
    );
}


// ==========================================
// TIME FORMAT
// ==========================================

function formatTime(timeString) {

    if (!timeString) {
        return "-";
    }


    const parts =
        timeString.split(":");


    let hour =
        parseInt(parts[0], 10);

    const minute =
        parts[1] || "00";


    const period =
        hour >= 12 ? "PM" : "AM";


    hour = hour % 12;

    if (hour === 0) {
        hour = 12;
    }


    return (
        hour +
        ":" +
        minute +
        " " +
        period
    );
}


// ==========================================
// DOCTOR INITIALS
// ==========================================

function getDoctorInitials(name) {

    const cleanName =
        name.replace(
            /^Dr\.?\s*/i,
            ""
        );


    const parts =
        cleanName.trim().split(" ");


    let initials = "";


    for (
        let i = 0;
        i < parts.length && i < 2;
        i++
    ) {

        if (parts[i]) {

            initials +=
                parts[i]
                    .charAt(0)
                    .toUpperCase();
        }
    }


    return initials || "DR";
}


// ==========================================
// BASIC HTML SAFETY
// ==========================================

function escapeHtml(value) {

    const div =
        document.createElement("div");

    div.textContent =
        String(value);

    return div.innerHTML;
}

// ==========================================
// BOOKING MODAL ELEMENTS
// ==========================================

const bookingModal =
    document.getElementById("bookingModal");

const closeBookingModalButton =
    document.getElementById("closeBookingModal");

const cancelBookingButton =
    document.getElementById("cancelBookingButton");

const confirmBookingButton =
    document.getElementById("confirmBookingButton");

const appointmentReason =
    document.getElementById("appointmentReason");

const bookingMessage =
    document.getElementById("bookingMessage");


// Currently selected schedule
let selectedSchedule = null;


// ==========================================
// OPEN BOOKING MODAL
// ==========================================

function openBookingModal(schedule) {

    selectedSchedule = schedule;

    const doctorName =
        schedule.doctorName || "Doctor";

    const specialization =
        schedule.specialization || "-";

    const hospitalName =
        schedule.hospitalName || "-";

    const fee =
        Number(
            schedule.consultationFee || 0
        ).toFixed(2);


    document.getElementById(
        "modalDoctorName"
    ).textContent = doctorName;


    document.getElementById(
        "modalSpecialization"
    ).textContent = specialization;


    document.getElementById(
        "modalDoctorAvatar"
    ).textContent =
        getDoctorInitials(doctorName);


    document.getElementById(
        "modalHospital"
    ).textContent = hospitalName;


    document.getElementById(
        "modalDate"
    ).textContent =
        formatDate(
            schedule.scheduleDate
        );


    document.getElementById(
        "modalTime"
    ).textContent =
        formatTime(schedule.startTime) +
        " - " +
        formatTime(schedule.endTime);


    document.getElementById(
        "modalFee"
    ).textContent =
        "Rs. " + fee;


    appointmentReason.value = "";

    bookingMessage.textContent = "";

    bookingMessage.className =
        "booking-message";


    bookingModal.classList.remove(
        "hidden"
    );
}


// ==========================================
// CLOSE BOOKING MODAL
// ==========================================

function closeBookingModal() {

    bookingModal.classList.add(
        "hidden"
    );

    selectedSchedule = null;

    appointmentReason.value = "";

    bookingMessage.textContent = "";

    bookingMessage.className =
        "booking-message";
}


closeBookingModalButton.addEventListener(
    "click",
    closeBookingModal
);


cancelBookingButton.addEventListener(
    "click",
    closeBookingModal
);


// Click outside modal
bookingModal.addEventListener(
    "click",
    function (event) {

        if (event.target === bookingModal) {

            closeBookingModal();
        }
    }
);


// ==========================================
// CONFIRM APPOINTMENT
// ==========================================

confirmBookingButton.addEventListener(
    "click",
    async function () {

        if (!selectedSchedule) {

            showBookingMessage(
                "Please select a doctor first.",
                "error"
            );

            return;
        }


        if (
            !loggedInUser ||
            !loggedInUser.userId
        ) {

            showBookingMessage(
                "Patient information is unavailable. Please login again.",
                "error"
            );

            return;
        }


        const reason =
            appointmentReason.value.trim();


        confirmBookingButton.disabled = true;

        confirmBookingButton.innerHTML =
            '<i class="bi bi-arrow-repeat"></i>' +
            ' Booking...';


        try {

            const appointment = {

                patientId:
                loggedInUser.userId,

                scheduleId:
                selectedSchedule.scheduleId,

                reason:
                reason
            };


            const response =
                await fetch(
                    "/api/appointments",
                    {
                        method: "POST",

                        headers: {
                            "Content-Type":
                                "application/json"
                        },

                        body:
                            JSON.stringify(
                                appointment
                            )
                    }
                );


            if (!response.ok) {

                const errorText =
                    await response.text();

                throw new Error(
                    errorText ||
                    "Appointment booking failed."
                );
            }


            const successText = await response.text();

            showBookingMessage(
                successText || "Appointment booked successfully!",
                "success"
            );


            confirmBookingButton.innerHTML =
                '<i class="bi bi-check-circle"></i>' +
                ' Booked';


            // Close after success
            setTimeout(
                function () {

                    closeBookingModal();

                    confirmBookingButton.disabled =
                        false;

                    confirmBookingButton.innerHTML =
                        '<i class="bi bi-calendar-check"></i>' +
                        ' Confirm Appointment';

                },
                1800
            );


        } catch (error) {

            console.error(
                "Appointment booking error:",
                error
            );


            showBookingMessage(
                getReadableErrorMessage(
                    error.message
                ),
                "error"
            );


            confirmBookingButton.disabled =
                false;


            confirmBookingButton.innerHTML =
                '<i class="bi bi-calendar-check"></i>' +
                ' Confirm Appointment';
        }
    }
);


// ==========================================
// BOOKING MESSAGE
// ==========================================

function showBookingMessage(
    message,
    type
) {

    bookingMessage.textContent =
        message;

    bookingMessage.className =
        "booking-message " + type;
}


// ==========================================
// CLEAN BACKEND ERROR
// ==========================================

function getReadableErrorMessage(
    message
) {

    if (!message) {

        return "Appointment booking failed.";
    }


    // Backend may return JSON error text
    try {

        const parsed =
            JSON.parse(message);


        if (parsed.message) {

            return parsed.message;
        }


        if (parsed.error) {

            return parsed.error;
        }

    } catch (error) {

        // Normal text response
    }


    return message;
}

// ==========================================
// DASHBOARD SEARCH QUERY
// ==========================================

async function handleDashboardSearch() {

    const params =
        new URLSearchParams(
            window.location.search
        );

    const query =
        params.get("q");


    if (!query) {
        return;
    }


    const searchText =
        query
            .trim()
            .toLowerCase();


    if (!searchText) {
        return;
    }


    try {

        // Load doctors and hospitals
        const [
            doctorResponse,
            hospitalResponse
        ] = await Promise.all([

            fetch("/api/doctors"),

            fetch("/api/hospitals")

        ]);


        if (
            !doctorResponse.ok ||
            !hospitalResponse.ok
        ) {

            throw new Error(
                "Unable to search doctors."
            );
        }


        const doctors =
            await doctorResponse.json();


        const hospitals =
            await hospitalResponse.json();


        // ==================================
        // SPECIALIZATION MATCH
        // ==================================

        const specializationMatch =
            doctors.find(
                doctor =>
                    doctor.specialization &&
                    doctor.specialization
                        .toLowerCase()
                        .includes(searchText)
            );


        if (specializationMatch) {

            specializationSelect.value =
                specializationMatch.specialization;


            resultMessage.textContent =
                'Specialization "' +
                specializationMatch.specialization +
                '" selected. Choose a hospital and date to check availability.';


            specializationSelect.scrollIntoView({
                behavior: "smooth",
                block: "center"
            });


            return;
        }


        // ==================================
        // HOSPITAL MATCH
        // ==================================

        const hospitalMatch =
            hospitals.find(
                hospital => {

                    const name =
                        hospital.hospitalName ||
                        "";

                    const city =
                        hospital.city ||
                        "";


                    return (
                        name
                            .toLowerCase()
                            .includes(searchText) ||

                        city
                            .toLowerCase()
                            .includes(searchText)
                    );
                }
            );


        if (hospitalMatch) {

            hospitalSelect.value =
                String(
                    hospitalMatch.hospitalId
                );


            resultMessage.textContent =
                hospitalMatch.hospitalName +
                ' selected. Choose a date to check available doctors.';


            hospitalSelect.scrollIntoView({
                behavior: "smooth",
                block: "center"
            });


            return;
        }


        // ==================================
        // DOCTOR NAME MATCH
        // ==================================

        const doctorMatch =
            doctors.find(
                doctor => {

                    const name =
                        doctor.fullName ||
                        doctor.doctorName ||
                        "";


                    return name
                        .toLowerCase()
                        .includes(searchText);
                }
            );


        if (doctorMatch) {

            if (
                doctorMatch.specialization
            ) {

                specializationSelect.value =
                    doctorMatch.specialization;
            }


            resultMessage.textContent =
                (doctorMatch.fullName ||
                    doctorMatch.doctorName ||
                    "Doctor") +
                " found. Choose a hospital and date to check availability.";


            specializationSelect.scrollIntoView({
                behavior: "smooth",
                block: "center"
            });


            return;
        }


        // ==================================
        // NOTHING FOUND
        // ==================================

        resultMessage.textContent =
            'No doctor, specialization or hospital found for "' +
            query +
            '".';


    } catch (error) {

        console.error(
            "Dashboard search error:",
            error
        );


        showSearchError(
            error.message
        );
    }
}

// ==========================================
// INITIALIZE FIND DOCTORS PAGE
// ==========================================

document.addEventListener(
    "DOMContentLoaded",
    async function () {

        try {

            // ==================================
            // LOAD HOSPITALS
            // ==================================

            await loadHospitals();


            // ==================================
            // LOAD SPECIALIZATIONS
            // ==================================

            await loadSpecializations();


            // ==================================
            // HANDLE SEARCH FROM DASHBOARD
            // Example:
            // /patient/doctors?q=Cardiologist
            // ==================================

            await handleDashboardSearch();


            console.log(
                "Find Doctors page loaded successfully."
            );


        } catch (error) {

            console.error(
                "Find Doctors initialization error:",
                error
            );
        }
    }
);