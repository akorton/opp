const tasks = document.getElementById("tasks");
const itemTask = document.getElementsByClassName("item-task")[0];
const addTaskButton = document.getElementById("add-task");
const overlayTask = document.getElementById("overlay-task");
const modalTask = document.getElementById("modal-task");
const descriptionTaskInput = document.getElementById("description-task");
const deadlineTaskInput = document.getElementById("deadline-task");
const executorsTaskInput = document.getElementById("executors-task");
const prerequisitesTaskInput = document.getElementById("prerequisites-task");
const prerequisitesTaskLabel = document.getElementById("prerequisites-task-label");
const statusTaskInput = document.getElementById("status-task");
const statusTaskLabel = document.getElementById("status-task-label");
const modalTaskButton = document.getElementById("submit-task");
const taskIdInput = document.getElementById("task-id");

const urlTaskApi = host + "/api/task";
// const token = localStorage.getItem("token");

const projectId = localStorage.getItem("projectId");
let cols = [];

let isExecutor = false;

const setExecutor = () => {
    addTaskButton.style.display = 'block';
}

const closeTaskModal = () => {
    overlayTask.style.display = 'none';
    modalTask.style.display = 'none';
    modalTaskButton.innerText = 'Create';
    descriptionTaskInput.value = '';
    deadlineTaskInput.value = '';
    executorsTaskInput.value = '';
    prerequisitesTaskInput.value = '';
};

modalTaskButton.addEventListener('click', (e) => {
    e.preventDefault();

    var data = JSON.stringify({
            "id": taskIdInput.value,
            "description": descriptionTaskInput.value,
            "deadline": deadlineTaskInput.value,
            "executorIds": executorsTaskInput.value.split(","),
            "prerequisiteIds": prerequisitesTaskInput.value.split(","),
            "taskStatus": statusTaskInput.value == "FINISHED" || statusTaskInput.value == "IN_PROGRESS" ? statusTaskInput.value : null,
            "projectId": projectId
        });
    console.log(data);
    const curUrl = modalTaskButton.innerText == 'Create' ? urlTaskApi : urlTaskApi + '/update';
    fetch(curUrl, {
        method: "POST",
        headers: {
            "Authorization": "Bearer " + token,
            'Content-Type': 'application/json'
        },
        body: data
    }).then(response => {
        if (response.ok) {
            closeTaskModal();
            getTasks();
        }
    })
});

const openTaskModal = () => {
    overlayTask.style.display = 'block';
    modalTask.style.display = 'flex';

    let displayStyle = modalTaskButton.innerText == "Create" ? 'none' : 'block';
    prerequisitesTaskInput.style.display = displayStyle;
    statusTaskInput.style.display = displayStyle;
    prerequisitesTaskLabel.style.display = displayStyle;
    statusTaskLabel.style.display = displayStyle;
};

addTaskButton.addEventListener('click', () => {
    openTaskModal();
});

overlayTask.addEventListener('click', (e) => {
    if (!modalTask.contains(e.target)) {
        closeTaskModal();
    }
});

const getColorByStatus = (status) => {
    if (status == "NOT_FINISHED") {
        return "#ff0000";
    } else if (status == "FINISHED") {
        return "#00ff00";
    } else if (status == "PREREQUISITES_NOT_MET") {
        return "#888888";
    } else {
        return "#ffa500";
    }
};

const addTaskItem = (itemColData) => {
    var curItem = itemTask.cloneNode(true);

    curItem.style.display = "flex";

    let curColIdx = itemColData.column - 1;
    let itemData = itemColData.taskDto;
    
    var title = curItem.getElementsByClassName("title")[0];
    title.innerText = `Task #${itemData.id}`;

    var description = curItem.getElementsByClassName("description")[0];
    description.innerText = itemData.description;

    var deadline = curItem.getElementsByClassName("deadline")[0];
    deadline.innerText = `Deadline: ${itemData.deadline}`;

    var data = curItem.getElementsByClassName("data")[0];
    
    var prerequisites = data.getElementsByClassName("prerequisites")[0];
    prerequisites.innerText = `Prerequisites: ${itemData.prerequisiteIds}`;
    
    var executorIds = data.getElementsByClassName("executorIds")[0];
    executorIds.innerText = `Executors ids: ${itemData.executorIds}`;

    var status = curItem.getElementsByClassName("status")[0];
    status.innerText = itemData.taskStatus;
    status.style.color = getColorByStatus(status.innerText);

    var edit = curItem.getElementsByClassName("edit")[0];
    if (isExecutor) {
        edit.style.display = 'block';
        var taskId = itemData.id;

        edit.addEventListener('click', () => {
            modalTaskButton.innerText = 'Save';
            openTaskModal();

            taskIdInput.value = taskId;
            descriptionTaskInput.value = itemData.description;
            prerequisitesTaskInput.value = itemData.prerequisiteIds;
            deadlineTaskInput.value = itemData.deadline;
            executorsTaskInput.value = itemData.executorIds;
            statusTaskInput.value = itemData.taskStatus;
        });
    }
    
    cols[curColIdx].appendChild(curItem);
};

const getTasks = () => {
    tasks.innerHTML = '';

    fetch(urlTaskApi + `?project_id=${projectId}`, {
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
        let maxCol = 1; 

        data.forEach(item => {
            maxCol = maxCol > item.column ? maxCol : item.column;
        });

        cols = Array.from({ length: maxCol });

        for (let i = 0; i < maxCol; ++i) {
            let curCol = document.createElement("div");
            curCol.classList.add("col");

            tasks.appendChild(curCol);
            cols[i] = curCol;
        }

        data.forEach(itemData => addTaskItem(itemData));
    });
};

const checkExecutor = () => {
    fetch(host + "/api/auth/test/e", {
        method: "GET",
        headers: {
            "Authorization": "Bearer " + token,
            'Content-Type': 'application/json'
        }
    }).then(response => {
        if (response.ok) {
            isExecutor = true;
            setExecutor();
        }
    });
}

checkExecutor();
getTasks();

