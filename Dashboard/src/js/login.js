/* eslint-disable */
document.addEventListener('DOMContentLoaded', () => {
    const form = document.getElementById('login-form'); // Đảm bảo <form id="login-form">
    const msg = document.getElementById('login-msg');   // Đảm bảo <p id="login-msg">
    
    // Cấu hình đúng port 8080 của Spring Boot
    const API_BASE_URL = 'http://localhost:8080/api/auth';

    function showMsg(text, ok = false) {
        if (!msg) return;
        msg.classList.toggle('text-danger', !ok);
        msg.classList.toggle('text-success', ok);
        msg.textContent = text;
        msg.style.display = 'block';
    }

    if(form) {
        form.addEventListener('submit', async (e) => {
            e.preventDefault();
            showMsg('');
            
            const usernameInput = document.getElementById('username'); // id="username"
            const passwordInput = document.getElementById('password'); // id="password"

            const username = usernameInput.value.trim();
            const password = passwordInput.value; 

            if (!username || !password) {
                return showMsg('Vui lòng nhập đầy đủ username và password.', false);
            }

            // Hiệu ứng loading nút bấm (Optional)
            const btnSubmit = form.querySelector('button[type="submit"]');
            const originalBtnText = btnSubmit.innerText;
            btnSubmit.disabled = true;
            btnSubmit.innerText = "Đang xử lý...";

            try {
                const res = await fetch(`${API_BASE_URL}/login`, {
                    method: 'POST',
                    headers: { 'Content-Type': 'application/json' },
                    body: JSON.stringify({ username, password })
                });
                
                // Xử lý khi đăng nhập thất bại (401, 400, 500)
                if (!res.ok) {
                    const errorData = await res.json().catch(() => ({}));
                    if (res.status === 401 || res.status === 403) {
                        throw new Error('Sai tài khoản hoặc mật khẩu!');
                    }
                    throw new Error(errorData.message || 'Đăng nhập thất bại.');
                }
                
                // Xử lý khi thành công
                const data = await res.json(); 
                
                // QUAN TRỌNG: Kiểm tra cấu trúc JSON trả về từ Backend
                // Backend phải trả về field 'role'. Nếu không có, dòng dưới sẽ sai.
                const userRole = data.role || data.user?.role; 

                if (userRole !== 'ADMIN') {
                    throw new Error('Tài khoản này không có quyền truy cập trang Admin.');
                }
                
                showMsg('Đăng nhập thành công! Đang chuyển hướng...', true);

                // --- LƯU LOCALSTORAGE (Đồng bộ với code Users) ---
                // Dùng key 'jwt_token' để file users.html đọc được
                const token = data.accessToken || data.token;
                localStorage.setItem('jwt_token', token);
                
                // Lưu các thông tin phụ để hiển thị lên Header (nếu cần)
                localStorage.setItem('user_role', userRole);
                localStorage.setItem('user_fullname', data.fullname || username);

                // Chuyển hướng sau 1s
                setTimeout(() => {
                    window.location.href = 'index.html'; // Chuyển về Dashboard chính
                }, 1000);

            } catch (err) {
                console.error('Login error:', err);
                showMsg(err.message || 'Lỗi kết nối Server (8080).', false);
            } finally {
                // Reset nút bấm
                btnSubmit.disabled = false;
                btnSubmit.innerText = originalBtnText;
            }
        });
    }
});