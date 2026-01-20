const usernameInput = document.getElementById("username");
const passwordInput = document.getElementById("password");
const roleInput = document.getElementById("role");
const roleLabel = document.getElementById('role-label');
const defaultOptions = document.getElementById("defaultOption").innerText;
const registerButton = document.getElementById("register");
const loginButton = document.getElementById("login");
const alertForm = document.getElementById("alertForm");
const form = document.getElementById("form");
const registerTab = document.getElementById("register-tab");
const loginTab = document.getElementById("login-tab");
const toast = document.getElementById("toast-error");
let toast_container = document.getElementById("toast-container");

const domain_local = "localhost";
const domain_prod = "217.76.176.93";
const host = `http://${domain_prod}:8080`;
// const host = `http://${domain_local}:8080`;
const apiUrl = host + "/api/auth";

let register = true;

const createNewToast = (message) => {
    const cur = toast.cloneNode(true);
    cur.style.display = 'flex';

    const text = cur.getElementsByClassName("message")[0];
    text.innerText = message;

    setInterval(() => {
        cur.remove();
    }, 3000);

    toast_container.appendChild(cur);
};


const validate = () => {
    // Reset alert
    alertForm.style.display = "none";

    var error = false;

    if (usernameInput.value.trim().length == 0) {
        error = true;
    }

    if (passwordInput.value.trim().length == 0) {
        error = true;
    }

    if (roleInput.value == defaultOptions && register) {
        error = true;
    }

    if (error) {
        alertForm.style.display = "block";
    }

    return !error;
};

registerTab.addEventListener('click', () => {
    registerTab.classList.add('active');
    registerButton.style.display = 'block';
    role.style.display = 'block';
    roleLabel.style.display = 'block';

    loginTab.classList.remove('active');
    loginButton.style.display = 'none';

    register = true;
});

loginTab.addEventListener('click', () => {
    loginTab.classList.add('active');
    loginButton.style.display = 'block';
    role.style.display = 'none';
    roleLabel.style.display = 'none';

    registerTab.classList.remove('active');
    registerButton.style.display = 'none';

    register = false;
});

const redirect = (token, endpoint) => {
    fetch(host + endpoint, {
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
    }).then(async response => {
        if (!response.ok) {
            const error = await response.text();
            console.log(error);
            createNewToast(error);
        } 

        return response.json();
    }).then(data => {
        localStorage.setItem("token", data.token);
        redirect(data.token, "/project.html");
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
