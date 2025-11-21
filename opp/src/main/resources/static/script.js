const usernameInput = document.getElementById("username");
const passwordInput = document.getElementById("password");
const roleInput = document.getElementById("role");
const defaultOptions = document.getElementById("defaultOption").innerText;
const registerButton = document.getElementById("register");
const loginButton = document.getElementById("login");
const alertForm = document.getElementById("alertForm");
const alertRequest = document.getElementById("alertRequest");
const form = document.getElementById("form");

const url = "http://localhost:8080/api/auth";

const validate = () => {
    // Reset alert
    alertForm.style.display = "none";
    alertRequest.style.display = "none";

    var error = false;

    if (usernameInput.value.trim().length == 0) {
        error = true;
    }

    if (passwordInput.value.trim().length == 0) {
        error = true;
    }

    if (roleInput.value == defaultOptions) {
        error = true;
    }

    if (error) {
        alertForm.style.display = "block";
    }

    return !error;
};

const submit = (endpoint) => {
    if (!validate()) {
        return;
    }

    fetch(url + endpoint, {
        method: "POST",
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify({
            "username": usernameInput.value,
            "password": passwordInput.value,
            "role": roleInput.value
        })
    }).then(response => {
        if (!response.ok) {
            alertRequest.style.display = "block";
            throw new Error();
        } 

        return response.json();
    }).then(data => {
        console.log(data.token);
    })
};

registerButton.addEventListener("click", (e) => {
    e.preventDefault();
    submit("/register");
});


loginButton.addEventListener("click", (e) => {
    e.preventDefault();
    submit("/login");
});
