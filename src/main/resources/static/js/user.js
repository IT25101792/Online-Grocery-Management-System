
const registerForm = document.getElementById("registerForm");

if (registerForm) {
    registerForm.addEventListener("submit", function(event) {
        event.preventDefault();

        const customer = {
            userId: 0,
            name: document.getElementById("name").value,
            email: document.getElementById("email").value,
            password: document.getElementById("password").value,
            address: document.getElementById("address").value,
            phone: document.getElementById("phone").value
        };

        fetch("/api/users/register", {
            method: "POST",
            headers: {"Content-Type": "application/json"},
            body: JSON.stringify(customer)
        })
            .then(response => response.text())
            .then(message => alert(message))
            .catch(error => alert("Registration failed."));
    });
}


const loginForm = document.getElementById("loginForm");

if (loginForm) {
    loginForm.addEventListener("submit", function(event) {
        event.preventDefault();

        const loginDetails = {
            email: document.getElementById("email").value,
            password: document.getElementById("password").value
        };

        fetch("/api/users/login", {
            method: "POST",
            headers: {"Content-Type": "application/json"},
            body: JSON.stringify(loginDetails)
        })
            .then(response => response.text())
            .then(message => alert(message))
            .catch(error => alert("Login failed."));
    });
}


const profileForm = document.getElementById("profileForm");

if (profileForm) {


    profileForm.addEventListener("submit", function(event) {
        event.preventDefault();

        const userId = document.getElementById("userId").value;

        fetch("/api/users/" + userId)
            .then(response => response.text())
            .then(profile => {
                document.getElementById("profileDetails").innerText = profile;
            })
            .catch(error => alert("Unable to load profile."));
    });
}
const updateProfileForm = document.getElementById("updateProfileForm");

if (updateProfileForm) {
    updateProfileForm.addEventListener("submit", function(event) {
        event.preventDefault();

        const userId = document.getElementById("userId").value;

        const customer = {

            userId: Number(userId),
            name: document.getElementById("name").value,
            email: document.getElementById("email").value,
            address: document.getElementById("address").value,
            phone: document.getElementById("phone").value
        };

        fetch("/api/users/" + userId, {
            method: "PUT",
            headers: {"Content-Type": "application/json"},
            body: JSON.stringify(customer)
        })
            .then(response => response.text())
            .then(message => alert(message))
            .catch(error => alert("Unable to update profile."));
    });

}
const deleteAccountForm = document.getElementById("deleteAccountForm");

if (deleteAccountForm) {
    deleteAccountForm.addEventListener("submit", function(event) {
        event.preventDefault();

        const userId = document.getElementById("userId").value;

        if (confirm("Are you sure you want to delete this account?")) {

            fetch("/api/users/" + userId, {
                method: "DELETE"
            })
                .then(response => response.text())
                .then(message => alert(message))
                .catch(error => alert("Unable to delete account."));
        }
    });
}
