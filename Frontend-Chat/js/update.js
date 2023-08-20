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
var defautAvatarSrc;
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
            avatarThumbnail.src = defautAvatarSrc;
            avatarChanged = false;
        }
    });


    closeButton.addEventListener("click", function () {
        fullImageOverlay.style.display = "none";
    });

    const btnUpdate = document.getElementById('btn-update');
    btnUpdate.addEventListener('click', async function () {
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

                // Tạo Spinner và thiết lập tùy chọn
                const spinnerOptions = {
                    lines: 12,
                    length: 10,
                    width: 6,
                    radius: 12,
                    color: '#000',
                    speed: 1,
                    trail: 100,
                    shadow: false,
                };
                const spinner = new Spinner(spinnerOptions);

                // Hiển thị spinner giữa màn hình
                const spinnerContainer = document.createElement('div');
                spinnerContainer.className = 'spinner-container';
                document.body.appendChild(spinnerContainer);
                spinner.spin(spinnerContainer);


                const response = await fetch(apiUrls.User.UPDATE_USER, {
                    method: "PATCH",
                    headers: {
                        "Authorization": `Bearer ${accessToken}`,
                    },
                    body: formData,
                });
                
                spinner.stop();

                toastr.options = {
                    newestOnTop: true,           // Hiển thị thông báo mới nhất ở trên cùng
                    preventDuplicates: false,
                    positionClass: 'toast-top-center',
                    toastClass: 'toastr-custom-width',  
                    showEasing: 'swing',         // Hiệu ứng hiển thị
                    hideEasing: 'linear',        // Hiệu ứng ẩn
                    showMethod: 'fadeIn',        // Phương thức hiển thị
                    hideMethod: 'fadeOut',        // Phương thức ẩn      
                    timeOut: '3000',             // Thời gian tự động ẩn thông báo (milliseconds)            
                };
                if (response.ok) {                    
                    toastr.success('Cập nhật thành công!');
            
                    setTimeout(() => {
                        window.location.href = "../index.html"; // Chuyển trang sau khi thông báo hiện trong 2 giây
                    }, 2000);
                } else {                     
                    const errorResponse = await response.json(); // Parse JSON response
                    const errorMessage = errorResponse.message; // Access the "message" field            
                    let errorMessageString = "";
                    if (typeof errorMessage === 'string') {
                        errorMessageString = errorMessage;
                    }
                    else {
                        // Kiểm tra xem có các trường nào trong đối tượng "message"
                        const errorFields = Object.keys(errorMessage);
                        errorFields.forEach(field => {
                            errorMessageString = `${errorMessage[field]}`;
                        });
                    }
                    toastr.error(errorMessageString);
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
            document.querySelector('#old-avatar').src = data.data.avatar ? data.data.avatar : "../assets/image/avatar.jpg";
            defautAvatarSrc = document.querySelector('#old-avatar').src;
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