const API_BASE_URL = 'http://localhost:8080/api/v1';
const URL_SOCKET = 'http://localhost:9090';
const apiUrls = {
    // Auth
    Auth: {
        LOGIN: API_BASE_URL + "/auth/login",
        LOGOUT: API_BASE_URL + "/auth/logout",
        RESET_PASSWORD: API_BASE_URL + "/auth/reset-password",
    },
    // User
    User: {
        REGISTER: API_BASE_URL + "/auth/register",
        GET_CURRENT_USER: API_BASE_URL + "/user/current",
        UPDATE_USER: API_BASE_URL + "/user",
        CHANGE_PASSWORD: API_BASE_URL + "/user/change-password",
        GET_BY_ID: API_BASE_URL + "/user",
        GET_ALL_USER_CONVERSATION: API_BASE_URL + "/user/all/conversation",
        SEARCH_FRIEND: API_BASE_URL + "/user/search/friend",
        SEARCH_OTHER_USER: API_BASE_URL + "/user/search/other",
    },
    // Message
    Message: {
        GET_MESSAGE_WITH_OTHER_ID: API_BASE_URL + "/message/me",
    },
}

// Lấy accessToken từ localStorage
const accessToken = localStorage.getItem("accessToken");
checkToken(accessToken);
const socket = io(URL_SOCKET + `?accessToken=${accessToken}`);
var myId;
var currentPageMessage = 0;
var selectedUserId = null;
getCurrentUser(accessToken);
loadConversations(accessToken);
document.addEventListener('DOMContentLoaded', function () {    

    socket.on('server_send_message', (message) => {
        if (myId == message.senderId || (myId == message.receiverId && selectedUserId == message.senderId)) {
            currentPageMessage = 1;
            bodyMessage(selectedUserId, accessToken, currentPageMessage);
            bodyConversation(accessToken, 1);
        }
        else {
            if (myId == message.receiverId)
                bodyConversation(accessToken, 1);
        }
    });

    // Bắt sự kiện liên quan đến search friend
    const searchBox = document.querySelector('.row .searchBox .searchBox-inner');
    const searchInput = searchBox.querySelector('.form-control');
    const searchButton = searchBox.querySelector('.search-button');
    const colorSearchIcon = window.getComputedStyle(searchButton).color;

    searchInput.addEventListener('input', function () {
        // Thay đổi màu nút search
        if (searchInput.value.trim() !== '') {
            searchButton.style.color = 'black';
        } else {
            bodyConversation(accessToken, 1);
            searchButton.style.color = colorSearchIcon;
        }
    });

    searchInput.addEventListener('keydown', function (event) {
        if (event.key === 'Enter' && !event.shiftKey) {
            event.preventDefault();
            if (searchInput.value.trim() !== '')
                bodyConversation(accessToken, 2);
        }
    });

    searchButton.addEventListener("click", function () {
        if (searchInput.value.trim() !== '')
            bodyConversation(accessToken, 2);
    });

    // Bắt sự kiện liên quan đến tìm kiếm người dùng khác  
    const searchComposeBox = document.querySelector('.row .composeBox .composeBox-inner');
    const searchInputComposeBox = searchComposeBox.querySelector('.form-control');
    const searchButtonComposeBox = searchComposeBox.querySelector('.search-button');
    const colorSearchIconComposeBox = window.getComputedStyle(searchButtonComposeBox).color;

    searchInputComposeBox.addEventListener('input', function () {
        // Thay đổi màu nút search
        if (searchInputComposeBox.value.trim() !== '') {
            searchButtonComposeBox.style.color = 'black';
        } else {
            bodySearchUser(accessToken, 0);
            searchButtonComposeBox.style.color = colorSearchIconComposeBox;
        }
    });

    searchInputComposeBox.addEventListener('keydown', function (event) {
        if (event.key === 'Enter' && !event.shiftKey) {
            event.preventDefault();
            if (searchInputComposeBox.value.trim() !== '')
                bodySearchUser(accessToken, 1);
        }
    });

    searchButtonComposeBox.addEventListener("click", function () {
        if (searchInputComposeBox.value.trim() !== '')
            bodySearchUser(accessToken, 1);
    });

    // Bắt sự kiện logout
    document.getElementById('menu-logout').addEventListener('click', function () {
        var result = window.confirm("Bạn có chắc muốn đăng xuất?");
        if (result) {
            logout();
        }
    });


});



