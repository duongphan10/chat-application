const API_BASE_URL = 'http://localhost:8080/api/v1';
const apiUrls = {
    // User
    User: {
        REGISTER: API_BASE_URL + "/auth/register",
        GET_CURRENT_USER: API_BASE_URL + "/user/current",
        UPDATE_USER: API_BASE_URL + "/user",
        CHANGE_PASSWORD: API_BASE_URL + "/user/change-password",
        GET_BY_ID: API_BASE_URL + "/user",
        GET_BY_USERNAME: API_BASE_URL + "/user/get",
    }
}

// Lấy accessToken từ localStorage
const accessToken = localStorage.getItem("accessToken");

document.addEventListener("DOMContentLoaded", function () {

    getData();

    const avatarThumbnail = document.getElementById("avatar-thumbnail");
    const fullImageOverlay = document.getElementById("full-image-overlay");
    const fullAvatar = document.getElementById("full-avatar");
    const closeButton = document.getElementById("close-button");

    avatarThumbnail.addEventListener("click", function () {
        const thumbnailSrc = avatarThumbnail.src;
        fullAvatar.src = thumbnailSrc;
        fullImageOverlay.style.display = "flex";
    });

    closeButton.addEventListener("click", function () {
        fullImageOverlay.style.display = "none";
    });

    document.getElementById('btn-update').addEventListener('click', function () {
        window.location.href = "../pages/update.html";
    });

    document.getElementById('btn-back').addEventListener('click', function () {
        window.location.href = "../index.html";
    });
});

async function getData() {
    const username = window.location.hash.substring(1); // Lấy phần sau dấu #
    try {
        const response = await fetch(`${apiUrls.User.GET_BY_USERNAME}?username=${username}`, {
            method: "GET",
            mode: 'cors', // Bật chế độ CORS
            headers: {
                "Content-Type": "application/json",
                "Authorization": `Bearer ${accessToken}`,
            }
        });
        const response1 = await fetch(apiUrls.User.GET_CURRENT_USER, {
            method: "GET",
            mode: 'cors', // Bật chế độ CORS
            headers: {
                "Content-Type": "application/json",
                "Authorization": `Bearer ${accessToken}`,
            },
        });        
        const data1 = await response1.json();
        
        if (response.ok) {
            const data = await response.json();
            if (data.data.avatar)
                document.getElementById("avatar-thumbnail").src = data.data.avatar;
            document.querySelector('.fullname').textContent = data.data.fullName;
            document.querySelector('.username').textContent = data.data.username;
            document.querySelector('.email').textContent = data.data.email;
            if (data.data.id != data1.data.id) {
                document.getElementById("btn-update").style.display = "none";
            }
        }
        else
            alert("Lỗi trong quá trình xử lý: " + response.status);
    } catch (error) {
        // Handle any network or server errors
        alert("Lỗi! Vui lòng thử lại sau");
    }
}