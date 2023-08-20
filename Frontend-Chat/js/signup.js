const API_BASE_URL = 'http://localhost:8080/api/v1';
const apiUrls = {
    // User
    User: {
        REGISTER: API_BASE_URL + "/auth/register",
        GET_CURRENT_USER: API_BASE_URL + "/user/current",
        UPDATE_USER: API_BASE_URL + "/user",
        CHANGE_PASSWORD: API_BASE_URL + "/user/change-password",
        GET_BY_ID: API_BASE_URL + "/user",
        GET_ALL_USER_CONVERSATION: API_BASE_URL + "/user/all/conversation",
    }
}

document.addEventListener("DOMContentLoaded", function () {
    const signupForm = document.getElementById("signup-form");
    signupForm.addEventListener("submit", function (event) {
        event.preventDefault(); // Prevent the default form submission

        const formData = new FormData(signupForm);
        const fullname = formData.get("fullname");
        const username = formData.get("username");
        const password = formData.get("password");
        const confirmPassword = formData.get("confirm-password");
        const email = formData.get("email");

        signup(fullname, username, password, confirmPassword, email);
    });
});

async function signup(fullname, username, password, confirmPassword, email) {
    if (password == confirmPassword) {
        try {
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

            // Tạo đối tượng chứa dữ liệu của bạn
            const data = {
                fullName: fullname,
                username: username,
                password: password,
                email: email
            };
            const response = await fetch(apiUrls.User.REGISTER, {
                method: "POST",
                mode: 'cors', // Bật chế độ CORS
                headers: {
                    "Content-Type": "application/json",
                },
                body: JSON.stringify(data),
            });

            if (response.ok) {
                toastr.success('Đăng ký thành công!');

                setTimeout(() => {
                    window.location.href = "../pages/login.html"; // Chuyển trang sau khi thông báo hiện trong 2 giây
                }, 2000);
            }
            else {
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
            // Handle any network or server errors
            alert("Error! Please try again later");
        }
    } else {
        const errorMessage = "Vui lòng nhập mật khẩu trùng nhau!";
        toastr.error(errorMessage);
    }

    // Giữ nguyên giá trị các trường
    const signupForm = document.getElementById("signup-form");
    signupForm.elements["fullname"].value = fullname;
    signupForm.elements["username"].value = username;
    signupForm.elements["password"].value = password;
    signupForm.elements["confirm-password"].value = confirmPassword;
    signupForm.elements["email"].value = email;

}
