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
    const currentPass = document.querySelector('#current-password');
    const newPass = document.querySelector('#new-password');
    const confirmPass = document.querySelector('#confirm-password');
    const errorContainer = document.getElementById("error-message");
    const formChange = document.getElementById("form");
    const btnChangePass = document.getElementById("btn-update");
    const btnBack = document.getElementById("btn-back");

    let timeoutId; // Biến lưu trữ ID của timeout
    const delay = 1000; // Khoảng thời gian chờ (ms) trước khi kiểm tra

    newPass.addEventListener("input", () => {
        clearTimeout(timeoutId); // Xóa timeout hiện tại

        // Tạo một timeout mới để kiểm tra sau khoảng thời gian delay
        timeoutId = setTimeout(() => {
            const newPassword = newPass.value;
            const confirmPassword = confirmPass.value;
            if (confirmPassword) {
                if (newPassword === confirmPassword) {
                    errorContainer.textContent = "";
                    btnChangePass.disabled = false;
                } else {
                    errorContainer.textContent = "Mật khẩu không khớp!";
                    btnChangePass.disabled = true;
                }
            }
        }, delay);
    });

    confirmPass.addEventListener("input", () => {
        clearTimeout(timeoutId); // Xóa timeout hiện tại

        // Tạo một timeout mới để kiểm tra sau khoảng thời gian delay
        timeoutId = setTimeout(() => {
            const newPassword = newPass.value;
            const confirmPassword = confirmPass.value;

            if (newPassword === confirmPassword) {
                errorContainer.textContent = "";
                btnChangePass.disabled = false;
            } else {
                errorContainer.textContent = "Mật khẩu không khớp!";
                btnChangePass.disabled = true;
            }
        }, delay);
    });

    formChange.addEventListener("submit", async function (event) {
        event.preventDefault();
        if (newPass.value == confirmPass.value) {
            const data = {
                currentPassword: currentPass.value,
                newPassword: newPass.value
            };
            const response = await fetch(apiUrls.User.CHANGE_PASSWORD, {
                method: "POST",
                headers: {
                    "Authorization": `Bearer ${accessToken}`,
                    "Content-Type": "application/json",
                },
                body: JSON.stringify(data),
            });

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
                const data = await response.json();
                if (data.data.status == 1) {

                    toastr.success('Cập nhật mật khẩu thành công!');

                    setTimeout(() => {
                        window.location.href = "../index.html"; // Chuyển trang sau khi thông báo hiện trong 2 giây
                    }, 2000);
                }
                else {
                    const errorMessage = data.data.message; // Access the "message" field                   
                    toastr.error(errorMessage);
                }
            } else {
                if (response.status == 401) {
                    toastr.error('Lỗi');
                    setTimeout(() => {
                        window.location.href = "../index.html"; // Chuyển trang sau khi thông báo hiện trong 2 giây
                    }, 2000);
                }
                else {
                    const errorResponse = await response.json(); // Parse JSON response
                    const errorMessage = errorResponse.message; // Access the "message" field            

                    // Kiểm tra xem có các trường nào trong đối tượng "message"
                    const errorFields = Object.keys(errorMessage);

                    let errorMessageString = "";
                    errorFields.forEach(field => {
                        errorMessageString = `${errorMessage[field]}`;
                    });

                    toastr.error(errorMessageString);
                }
            }
        }

    });

    document.getElementById('btn-back').addEventListener('click', function () {
        window.location.href = "../index.html";
    });
});