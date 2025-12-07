/* eslint-disable */

document.addEventListener('DOMContentLoaded', () => {
    const usersTableBody = document.getElementById('users-table-body');
    
    // 1. Cấu hình đúng đường dẫn API của Backend Spring Boot
    // Controller AdminUserController map tại: /api/admin/users
    const API_URL = 'http://localhost:8080/api/admin/users'; 

    // Lấy Token từ lúc đăng nhập (giả sử bạn lưu key là 'jwt_token')
    const token = localStorage.getItem('jwt_token');

    // Kiểm tra nếu chưa đăng nhập
    if (!token) {
        alert("Bạn chưa đăng nhập! Vui lòng đăng nhập quyền Admin.");
        window.location.href = 'login.html'; // Chuyển hướng về trang login
        return;
    }

    // Hàm hỗ trợ gọi API có kèm Token (Auth)
    const authFetch = async (url, options = {}) => {
        // Mặc định thêm Header Authorization
        const headers = {
            'Content-Type': 'application/json',
            'Authorization': `Bearer ${token}`,
            ...options.headers
        };

        const response = await fetch(url, { ...options, headers });

        if (response.status === 401 || response.status === 403) {
            alert("Phiên đăng nhập hết hạn hoặc không có quyền Admin!");
            localStorage.removeItem('jwt_token');
            window.location.href = 'login.html';
            return null;
        }
        return response;
    };

    const formatLastActive = (timestamp) => {
        if (!timestamp) return 'Chưa đăng nhập';
        return new Date(timestamp).toLocaleDateString('vi-VN', {
            hour: '2-digit', minute: '2-digit', day: '2-digit', month: '2-digit', year: 'numeric'
        });
    };

    // ======================
    // 1. Load danh sách User
    // ======================
    const loadUsers = async () => {
        usersTableBody.innerHTML = '<tr><td colspan="7" class="text-center">Đang tải dữ liệu...</td></tr>';
        
        try {
            // Gọi API GET /api/admin/users
            const response = await authFetch(API_URL);
            if (!response) return; // Lỗi auth

            if (!response.ok) throw new Error(`Lỗi HTTP: ${response.status}`);
            
            const users = await response.json(); 
            usersTableBody.innerHTML = ''; 

            if (users.length === 0) {
                usersTableBody.innerHTML = '<tr><td colspan="7" class="text-center">Chưa có người dùng nào.</td></tr>';
                return;
            }

            users.forEach(user => {
                // CHÚ Ý: Map đúng tên trường trả về từ Java (camelCase)
                // Java: id, fullName, email, role, ...
                const userId = user.userId; 
                const fullName = user.fullname; // Fallback nếu fullName null
                const email = user.email;
                const role = user.role; // Backend trả về String hoặc Object
                
                // Giả lập dữ liệu chưa có trong DTO trả về (để không bị lỗi hiển thị)
                const learnedWords = user.learnedWords || 0; 
                const lastLogin = user.lastLogin || null;

                const roleBadge = role === 'ADMIN' 
                    ? '<span class="badge bg-danger">ADMIN</span>' 
                    : '<span class="badge bg-info">USER</span>';

                const row = usersTableBody.insertRow();
                row.innerHTML = `
                    <td>${userId}</td>
                    <td>${fullName}</td>
                    <td>${email}</td>
                    <td class="text-center">${roleBadge}</td>
                    <td class="text-center">${learnedWords} từ</td>
                    <td class="text-center">${formatLastActive(lastLogin)}</td>
                    <td>
                        <button class="btn btn-sm btn-info me-1 text-white" onclick="editUser(${userId})">Sửa</button>
                        <button class="btn btn-sm btn-danger text-white" onclick="deleteUser(${userId})">Xóa</button>
                    </td>
                `;
            });
        } catch (error) {
            console.error("Lỗi:", error);
            usersTableBody.innerHTML = '<tr><td colspan="7" class="text-danger text-center">Không thể tải dữ liệu. Hãy kiểm tra Backend (Port 8080) và CORS.</td></tr>';
        }
    };

    // ======================
    // 2. Thêm User Mới
    // ======================
    const btnAdd = document.getElementById('btn-add-user');
    if (btnAdd) {
        btnAdd.addEventListener('click', async () => {
            // Demo dùng prompt cho nhanh, thực tế nên dùng Modal
            const fullName = prompt('Nhập tên đầy đủ:');
            if (!fullName) return;
            const email = prompt('Nhập email:');
            const password = prompt('Nhập mật khẩu:', '123456');
            const role = prompt('Nhập role (ADMIN/USER):', 'USER');

            // DTO gửi lên phải khớp với CreateUserRequest bên Java
            const payload = {
                fullName: fullName,
                email: email,
                password: password,
                role: role,
                username: email.split('@')[0] // Tạo tạm username từ email
            };

            try {
                // Backend: @PostMapping tại /api/admin/users (không có /add)
                const res = await authFetch(API_URL, {
                    method: 'POST',
                    body: JSON.stringify(payload)
                });

                if (res && res.ok) {
                    alert('✅ Đã thêm User thành công!');
                    loadUsers();
                } else {
                    alert('❌ Lỗi thêm User (Kiểm tra lại dữ liệu)');
                }
            } catch (err) {
                console.error(err);
                alert('Lỗi kết nối Server');
            }
        });
    }

    // ======================
    // 3. Xóa User
    // ======================
    window.deleteUser = async (id) => {
        if (!confirm(`Bạn có chắc muốn xóa User ID: ${id}?`)) return;

        try {
            // Backend: @DeleteMapping("/{id}")
            const res = await authFetch(`${API_URL}/${id}`, {
                method: 'DELETE'
            });

            if (res && res.ok) {
                alert('✅ Đã xóa thành công!');
                loadUsers();
            } else {
                alert('❌ Không thể xóa User này.');
            }
        } catch (err) {
            console.error(err);
            alert('Lỗi kết nối Server');
        }
    };

    // ======================
    // 4. Sửa User (Demo Update Role)
    // ======================
    window.editUser = async (id) => {
        const newRole = prompt("Nhập Role mới (ADMIN/USER):");
        if(!newRole) return;

        // DTO khớp với UpdateUserRequest
        const payload = {
            role: newRole
            // Thêm các trường khác nếu Backend yêu cầu
        };

        try {
             // Backend: @PutMapping("/{id}")
            const res = await authFetch(`${API_URL}/${id}`, {
                method: 'PUT',
                body: JSON.stringify(payload)
            });

            if (res && res.ok) {
                alert('✅ Cập nhật thành công!');
                loadUsers();
            } else {
                alert('❌ Lỗi cập nhật.');
            }
        } catch (err) {
            console.error(err);
        }
    };

    // Chạy hàm load khi mở trang
    loadUsers();
});