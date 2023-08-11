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

    try {
        if (password == confirmPassword) {
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
                localStorage.setItem("message-signup", "Đăng ký thành công!");
                window.location.href = "../pages/login.html";
            }
            else {
                const errorContainer = document.getElementById("error-message");
                const errorResponse = await response.json(); // Parse JSON response
                const errorMessage = errorResponse.message; // Access the "message" field            

                // Kiểm tra xem có các trường nào trong đối tượng "message"
                const errorFields = Object.keys(errorMessage);
               
                let errorMessageString = "";
                errorFields.forEach(field => {
                    errorMessageString = `${errorMessage[field]}`;
                });
                errorContainer.textContent = errorMessageString;
                

            }
        } else {
            const errorContainer = document.getElementById("error-message");
            const errorMessage = "Vui lòng nhập mật khẩu trùng nhau!";
            errorContainer.textContent = errorMessage;
        }

        // Giữ nguyên giá trị các trường
        const signupForm = document.getElementById("signup-form");
        signupForm.elements["fullname"].value = fullname;
        signupForm.elements["username"].value = username;
        signupForm.elements["password"].value = password;
        signupForm.elements["confirm-password"].value = confirmPassword;
        signupForm.elements["email"].value = email;
        
    } catch (error) {
        // Handle any network or server errors
        alert("Error! Please try again later");
    }

}
