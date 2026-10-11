// ==========================================
// PATIENT PROFILE
// ==========================================

const storedUser =
    sessionStorage.getItem("loggedInUser");


if (!storedUser) {

    window.location.href = "/login";

} else {

    const user =
        JSON.parse(storedUser);


    // Only patient can access
    if (user.role !== "PATIENT") {

        window.location.href = "/login";

    } else {

        displayPatientProfile(user);
    }
}


// ==========================================
// DISPLAY PROFILE
// ==========================================

function displayPatientProfile(user) {

    const fullName =
        user.fullName || "Patient";

    const email =
        user.email || "-";

    const phone =
        user.phone || "Not provided";

    const userId =
        user.userId || "-";

    const role =
        user.role || "PATIENT";


    setText(
        "profileName",
        fullName
    );

    setText(
        "profileEmail",
        email
    );

    setText(
        "infoFullName",
        fullName
    );

    setText(
        "infoEmail",
        email
    );

    setText(
        "infoPhone",
        phone
    );

    setText(
        "infoUserId",
        userId
    );

    setText(
        "infoRole",
        role
    );


    // Avatar
    const avatar =
        document.getElementById(
            "profileAvatar"
        );


    if (avatar) {

        avatar.textContent =
            fullName
                .charAt(0)
                .toUpperCase();
    }
}


// ==========================================
// SET TEXT HELPER
// ==========================================

function setText(id, value) {

    const element =
        document.getElementById(id);


    if (element) {

        element.textContent =
            value;
    }
}


// ==========================================
// LOGOUT
// ==========================================

const logoutButton =
    document.getElementById(
        "profileLogoutButton"
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