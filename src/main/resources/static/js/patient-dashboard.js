// ==========================================
// LOGGED-IN PATIENT
// ==========================================

const storedUser =
    sessionStorage.getItem("loggedInUser");

let loggedInUser = null;


if (!storedUser) {

    // No logged-in user
    window.location.href = "/login";

} else {

    loggedInUser =
        JSON.parse(storedUser);


    // Only PATIENT can use this dashboard
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

    const fullName =
        user.fullName || "Patient";


    const firstName =
        fullName
            .trim()
            .split(" ")[0];


    // Top-right profile name
    const topUserName =
        document.getElementById(
            "topUserName"
        );


    if (topUserName) {

        topUserName.textContent =
            fullName;
    }


    // Welcome name
    const welcomeUserName =
        document.getElementById(
            "welcomeUserName"
        );


    if (welcomeUserName) {

        welcomeUserName.textContent =
            firstName;
    }


    // Avatar first letter
    const userAvatar =
        document.getElementById(
            "userAvatar"
        );


    if (userAvatar) {

        userAvatar.textContent =
            fullName
                .charAt(0)
                .toUpperCase();
    }
}


// ==========================================
// LOGOUT
// ==========================================

const logoutButton =
    document.getElementById(
        "logoutButton"
    );


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
// FIND DOCTOR BUTTON
// ==========================================

const findDoctorButton =
    document.getElementById(
        "findDoctorButton"
    );