// Check token
function checkToken(accessToken) {
    // Kiểm tra xem accessToken có tồn tại hay không
    if (!accessToken) {
        logout();
        return; // Thoát khỏi hàm ngay sau khi gọi logout()
    }
    else {
        // Giải mã token JWT và lấy thông tin payload (dữ liệu) trong token
        const decodedToken = JSON.parse(atob(accessToken.split(".")[1]));

        // Lấy giá trị "exp" trong payload để kiểm tra thời gian hết hạn
        const expirationTime = decodedToken.exp * 1000; // Convert seconds to milliseconds

        const currentTime = Date.now();
        if (currentTime >= expirationTime) {
            // Token đã hết hạn, yêu cầu người dùng đăng nhập lại
            alert("Phiên hoạt động hết hạn. Đăng nhập lại!");
            logout();
            return;
        }
    }
}
// LOGOUT 
function logout() {
    localStorage.clear();
    window.location.href = "../Frontend-Chat/pages/login.html";
}

async function getCurrentUser(accessToken) {
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
            myId = data.data.id;
        }
        else
            alert("Lỗi trong quá trình xử lý: " + response.status);
    } catch (error) {
        // Handle any network or server errors
        alert("Lỗi! Vui lòng thử lại sau");
    }
}

// CONVERSATIONS --------
async function loadConversations(accessToken) {

    headerConversation(accessToken);
    bodyConversation(accessToken, 1);

}

// HEADER PROFILE -------
async function headerConversation(accessToken) {
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
            // Change my avatar            
            if (data.data.avatar) {
                const avatarImage = document.getElementById("avatarImage");
                const avatarSettingImage = document.querySelector('.menu-heading-img img');
                avatarImage.src = data.data.avatar;
                avatarSettingImage.src = data.data.avatar;
            }
            // Thay fullname           
            const avatarName = document.getElementById("myname");
            const avatarSettingName = document.querySelector('.menu-heading-name .menu-avatar-name');
            avatarName.textContent = data.data.fullName;
            avatarSettingName.textContent = data.data.fullName;
        }
        else
            alert("Lỗi trong quá trình xử lý: " + response.status);
    } catch (error) {
        // Handle any network or server errors
        alert("Lỗi! Vui lòng thử lại sau");
    }
}

