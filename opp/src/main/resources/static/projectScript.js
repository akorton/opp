const projects = document.getElementById("projects");
const item = document.getElementsByClassName("item")[0];
const addButton = document.getElementById("add");
const overlay = document.getElementById("overlay");
const modal = document.getElementById("modal");
const titleInput = document.getElementById("title");
const descriptionInput = document.getElementById("description");
const subjectInput = document.getElementById("subject");
const deadlineInput = document.getElementById("deadline");
const executorsInput = document.getElementById("executors");
const modalButton = document.getElementById("submit-project");
const projectIdInput = document.getElementById("project-id");
const id_p = document.getElementById("id-p");
toast_container = document.getElementById("toast-container-project");

// const host = "http://localhost:8080";
const urlApi = host + "/api/project";
const token = localStorage.getItem("token");

let isClient = false;

const setClient = () => {
    addButton.style.display = 'block';
}

const closeModal = () => {
    overlay.style.display = 'none';
    modal.style.display = 'none';
    modalButton.innerText = 'Create';
    titleInput.value = '';
    descriptionInput.value = '';
    subjectInput.value = '';
    deadlineInput.value = '';
    executorsInput.value = '';
    projectIdInput.value = '';
};

modalButton.addEventListener('click', (e) => {
    e.preventDefault();
    var data = JSON.stringify({
            "id": projectIdInput.value,
            "title": titleInput.value,
            "description": descriptionInput.value,
            "subject": subjectInput.value,
            "deadline": deadlineInput.value,
            "executorIds": executorsInput.value.split(",")
        });
    console.log(data);
    const curUrl = modalButton.innerText == 'Create' ? urlApi : urlApi + '/update';
    fetch(curUrl, {
        method: "POST",
        headers: {
            "Authorization": "Bearer " + token,
            'Content-Type': 'application/json'
        },
        body: data
    }).then(async response => {
        if (response.ok) {
            closeModal();
            getProjects();
        } else {
            const error = await response.text();
            console.log(error);
            createNewToast(error);
        }
    })
});

const openModal = () => {
    overlay.style.display = 'block';
    modal.style.display = 'flex';
};

addButton.addEventListener('click', () => {
    openModal();
});

overlay.addEventListener('click', (e) => {
    if (!modal.contains(e.target)) {
        closeModal();
    }
});

const addItem = (itemData) => {
    var curItem = item.cloneNode(true);

    curItem.style.display = "flex";
    
    var title = curItem.getElementsByClassName("title")[0];
    title.innerText = itemData.title;

    var description = curItem.getElementsByClassName("description")[0];
    description.innerText = itemData.description;

    var data = curItem.getElementsByClassName("data")[0];
    
    var subject = data.getElementsByClassName("subject")[0];
    subject.innerText = `Subject: ${itemData.subject}`;

    var deadline = data.getElementsByClassName("deadline")[0];
    deadline.innerText = `Deadline: ${itemData.deadline}`;
    
    var executorIds = data.getElementsByClassName("executorIds")[0];
    executorIds.innerText = `Executors ids: ${itemData.executorIds}`;

    var edit = curItem.getElementsByClassName("edit")[0];
    var deleteBtn = curItem.getElementsByClassName("delete")[0];
    if (isClient) {
        edit.style.display = 'block';
        deleteBtn.style.display = 'block';
        var projectId = itemData.id;

        edit.addEventListener('click', () => {
            openModal();

            projectIdInput.value = projectId;
            titleInput.value = itemData.title;
            descriptionInput.value = itemData.description;
            subjectInput.value = itemData.subject;
            deadlineInput.value = itemData.deadline;
            executorsInput.value = itemData.executorIds;
            modalButton.innerText = 'Save';
        });

        deleteBtn.addEventListener('click', () => {
            fetch(urlApi + `?project_id=${projectId}`, {
                method: "DELETE",
                headers: {
                    "Authorization": "Bearer " + token,
                    'Content-Type': 'application/json'
                }
            }).then(response => {
                if (response.ok) {
                    getProjects();
                }
            })
        });
    }

    curItem.addEventListener('click', (e) => {
        if (edit.contains(e.target) || deleteBtn.contains(e.target)) {
            return;
        }

        localStorage.setItem("projectId", itemData.id);
        redirect(token, "/tasks.html");
    });
    
    projects.appendChild(curItem);
};

const getProjects = () => {
    projects.innerHTML = '';

    fetch(urlApi, {
        method: "GET",
        headers: {
            "Authorization": "Bearer " + token,
            'Content-Type': 'application/json'
        }
    }).then(response => {
        if (!response.ok) {
            throw new Error();
        }

        return response.json();
    }).then(data => {
        console.log(data);
        data.forEach(itemData => addItem(itemData));
    });
};

const checkClient = () => {
    fetch(host + "/api/auth/test/c", {
        method: "GET",
        headers: {
            "Authorization": "Bearer " + token,
            'Content-Type': 'application/json'
        }
    }).then(response => {
        if (response.ok) {
            isClient = true;
            setClient();
        }
    });
}

const setId = () => {
    fetch(host + "/api/auth/id", {
        method: "GET",
        headers: {
            "Authorization": "Bearer " + token,
            'Content-Type': 'application/json'
        }
    }).then(response => {
        if (!response.ok) {
            throw new Error();
        }

        return response.json();
    }).then(data => {
        console.log(data);
        id_p.innerText = `Your id: ${data}`;
    });
}

checkClient();
getProjects();
setId();
