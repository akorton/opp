const projects = document.getElementById("projects");
const item = document.getElementsByClassName("item")[0];

// const host = "localhost:8080";
const urlApi = host + "/api/project";
const token = localStorage.getItem("token");

const addItem = (itemData) => {
    var curItem = item.cloneNode(true);

    curItem.style.display = "flex";
    
    var title = curItem.getElementsByClassName("title")[0];
    title.innerText = itemData.title;

    var description = curItem.getElementsByClassName("description")[0];
    description.innerText = itemData.description;

    var data = curItem.getElementsByClassName("data")[0];
    
    var subject = data.getElementsByClassName("subject")[0];
    subject.innerText = itemData.subject;

    var deadline = data.getElementsByClassName("deadline")[0];
    deadline.innerText = itemData.deadline;
    
    var executorIds = data.getElementsByClassName("executorIds")[0];
    executorIds.innerText = itemData.executorIds;
    
    projects.appendChild(curItem);
};

const getProjects = () => {
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

getProjects();
