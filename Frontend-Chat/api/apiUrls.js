// Định nghĩa các đường dẫn API
const API_BASE_URL = 'http://localhost:8080/api/v1'; // Đường dẫn cơ bản của API

const apiUrls = {
    // Đường dẫn API cho phần đăng nhập và xác thực (Auth)
    Auth: {
        LOGIN: API_BASE_URL + "/auth/login",
        LOGOUT: API_BASE_URL + "/auth/logout",
        RESET_PASSWORD: API_BASE_URL + "/auth/reset-password",
    },

    // Đường dẫn API cho phần thông tin người dùng (User)
    User: {        
        REGISTER: API_BASE_URL + "/auth/register",
        GET_CURRENT_USER: API_BASE_URL + "/user/current",
        UPDATE_USER: API_BASE_URL + "/user",
        CHANGE_PASSWORD: API_BASE_URL + "/user/change-password",
        GET_BY_ID: API_BASE_URL + "/user/${id}",
    },

    // Đường dẫn API cho phần tin nhắn (Message)
    Message: {
        
    },
}
// Xuất hằng số apiUrls để có thể sử dụng trong các file khác
export default apiUrls;