// LIST CONVERSATIONS 
async function bodyConversation(accessToken, type) {
    try {
        var response;
        if (type == 1) {
            response = await fetch(apiUrls.User.GET_ALL_USER_CONVERSATION, {
                method: "GET",
                mode: 'cors', // Bật chế độ CORS
                headers: {
                    "Content-Type": "application/json",
                    "Authorization": `Bearer ${accessToken}`,
                },
            });
        }
        else {
            if (type == 2) {
                const searchBox = document.querySelector('.row .searchBox .searchBox-inner');
                const searchInput = searchBox.querySelector('.form-control');
                const searchValue = searchInput.value;
                response = await fetch(apiUrls.User.SEARCH_FRIEND + `?search=${searchValue}`, {
                    method: "GET",
                    mode: 'cors', // Bật chế độ CORS
                    headers: {
                        "Content-Type": "application/json",
                        "Authorization": `Bearer ${accessToken}`,
                    },
                });
            }
        }
        if (response.ok) {
            const data = await response.json();
            const userListData = data.data.items;
            const userListContainer = document.getElementById("userList");
            userListContainer.innerHTML = '';
            for (const user of userListData) {
                // Lấy tin nhắn cuối cùng 2 người nhắn nhau
                const response1 = await fetch(apiUrls.Message.GET_MESSAGE_WITH_OTHER_ID + `/${user.id}?pageNum=1`, {
                    method: "GET",
                    mode: 'cors', // Bật chế độ CORS
                    headers: {
                        "Content-Type": "application/json",
                        "Authorization": `Bearer ${accessToken}`,
                    },
                });
                const data1 = await response1.json();
                const messageListData = data1.data.items;
                var message = "";
                const timeLastMessage = formatTime(new Date(messageListData[0].createdDate), 1);
                if (messageListData[0].sender_id == user.id) {
                    message = messageListData[0].message;
                }
                else {
                    message = "Bạn: " + messageListData[0].message;
                }

                // Xử lý nếu avatar null
                const avatar = user.avatar ? user.avatar : "../Frontend-Chat/assets/image/avatar.jpg";
                // Xử lý icon online
                const status = (user.status == "ONLINE") ? "online-icon" : "";

                const userDiv = document.createElement("div");
                userDiv.classList.add("row", "sideBar-body");
                const userContent = `                  
                    <div class="col-sm-3 col-xs-3 sideBar-avatar">
                        <div class="avatar-icon">
                            <img src="${avatar}" >
                            <span class="${status}"></span>
                        </div>
                    </div>
                    <div class="col-sm-9 col-xs-9 sideBar-main">
                        <div class="row">
                            <div class="col-sm-8 col-xs-8 sideBar-name">
                                <span class="name-meta">
                                    ${user.fullName}
                                </span>
                                <span class="message-meta">
                                    ${message}
                                </span>
                            </div>
                            <div class="col-sm-4 col-xs-4 pull-right sideBar-time">
                                <span class="time-meta pull-right">
                                    ${timeLastMessage}
                                </span>
                            </div>
                        </div>
                    </div>                  
                `;
                userDiv.innerHTML = userContent;
                userListContainer.appendChild(userDiv);
                // Xử lý sự kiện khi bấm vào tên người dùng
                userDiv.addEventListener("click", () => handlerConversation(user.id, accessToken));
            }
        }
        else
            alert("Lỗi trong quá trình xử lý: " + response.status);
    } catch (error) {
        // Handle any network or server errors
        alert("Lỗi! Vui lòng thử lại sau");
    }
}

async function bodySearchUser(accessToken, type) {
    try {
        const searchComposeBox = document.querySelector('.row .composeBox .composeBox-inner');
        const searchInputComposeBox = searchComposeBox.querySelector('.form-control');
        const searchValue = searchInputComposeBox.value;

        const userListContainer = document.querySelector('.row .compose-sideBar');
        userListContainer.innerHTML = '';
        if (type == 1) {
            const response = await fetch(apiUrls.User.SEARCH_OTHER_USER + `?search=${searchValue}`, {
                method: "GET",
                mode: 'cors', // Bật chế độ CORS
                headers: {
                    "Content-Type": "application/json",
                    "Authorization": `Bearer ${accessToken}`,
                },
            });

            if (response.ok) {
                const data = await response.json();
                const userListData = data.data.items;

                for (const user of userListData) {
                    // Xử lý nếu avatar null
                    const avatar = user.avatar ? user.avatar : "../Frontend-Chat/assets/image/avatar.jpg";
                    // Xử lý icon online
                    const status = (user.status == "ONLINE") ? "online-icon" : "";

                    const userDiv = document.createElement("div");
                    userDiv.classList.add("row", "sideBar-body");
                    const userContent = `                                         
                        <div class="col-sm-3 col-xs-3 sideBar-avatar">
                            <div class="avatar-icon">
                                <img src="${avatar}">
                            </div>
                        </div>
                        <div class="col-sm-9 col-xs-9 sideBar-main">
                            <div class="row">
                                <div class="col-sm-8 col-xs-8 sideBar-name">
                                    <span class="name-meta">
                                        ${user.fullName}
                                    </span>
                                    <span class="message-meta">
                                       << ${user.username} >>
                                    </span>
                                </div>
                                <div class="col-sm-4 col-xs-4 pull-right sideBar-time">
                                    <span class="time-meta pull-right">
                                        <!-- 18:18 -->
                                    </span>
                                </div>
                            </div>
                        </div>
                    `;
                    userDiv.innerHTML = userContent;
                    userListContainer.appendChild(userDiv);
                    // Xử lý sự kiện khi bấm vào tên người dùng
                    userDiv.addEventListener("click", () => handlerConversation(user.id, accessToken));
                }
            }
            else
                alert("Lỗi trong quá trình xử lý: " + response.status);
        }
    } catch (error) {
        // Handle any network or server errors
        alert("Lỗi! Vui lòng thử lại sau");
    }
}

