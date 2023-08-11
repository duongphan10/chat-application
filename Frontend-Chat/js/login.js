const API_BASE_URL = 'http://localhost:8080/api/v1';
const apiUrls = {
    Auth: {
        LOGIN: API_BASE_URL + "/auth/login",
        LOGOUT: API_BASE_URL + "/auth/logout",
        RESET_PASSWORD: API_BASE_URL + "/auth/reset-password",
    }
}

document.addEventListener("DOMContentLoaded", function () {
    //Xử lý chuyển hướng từ signup
    const messageSignup = localStorage.getItem("message-signup");
    if (messageSignup) {
        const messageContainer = document.getElementById("message-signup");
        messageContainer.textContent = messageSignup;
        localStorage.removeItem("message-signup");
    }

    // Xử lý khi đăng nhập
    const loginForm = document.getElementById("login-form");
    loginForm.addEventListener("submit", function (event) {
        event.preventDefault(); // Prevent the default form submission
        
        const formData = new FormData(loginForm);
        const username = formData.get("username");
        const password = formData.get("password");

        login(username, password);
    });
});

async function login(username, password) {    
    
    try {
        const response = await fetch(apiUrls.Auth.LOGIN, {
            method: "POST",
            mode: 'cors', // Bật chế độ CORS
            headers: {
                "Content-Type": "application/json",
            },
            body: JSON.stringify({ username, password }),
        });

        if (response.ok) {
            const data = await response.json();
            const accessToken = data.data.accessToken;   

            // Save the access token in localStorage
            localStorage.setItem("accessToken", accessToken);

            window.location.href = "../index.html"; // Redirect to the chat page after successful login
        } else {
            // Handle login error (e.g., incorrect username/password)
            const errorResponse = await response.json(); // Parse JSON response
            const errorMessage = errorResponse.message; // Access the "message" field
            const errorContainer = document.getElementById("error-message");
            errorContainer.textContent = errorMessage;

            // Giữ nguyên giá trị tài khoản và mật khẩu người dùng
            const loginForm = document.getElementById("login-form");
            loginForm.elements["username"].value = username;
            loginForm.elements["password"].value = password;
        }
    } catch (error) {
        // Handle any network or server errors
        alert("Lỗi! Vui lòng thử lại sau");
    }
    
}
