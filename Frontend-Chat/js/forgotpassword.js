const API_BASE_URL = 'http://localhost:8080/api/v1';
const apiUrls = {
    Auth: {
        CREATE_NEW_PASSWORD: API_BASE_URL + "/auth/create-password"
    },
    Email: {
        SEND_VERIFICATION: API_BASE_URL + "/email/send-verify",
        VERIFICATION_FORGOT_PASSWORD: API_BASE_URL + "/email/verify"
    }
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
const emailContainer = document.querySelector('#con-email');
const verifyContainer = document.querySelector('#con-verify');
const changeContainer = document.querySelector('#con-change');

var email;

document.addEventListener("DOMContentLoaded", function () {
    document.getElementById('btn-back').addEventListener('click', function () {
        window.location.href = "../pages/login.html";
    });

    // gửi code tới email
    document.getElementById("form-email").addEventListener("submit", function (event) {
        event.preventDefault(); // Prevent the default form submission

        email = document.querySelector("#email").value;
        senVerification(email);
    });


    // Gửi lại mã
    const resendLink = document.getElementById('resend-link');
    const refreshIcon = resendLink.querySelector('.refresh-icon');
    resendLink.addEventListener('click', function (event) {
        event.preventDefault();

        // Thêm lớp rotate để kích hoạt hiệu ứng xoay
        refreshIcon.classList.add('rotate');

        email = document.querySelector("#email").value;
        senVerification(email);

        // Tạm dừng trong một khoảng thời gian nhỏ để hiệu ứng xoay được thấy rõ
        setTimeout(function () {
            refreshIcon.classList.remove('rotate');
        }, 2000); // Cần tùy chỉnh khoảng thời gian phù hợp
    });

    // Xác nhận mã
    document.getElementById("form-code").addEventListener("submit", function (event) {
        event.preventDefault(); // Prevent the default form submission

        const code = document.querySelector("#code").value;
        // alert(email + " " + code);
        verificationCode(email, code);
    });

    // Tạo mật khẩu mới
    document.getElementById("form-change").addEventListener("submit", function (event) {
        event.preventDefault(); // Prevent the default form submission        
        createNewPassword();
    });
});

async function senVerification(email) {
    try {
        // Hiển thị spinner giữa màn hình
        const spinnerContainer = document.createElement('div');
        spinnerContainer.className = 'spinner-container';
        document.body.appendChild(spinnerContainer);
        spinner.spin(spinnerContainer);

        const response = await fetch(apiUrls.Email.SEND_VERIFICATION, {
            method: "POST",
            mode: 'cors', // Bật chế độ CORS
            headers: {
                "Content-Type": "application/json",
            },
            body: JSON.stringify({ email }),
        });

        spinner.stop();

        if (response.ok) {
            const data = await response.json();
            const message = data.data.message;

            toastr.success(message);

            emailContainer.style.display = "none";
            verifyContainer.style.display = "block";
            changeContainer.style.display = "none";

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

            emailContainer.style.display = "";
            verifyContainer.style.display = "none";
            changeContainer.style.display = "none";
        }
    } catch (error) {
        console.log(error);
        // Handle any network or server errors
        alert("Lỗi! Vui lòng thử lại sau");
    }

}

async function verificationCode(email, code) {
    try {

        const formData = new FormData();
        formData.append('email', `${email}`);
        formData.append('code', `${code}`);

        const response = await fetch(apiUrls.Email.VERIFICATION_FORGOT_PASSWORD, {
            method: "POST",
            body: formData,
        });

        if (response.ok) {
            const data = await response.json();
            const message = data.data.message;

            if (data.data.status == 1) {
                toastr.success(message);
                verifyContainer.style.display = "none";
                changeContainer.style.display = "block";
            }
            else {
                toastr.error(message);
            }

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

            verifyContainer.style.display = "block";
            changeContainer.style.display = "none";
        }
    } catch (error) {
        // Handle any network or server errors
        alert("Lỗi! Vui lòng thử lại sau");
    }
}

async function createNewPassword() {
    const newPass = document.querySelector('#new-password');
    const confirmPass = document.querySelector('#confirm-password');

    if (newPass.value == confirmPass.value) {
        const data = {
            email: email,
            password: newPass.value
        };

        const response = await fetch(apiUrls.Auth.CREATE_NEW_PASSWORD, {
            method: "POST",
            headers: {
                "Content-Type": "application/json",
            },
            body: JSON.stringify(data),
        });

        if (response.ok) {
            const data = await response.json();
            const message = data.data.message;

            toastr.success(message);

            setTimeout(() => {
                window.location.href = "../pages/login.html"; // Chuyển trang sau khi thông báo hiện trong 2 giây
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
    }
    else {
        toastr.error("Mật khẩu xác nhận không khớp!");
    }
}