// Xử lý sự kiện khi bấm vào người dùng trong danh sách cuộc hội thoại
async function handlerConversation(userOtherId, accessToken) {
    currentPageMessage = 1;
    selectedUserId = userOtherId;
    const conversationContainer = document.getElementById("conversation-container");
    try {
        const response = await fetch(apiUrls.User.GET_BY_ID + `/${selectedUserId}`, {
            method: "GET",
            mode: 'cors', // Bật chế độ CORS
            headers: {
                "Content-Type": "application/json",
                "Authorization": `Bearer ${accessToken}`,
            },
        });
        if (response.ok) {
            const data = await response.json();
            // Hiển thị header conversation
            headerMessage(data);
            // Hiện thị tin nhắn   
            bodyMessage(selectedUserId, accessToken, currentPageMessage);
            // Hiển thị reply
            replyMessage();

            // Xử lý cuộn trang để xem tin nhắn cũ
            const messageContainer = document.getElementById("conversation");
            messageContainer.addEventListener("scroll", function () {
                const scrollTop = messageContainer.scrollTop;
                if (scrollTop < messageContainer.clientHeight / 3) {
                    // Tải thêm tin nhắn từ trang tiếp theo (currentPageMessage + 1)
                    currentPageMessage++;
                    bodyMessage(selectedUserId, accessToken, currentPageMessage);
                }
            });

            // Xử lý khi viết tin nhắn
            const rowHeading = document.querySelector('.row.heading#header-conversation');
            const rowMessage = document.querySelector('.row.message#conversation');
            const rowReply = document.querySelector('.row.reply#reply');
            const replyMain = document.querySelector('.reply-main');
            const textarea = replyMain.querySelector('textarea');
            const sendButton = document.getElementById('sendButton');
            const sendIcon = document.getElementById('sendIcon');

            // Tính chiều cao
            const conversationHeight = conversationContainer.clientHeight;
            const headerHeight = rowHeading.clientHeight;
            const messageHeight = rowMessage.clientHeight;
            const replyHeight = rowReply.clientHeight;
            const textAreaHeight = textarea.clientHeight;

            const replyStyle = window.getComputedStyle(rowReply);
            const totalPeplyPadding = parseFloat(replyStyle.paddingTop) + parseFloat(replyStyle.paddingBottom);

            const replyMainStyle = window.getComputedStyle(replyMain);
            const totalReplyMainPadding = parseFloat(replyMainStyle.paddingTop) + parseFloat(replyMainStyle.paddingBottom);

            const maxHeight = parseInt(getComputedStyle(textarea).maxHeight);
            const colorSendIcon = window.getComputedStyle(sendIcon).color;

            // Xử lý sự kiện nhập nội dung tin nhắn
            textarea.addEventListener('input', function () {
                // Thay đổi màu nút gửi
                if (textarea.value.trim() !== '')
                    sendIcon.style.color = '#0084ff';
                else
                    sendIcon.style.color = colorSendIcon;

                const scrollHeight = textarea.scrollHeight;
                var slectedHeight = null;
                if (scrollHeight > textarea.clientHeight) {
                    if (scrollHeight <= maxHeight)
                        slectedHeight = scrollHeight;
                    else
                        slectedHeight = maxHeight;
                }
                else {
                    const content = textarea.value;
                    if (content == "") {
                        textarea.style.height = textAreaHeight + 'px'; //'36px';
                    }
                    slectedHeight = textarea.clientHeight;
                }
                // Gán height cho các trường
                rowReply.style.maxHeight = (slectedHeight + totalReplyMainPadding + totalPeplyPadding) + 'px';
                replyMain.style.height = (slectedHeight + totalReplyMainPadding) + 'px';
                textarea.style.height = slectedHeight + 'px';

                const maxMessageHeight = conversationHeight - headerHeight - rowReply.clientHeight;
                rowMessage.style.maxHeight = maxMessageHeight + 'px';
                // rowMessage.scrollTop = scrollMessage;
            });

            // Xử lý gửi tin nhắn
            textarea.addEventListener('keydown', function (event) {
                if (event.key === 'Enter' && !event.shiftKey) {
                    event.preventDefault();
                    sendMessage();
                }
            });
            sendButton.addEventListener('click', function () {
                sendMessage()
            });

            function sendMessage() {
                const message = textarea.value.trim();
                if (message !== '') {
                    // Xóa nội dung trong textarea sau khi gửi tin nhắn
                    textarea.value = '';
                    sendIcon.style.color = colorSendIcon;
                    // Đặt lại kích thước div
                    rowReply.style.maxHeight = (textAreaHeight + totalReplyMainPadding + totalPeplyPadding) + 'px';
                    replyMain.style.height = (textAreaHeight + totalReplyMainPadding) + 'px';
                    textarea.style.height = textAreaHeight + 'px';

                    const maxMessageHeight = conversationHeight - headerHeight - rowReply.clientHeight;
                    rowMessage.style.maxHeight = maxMessageHeight + 'px';
                    // Gửi tin nhắn
                    // alert(userOtherId + " " + selectedUserId);
                    socket.emit("client_send_message", { message: message, receiverId: selectedUserId });
                }
            }
        }
        else if (response.status == 401) {
            checkToken(accessToken);
            alert("Bạn không có quyền thực hiện hành động này!");
        } else {
            // Xử lý các trường hợp lỗi khác (không phải 200, 201 hoặc 401)
            alert("Lỗi trong quá trình xử lý: " + response.status);
        }
    } catch (error) {
        // Handle any network or server errors
        alert("Lỗi! Vui lòng thử lại sau");
    }
}