if (findDoctorButton) {

    findDoctorButton.addEventListener(
        "click",
        function () {

            window.location.href =
                "/patient/doctors";
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
    document.querySelector(
        ".sidebar"
    );


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
// PATIENT DASHBOARD SEARCH
// ==========================================

document.addEventListener(
    "DOMContentLoaded",
    function () {

        const searchInput =
            document.querySelector(
                ".topbar-search input"
            );


        const searchIcon =
            document.querySelector(
                ".topbar-search .bi-search"
            );


        // Search input not found
        if (!searchInput) {

            console.error(
                "Dashboard search input not found."
            );

            return;
        }


        // ==================================
        // PERFORM SEARCH
        // ==================================

        function performDashboardSearch() {

            const query =
                searchInput
                    .value
                    .trim();


            // Empty search
            if (!query) {

                searchInput.focus();

                return;
            }


            console.log(
                "Dashboard search:",
                query
            );


            // Send query to Find Doctors page
            window.location.href =
                "/patient/doctors?q=" +
                encodeURIComponent(query);
        }


        // ==================================
        // ENTER KEY SEARCH
        // ==================================

        searchInput.addEventListener(
            "keydown",
            function (event) {

                if (
                    event.key === "Enter"
                ) {

                    event.preventDefault();

                    performDashboardSearch();
                }
            }
        );


        // ==================================
        // SEARCH ICON CLICK
        // ==================================

        if (searchIcon) {

            searchIcon.style.cursor =
                "pointer";


            searchIcon.addEventListener(
                "click",
                function () {

                    performDashboardSearch();
                }
            );
        }
    }
);


// ==========================================
// LIVE DASHBOARD DATA
// ==========================================

async function loadDashboardData() {

    try {

        // ==================================
        // HELPER GET FUNCTION
        // ==================================

        const get = async function (url) {

            const response =
                await fetch(url);


            if (!response.ok) {

                const errorText =
                    await response.text();


                throw new Error(
                    errorText ||
                    "Request failed."
                );
            }


            return response.json();
        };


        // ==================================
        // LOAD DATA
        // ==================================

        const [
            appointments,
            prescriptions,
            payments,
            reviews
        ] = await Promise.all([

            get("/api/appointments"),

            get("/api/prescriptions"),

            get("/api/payments"),

            get("/api/reviews")

        ]);


        if (!loggedInUser) {
            return;
        }


        const userId =
            Number(
                loggedInUser.userId
            );


        // ==================================
        // PATIENT APPOINTMENTS
        // ==================================

        const myAppointments =
            appointments

                .filter(
                    function (appointment) {

                        return (
                            Number(
                                appointment.patientId
                            ) === userId
                        );
                    }
                )

                .sort(
                    function (a, b) {

                        return (
                            Number(
                                b.appointmentId
                            ) -
                            Number(
                                a.appointmentId
                            )
                        );
                    }
                );


        // Patient appointment IDs
        const appointmentIds =
            new Set(

                myAppointments.map(
                    function (appointment) {

                        return Number(
                            appointment.appointmentId
                        );
                    }
                )
            );


        // ==================================
        // PATIENT PRESCRIPTIONS
        // ==================================

        const myPrescriptions =
            prescriptions.filter(
                function (prescription) {

                    return appointmentIds.has(
                        Number(
                            prescription.appointmentId
                        )
                    );
                }
            );


        // ==================================
        // PATIENT PAYMENTS
        // ==================================

        const myPayments =
            payments.filter(
                function (payment) {

                    return appointmentIds.has(
                        Number(
                            payment.appointmentId
                        )
                    );
                }
            );


        // ==================================
        // PATIENT REVIEWS
        // ==================================

        const myReviews =
            reviews.filter(
                function (review) {

                    return (
                        Number(
                            review.patientId
                        ) === userId
                    );
                }
            );


        // ==================================
        // UPDATE DASHBOARD COUNTS
        // ==================================

        setDashboardValue(
            "appointmentCount",
            myAppointments.length
        );


        setDashboardValue(
            "prescriptionCount",
            myPrescriptions.length
        );


        setDashboardValue(
            "paymentCount",
            myPayments.length
        );


        setDashboardValue(
            "reviewCount",
            myReviews.length
        );


        // ==================================
        // RECENT APPOINTMENTS
        // ==================================

        await displayRecentAppointments(
            myAppointments,
            get
        );


    } catch (error) {

        console.warn(
            "Dashboard data error:",
            error.message
        );
    }
}


// ==========================================
// SET DASHBOARD VALUE
// ==========================================

function setDashboardValue(
    elementId,
    value
) {

    const element =
        document.getElementById(
            elementId
        );


    if (element) {

        element.textContent =
            value;
    }
}


// ==========================================
// DISPLAY RECENT APPOINTMENTS
// ==========================================

async function displayRecentAppointments(
    appointments,
    get
) {

    const container =
        document.getElementById(
            "recentAppointments"
        );


    if (!container) {
        return;
    }


    // ======================================
    // NO APPOINTMENTS
    // ======================================

    if (appointments.length === 0) {

        container.innerHTML =

            '<div class="empty-state">' +

            '<i class="bi bi-calendar2-plus"></i>' +

            '<h3>No appointments yet</h3>' +

            '<p>' +
            'Find a doctor and book your first appointment.' +
            '</p>' +

            '</div>';


        return;
    }


    // Latest 4 appointments
    const recentAppointments =
        appointments.slice(0, 4);


    const rows =
        await Promise.all(

            recentAppointments.map(
                async function (appointment) {

                    let schedule = {};


                    try {

                        schedule =
                            await get(
                                "/api/schedules/" +
                                appointment.scheduleId +
                                "/details"
                            );

                    } catch (error) {

                        console.warn(
                            "Schedule details unavailable:",
                            appointment.scheduleId
                        );
                    }


                    return createRecentAppointmentHtml(
                        appointment,
                        schedule
                    );
                }
            )
        );


    container.innerHTML =
        rows.join("");
}


// ==========================================
// CREATE RECENT APPOINTMENT HTML
// ==========================================

function createRecentAppointmentHtml(
    appointment,
    schedule
) {

    let dateNumber = "--";

    let monthName = "";


    if (schedule.scheduleDate) {

        const date =
            new Date(
                schedule.scheduleDate +
                "T00:00:00"
            );


        dateNumber =
            date.getDate();


        monthName =
            date.toLocaleString(
                "en-US",
                {
                    month: "short"
                }
            );
    }


    const doctorName =
        schedule.doctorName ||
        "Doctor";


    const specialization =
        schedule.specialization ||
        "";


    const hospitalName =
        schedule.hospitalName ||
        "";


    const appointmentStatus =
        appointment.appointmentStatus ||
        "PENDING";


    return (

        '<div class="recent-appointment">' +


        '<div class="recent-date">' +

        '<b>' +
        escapeDashboardHtml(
            dateNumber
        ) +
        '</b>' +

        '<span>' +
        escapeDashboardHtml(
            monthName
        ) +
        '</span>' +

        '</div>' +


        '<div>' +

        '<strong>' +
        escapeDashboardHtml(
            doctorName
        ) +
        '</strong>' +

        '<p>' +
        escapeDashboardHtml(
            specialization
        ) +

        (
            specialization &&
            hospitalName
                ? " · "
                : ""
        ) +

        escapeDashboardHtml(
            hospitalName
        ) +

        '</p>' +

        '</div>' +


        '<span class="status-pill">' +

        escapeDashboardHtml(
            appointmentStatus
        ) +

        '</span>' +


        '</div>'
    );
}


// ==========================================
// BASIC HTML SAFETY
// ==========================================

function escapeDashboardHtml(value) {

    const div =
        document.createElement(
            "div"
        );


    div.textContent =
        String(value);


    return div.innerHTML;
}


// ==========================================
// INITIALIZE DASHBOARD DATA
// ==========================================

loadDashboardData();

// ==========================================
// TOP RIGHT PATIENT PROFILE
// ==========================================

document.addEventListener("DOMContentLoaded", function () {

    // Top-right profile area
    const profileName =
        document.getElementById("topUserName");


    if (!profileName) {

        console.error(
            "Top user profile element not found."
        );

        return;
    }


    // Find the parent clickable profile container
    const profileContainer =
        profileName.parentElement;


    if (!profileContainer) {
        return;
    }


    profileContainer.style.cursor =
        "pointer";


    profileContainer.addEventListener(
        "click",
        function () {

            window.location.href =
                "/patient/profile";
        }
    );
});

// ==========================================
// PATIENT PROFILE BUTTON
// ==========================================

const patientProfileButton =
    document.getElementById(
        "patientProfileButton"
    );


if (patientProfileButton) {

    patientProfileButton.style.cursor =
        "pointer";


    patientProfileButton.addEventListener(
        "click",
        function () {

            window.location.href =
                "/patient/profile";
        }
    );
}
// ==========================================
// DASHBOARD BANNER SLIDER
// ==========================================

document.addEventListener(
    "DOMContentLoaded",
    function () {

        const slides =
            document.querySelectorAll(
                ".banner-slide"
            );

        const dots =
            document.querySelectorAll(
                ".banner-dot"
            );

        const previousButton =
            document.getElementById(
                "bannerPrevious"
            );

        const nextButton =
            document.getElementById(
                "bannerNext"
            );


        if (slides.length === 0) {
            return;
        }


        let currentSlide = 0;

        let bannerTimer = null;


        // ==================================
        // SHOW SLIDE
        // ==================================

        function showBannerSlide(index) {

            if (index >= slides.length) {
                index = 0;
            }


            if (index < 0) {
                index = slides.length - 1;
            }


            slides.forEach(
                function (slide) {

                    slide.classList.remove(
                        "active"
                    );
                }
            );


            dots.forEach(
                function (dot) {

                    dot.classList.remove(
                        "active"
                    );
                }
            );


            slides[index]
                .classList.add(
                "active"
            );


            if (dots[index]) {

                dots[index]
                    .classList.add(
                    "active"
                );
            }


            currentSlide = index;
        }


        // ==================================
        // NEXT
        // ==================================

        function nextBannerSlide() {

            showBannerSlide(
                currentSlide + 1
            );
        }


        // ==================================
        // AUTO CHANGE - 2 SECONDS
        // ==================================

        function startBannerTimer() {

            stopBannerTimer();


            bannerTimer =
                setInterval(
                    nextBannerSlide,
                    2000
                );
        }


        function stopBannerTimer() {

            if (bannerTimer) {

                clearInterval(
                    bannerTimer
                );

                bannerTimer = null;
            }
        }


        // ==================================
        // NEXT BUTTON
        // ==================================

        if (nextButton) {

            nextButton.addEventListener(
                "click",
                function () {

                    nextBannerSlide();

                    startBannerTimer();
                }
            );
        }


        // ==================================
        // PREVIOUS BUTTON
        // ==================================

        if (previousButton) {

            previousButton.addEventListener(
                "click",
                function () {

                    showBannerSlide(
                        currentSlide - 1
                    );

                    startBannerTimer();
                }
            );
        }


        // ==================================
        // DOT CLICK
        // ==================================

        dots.forEach(
            function (dot) {

                dot.addEventListener(
                    "click",
                    function () {

                        const index =
                            Number(
                                dot.dataset.slide
                            );


                        showBannerSlide(
                            index
                        );


                        startBannerTimer();
                    }
                );
            }
        );


        // Start first slide
        showBannerSlide(0);

        // Start automatic slider
        startBannerTimer();
    }
);
// ==========================================
// LIVE DOCTOR + HOSPITAL SEARCH
// ==========================================
(function setupDashboardSearch(){
    const input=document.getElementById('dashboardGlobalSearch');
    const box=document.getElementById('dashboardSearchResults');
    if(!input||!box)return;
    let doctors=[], hospitals=[];
    Promise.all([
        fetch('/api/doctors').then(r=>r.ok?r.json():[]),
        fetch('/api/hospitals').then(r=>r.ok?r.json():[])
    ]).then(([d,h])=>{doctors=d||[];hospitals=h||[]}).catch(()=>{});

    function esc(v){const d=document.createElement('div');d.textContent=String(v||'');return d.innerHTML}
    function render(){
        const q=input.value.trim().toLowerCase();
        if(!q){box.classList.remove('show');box.innerHTML='';return}
        const ds=doctors.filter(d=>[d.fullName,d.specialization,d.licenseNumber].some(v=>String(v||'').toLowerCase().includes(q))).slice(0,6);
        const hs=hospitals.filter(h=>[h.hospitalName,h.city,h.address].some(v=>String(v||'').toLowerCase().includes(q))).slice(0,6);
        const rows=[];
        ds.forEach(d=>rows.push(`<div class="dashboard-search-item" data-go="/patient/doctors"><i class="bi bi-person-badge"></i><div><b>${esc(d.fullName)}</b><small>${esc(d.specialization||'Doctor')}</small></div></div>`));
        hs.forEach(h=>rows.push(`<div class="dashboard-search-item" data-go="/patient/doctors"><i class="bi bi-hospital"></i><div><b>${esc(h.hospitalName)}</b><small>${esc(h.city||h.address||'Hospital')}</small></div></div>`));
        box.innerHTML=rows.length?rows.join(''):'<div class="dashboard-search-empty">No matching doctors or hospitals found.</div>';
        box.classList.add('show');
        box.querySelectorAll('[data-go]').forEach(el=>el.onclick=()=>location.href=el.dataset.go);
    }
    input.addEventListener('input',render);
    input.addEventListener('keydown',e=>{if(e.key==='Enter'&&input.value.trim())location.href='/patient/doctors'});
    document.addEventListener('click',e=>{if(!e.target.closest('.topbar-search'))box.classList.remove('show')});
})();
