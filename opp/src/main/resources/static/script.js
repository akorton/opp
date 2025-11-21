const usernameInput = document.getElementById("username");
const passwordInput = document.getElementById("password");
const roleInput = document.getElementById("role");
const defaultOptions = document.getElementById("defaultOption").innerText;
const registerButton = document.getElementById("register");
const loginButton = document.getElementById("login");
const alertForm = document.getElementById("alertForm");
const alertRequest = document.getElementById("alertRequest");
const form = document.getElementById("form");

const host = "http://localhost:8080";
const apiUrl = host + "/api/auth";

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

const redirect = (token) => {
    fetch(host + "/project.html", {
        method: "GET",
        headers: {
            "Authorization": "Bearer " + token,
            'Content-Type': 'application/json'
        }
    }).then(response => {
        if (!response.ok) {
            throw new Error();
        }

        return response.text();
    }).then(data => {
        document.open();
        document.writeln(data);
        document.close();
    });
};

const submit = (endpoint) => {
    if (!validate()) {
        return;
    }

    fetch(apiUrl + endpoint, {
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
        localStorage.setItem("token", data.token);
        redirect(data.token);
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