// Hiển thị header user
async function headerMessage(data) {
    // Xử lý nếu avatar null
    const avatar = data.data.avatar ? data.data.avatar : "../Frontend-Chat/assets/image/avatar.jpg";
    // Xử lý icon online
    var status = "online-icon";
    var statusMessage = "Đang hoạt động";
    if (data.data.status == "OFFLINE") {
        status = "";
        statusMessage = formatTime(new Date(data.data.activityTime), 2);
    }
    const headerContainer = document.getElementById("header-conversation");
    const headerDiv = `                                        
        <div class="col-sm-2 col-md-1 col-xs-3 heading-avatar">
            <div class="heading-avatar-icon">
                <img src="${avatar}">
                <span class="${status}"></span>
            </div>
        </div>
        <div class="col-sm-8 col-xs-7 heading-name">
            <a class="heading-name-meta-1">
                ${data.data.fullName}
            </a>

            <span class="heading-online">
                ${statusMessage}
            </span>
        </div>                       
        <div class="col-sm-1 col-xs-1  heading-info pull-right">
            <i class="fa fa-info-circle fa-2x" aria-hidden="true"></i>
        </div> 
        <div class="col-sm-1 col-xs-1  heading-video pull-right">
            <i class="fa fa-video-camera fa-2x" aria-hidden="true"></i>
        </div>  
        <div class="col-sm-1 col-xs-1  heading-phone pull-right">
            <i class="fa fa-phone fa-2x" aria-hidden="true"></i>
        </div>                     
    `;
    // Thêm html vào conversationContainer
    headerContainer.innerHTML = headerDiv;
}

