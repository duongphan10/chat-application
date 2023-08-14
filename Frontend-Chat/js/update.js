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
let avatarChanged = false;
// Lấy accessToken từ localStorage
const accessToken = localStorage.getItem("accessToken");

document.addEventListener("DOMContentLoaded", function () {
    getData();

    const avatarThumbnail = document.getElementById("old-avatar");
    const fullImageOverlay = document.getElementById("full-image-overlay");
    const fullAvatar = document.getElementById("full-avatar");
    const closeButton = document.getElementById("close-button");

    avatarThumbnail.addEventListener("click", function () {
        const thumbnailSrc = avatarThumbnail.src;
        fullAvatar.src = thumbnailSrc;
        fullImageOverlay.style.display = "flex";
    });


    document.querySelector('.avatar').addEventListener('change', (event) => {
        const selectedFile = event.target.files[0]; // Lấy file đầu tiên được chọn (nếu có)    
        if (selectedFile) {
            avatarThumbnail.src = URL.createObjectURL(selectedFile);
            avatarChanged = true; // Đánh dấu hình ảnh đã thay đổi
        }
        else {
            // avatarThumbnail.src = 
            avatarChanged = false;
        }
    });


    closeButton.addEventListener("click", function () {
        fullImageOverlay.style.display = "none";
    });

    document.getElementById('btn-update').addEventListener('click', async function () {
        var result = window.confirm("Bạn có chắc muốn cập nhật thông tin?");
        if (result) {
            try {
                const fullName = document.querySelector('.fullname').value;
                const username = document.querySelector('.username').value;
                const email = document.querySelector('.email').value;
            
                // Sử dụng FormData để tạo dữ liệu gửi lên
                const formData = new FormData();
                formData.append('fullName', fullName);
                formData.append('username', username);
                formData.append('email', email);
                if (avatarChanged) {
                    formData.append('avatar', document.querySelector('.avatar').files[0]);
                }
                
                const response = await fetch(apiUrls.User.UPDATE_USER, {
                    method: "PATCH",
                    headers: {
                        "Authorization": `Bearer ${accessToken}`,
                    },
                    body: formData,
                });
                toastr.options = {
                    positionClass: 'toast-top-center',
                    toastClass: 'toastr-custom-width',                    
                };
                if (response.ok) {                    
                    toastr.success('Cập nhật thành công!');
            
                    setTimeout(() => {
                        window.location.href = "../index.html"; // Chuyển trang sau khi thông báo hiện trong 2 giây
                    }, 2000);
                } else {     
                    const errorResponse = await response.json(); // Parse JSON response
                    const errorMessage = errorResponse.message; // Access the "message" field            

                    // Kiểm tra xem có các trường nào trong đối tượng "message"
                    const errorFields = Object.keys(errorMessage);

                    let errorMessageString = "";
                    errorFields.forEach(field => {
                        errorMessageString = `${errorMessage[field]}`;
                    });
                
                    toastr.error(errorMessage, 'Cập nhật thất bại');
                }
            } catch (error) {
                alert("Lỗi! Vui lòng thử lại sau");
            }
            
        }
    });

    document.getElementById('btn-back').addEventListener('click', function () {
        window.location.href = "../index.html";
    });

});

async function getData() {
    try {
        const response = await fetch(apiUrls.User.GET_CURRENT_USER, {
            method: "GET",
            mode: 'cors', // Bật chế độ CORS
            headers: {
                "Content-Type": "application/json",
                "Authorization": `Bearer ${accessToken}`,
            },
        });


        if (response.ok) {
            const data = await response.json();
            if (data.data.avatar)
                document.querySelector('#old-avatar').src = data.data.avatar;
            document.querySelector('.fullname').value = data.data.fullName;
            document.querySelector('.username').value = data.data.username;
            document.querySelector('.email').value = data.data.email;
        }
        else
            alert("Lỗi trong quá trình xử lý: " + response.status);
    } catch (error) {
        // Handle any network or server errors
        alert("Lỗi! Vui lòng thử lại sau");
    }
}

// async function updateInfo(accessToken) {
    
// }