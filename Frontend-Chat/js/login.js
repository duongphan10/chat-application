const API_BASE_URL = 'http://localhost:8080/api/v1';
const apiUrls = {
    Auth: {
        LOGIN: API_BASE_URL + "/auth/login",
        LOGOUT: API_BASE_URL + "/auth/logout",
        RESET_PASSWORD: API_BASE_URL + "/auth/reset-password",
    }
}

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

document.addEventListener("DOMContentLoaded", function () {
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
            toastr.success("Đăng nhập thành công");
            setTimeout( function() {                
                window.location.href = "../index.html"; // Redirect to the chat page after successful login
            }, 1000);
            
        } else {
            // Handle login error (e.g., incorrect username/password)
            const errorResponse = await response.json(); 
            const errorMessage = errorResponse.message; 
            toastr.error(errorMessage)

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