// Xử lý hiện page message
async function bodyMessage(userOtherId, accessToken, pageNum) {
    try {
        const response = await fetch(apiUrls.Message.GET_MESSAGE_WITH_OTHER_ID + `/${userOtherId}?pageNum=${pageNum}`, {
            method: "GET",
            mode: 'cors', // Bật chế độ CORS
            headers: {
                "Content-Type": "application/json",
                "Authorization": `Bearer ${accessToken}`,
            },
        });

        if (response.ok) {
            const data1 = await response.json();
            const messageListData = data1.data.items;
            const messageContainer = document.getElementById("conversation");
            // Xóa hết các phần tử con trong messageContainer
            if (pageNum == 1) {
                messageContainer.innerHTML = "";
            }
            messageListData.forEach(message => {
                // Lấy ra time nhắn
                const createdDateObj = new Date(message.createdDate);
                const formattedTime = formatTime(createdDateObj, 3);

                const messageDiv = document.createElement("div");
                messageDiv.classList.add("row", "message-body");
                if (message.senderId != userOtherId) {
                    const messageContent = `                  
                        <div class="col-sm-12 message-main-sender">
                            <div class="sender">
                                <div class="message-text" title="${formattedTime}">
                                    ${message.message}
                                </div>
                                <span class="message-time pull-right">
                                    ${formattedTime}
                                </span>
                            </div>
                        </div>                
                    `;
                    messageDiv.innerHTML = messageContent;
                    //messageContainer.appendChild(messageDiv);
                    // Thêm div mới vào đầu của messageContainer
                    messageContainer.insertBefore(messageDiv, messageContainer.firstChild);
                }
                else {
                    const messageContent = `                  
                        <div class="col-sm-12 message-main-receiver">
                            <div class="receiver">
                                <div class="message-text">
                                    ${message.message}
                                </div>
                                <span class="message-time pull-right">
                                    ${formattedTime}
                                </span>
                            </div>
                        </div>               
                    `;
                    messageDiv.innerHTML = messageContent;
                    //messageContainer.appendChild(messageDiv);
                    // Thêm div mới vào đầu của messageContainer
                    messageContainer.insertBefore(messageDiv, messageContainer.firstChild);
                }
            });
            // Cuộn xuống dưới cùng nếu lần đầu hiện
            if (pageNum == 1) {
                messageContainer.scrollTop = messageContainer.scrollHeight;
            }

        }
        else if (response.status == 401) {
            checkToken(accessToken);
            alert("Bạn không có quyền thực hiện hành động này!");
        }
        else {
            // Xử lý các trường hợp lỗi khác (không phải 200, 201 hoặc 401)
            alert("Lỗi trong quá trình xử lý: " + response.status);
        }

    } catch (error) {
        // Handle any network or server errors
        alert("Lỗi! Vui lòng thử lại sau");
    }
}

// Hiển thị phần reply
async function replyMessage() {
    const conversationContainer = document.getElementById("conversation-container");
    const replyContent = `                                      
        <div class="col-sm-1 col-xs-1 reply-emojis" title="Chọn biểu tượng cảm xúc">
            <i class="fa fa-smile-o fa-2x"></i>
        </div>

        <div class="col-sm-1 col-xs-1 reply-images" title="Đính kèm file">
            <i class="fa fa-image fa-2x" aria-hidden="true"></i>
        </div>

        <div class="col-sm-1 col-xs-1 reply-recording" title="Gửi clip âm thanh">
            <i class="fa fa-microphone fa-2x" aria-hidden="true"></i>
        </div>

        <div class="col-sm-9 col-xs-9 reply-main">
            <textarea class="form-control" wrap="soft" id="comment" placeholder="Nhập tin nhắn..." required ></textarea>
        </div>
        
        <div class="col-sm-1 col-xs-1 reply-send" title="Gửi">
            <button type="button" id="sendButton" >
                <i class="fa fa-send fa-2x" aria-hidden="true" id="sendIcon"> </i>
            </button>
        </div>   
    `;

    // Kiểm tra xem div reply đã có trong conversationContainer chưa
    var replyDiv = conversationContainer.querySelector("#reply");
    if (!replyDiv) {
        replyDiv = `                  
            <div class="row reply" id="reply">
               ` + replyContent + `
            </div>    
        `;
        // Thêm html mới vào cuối của conversationContainer
        conversationContainer.innerHTML += replyDiv;
    }
    else {
        replyDiv.innerHTML = replyContent;
    }
}

function formatTime(date, type) {
    const currentTime = new Date();
    const timeDiff = currentTime - date; // Khoảng thời gian giữa thời gian hiện tại và thời gian đưa vào hàm (tính bằng milliseconds)

    const oneMinute = 60 * 1000;
    const oneHour = 60 * oneMinute;
    const oneDay = 24 * oneHour; // Số milliseconds trong một ngày
    const oneWeek = 7 * oneDay;
    const oneMonth = 30 * oneDay; // Số milliseconds trong một tháng
    const oneYear = 365 * oneDay; // Số milliseconds trong một năm

    const minute = String(date.getMinutes()).padStart(2, "0");
    const hour = String(date.getHours());//.padStart(2, "0");
    const day = String(date.getDate());//.padStart(2, "0");
    const month = String(date.getMonth() + 1);//.padStart(2, "0");
    const year = date.getFullYear();


    const dayOfWeek = date.getDay();
    const dayOf = ["CN", "Thứ 2", "Thứ 3", "Thứ 4", "Thứ 5", "Thứ 6", "Thứ 7"];
    const dayName = dayOf[dayOfWeek];

    const minutes = Math.floor(timeDiff / oneMinute);
    const hours = Math.floor(timeDiff / oneHour);
    const days = Math.floor(timeDiff / oneDay);
    const months = Math.floor(timeDiff / oneMonth);
    const years = Math.floor(timeDiff / oneYear);
    if (type == 1) {
        if (timeDiff < oneMinute)
            return `1 phút`;
        else
            if (timeDiff < oneHour)
                return `${minutes} phút`;
            else
                if (timeDiff < oneDay)
                    return `${hours} giờ`;
                else
                    if (timeDiff < oneWeek)
                        return `${dayName}`;
                    else
                        if (timeDiff < oneYear)
                            return `${day}/${month}`;
                        else
                            return `${day}/${month}/${year}`;
    }

    if (type == 2) {
        if (timeDiff < oneMinute)
            return `Hoạt động 1 phút trước`;
        else
            if (timeDiff < oneHour)
                return `Hoạt động ${minutes} phút trước`;
            else
                if (timeDiff < oneDay)
                    return `Hoạt động ${hours} giờ trước`;
                else
                    if (timeDiff < oneMonth)
                        return `Hoạt động ${days} ngày trước`;
                    else
                        if (timeDiff < oneYear)
                            return `Hoạt động ${months} tháng trước`;
                        else
                            return `Hoạt động ${years} năm trước`;
    }

    if (type == 3) {
        if (date.getMinutes() >= 0 && date.getHours() >= 0 && date.getDate() == currentTime.getDate()) {
            return `${hour}:${minute}`;
        } else
            if (timeDiff < oneMonth) {
                return `${hour}:${minute}, ${day} Tháng ${month}`;
            } else
                if (timeDiff < oneYear) {
                    return `${hour}:${minute}, ${day} Tháng ${month}, ${year}`;

                } else {
                    return `${hour}:${minute}, ${day} Tháng ${month}, ${year}`;
                }
    }

}