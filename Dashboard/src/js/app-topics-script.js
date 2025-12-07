/* eslint-disable */

document.addEventListener('DOMContentLoaded', () => {
    const topicsTableBody = document.getElementById('topics-table-body');
    const API_URL = 'http://localhost:8080/api/topics'; // Chạy Port 8080

    // 1. LẤY TOKEN & CHECK LOGIN
    const token = localStorage.getItem('jwt_token');
    if (!token) {
        alert("Phiên đăng nhập hết hạn! Vui lòng đăng nhập lại.");
        window.location.href = 'login.html';
        return;
    }

    // 2. HÀM GỌI API (AUTH FETCH)
    const authFetch = async (url, options = {}) => {
        const headers = {
            'Content-Type': 'application/json',
            'Authorization': `Bearer ${token}`,
            ...options.headers
        };
        const response = await fetch(url, { ...options, headers });
        if (response.status === 401 || response.status === 403) {
            alert("Bạn không có quyền hoặc hết phiên đăng nhập.");
            window.location.href = 'login.html';
            return null;
        }
        return response;
    };

    // ===========================
    // 3. LOAD DANH SÁCH CHỦ ĐỀ
    // ===========================
    const loadTopics = async () => {
        topicsTableBody.innerHTML = '<tr><td colspan="5" class="text-center">Đang tải dữ liệu...</td></tr>';
        
        try {
            const res = await authFetch(API_URL);
            if (!res) return;
            if (!res.ok) throw new Error(`HTTP ${res.status}`);

            const topics = await res.json();
            topicsTableBody.innerHTML = '';

            if (!topics || topics.length === 0) {
                topicsTableBody.innerHTML = '<tr><td colspan="5" class="text-center">Chưa có chủ đề nào.</td></tr>';
                return;
            }

            topics.forEach(topic => {
                // Map dữ liệu (Backend trả về camelCase)
                const id = topic.id || topic.topicId;
                const name = topic.name || topic.topicName;
                const desc = topic.description || '';
                
                // Xử lý ngày tháng (Nếu backend trả về createdAt)
                let dateStr = '—';
                if (topic.createdAt) {
                    dateStr = new Date(topic.createdAt).toLocaleDateString('vi-VN');
                }

                // Escape chuỗi để tránh lỗi nút Sửa
                const safeName = name ? name.replace(/'/g, "\\'") : '';
                const safeDesc = desc ? desc.replace(/'/g, "\\'").replace(/"/g, "&quot;") : '';

                const row = topicsTableBody.insertRow();
                row.innerHTML = `
                    <td>${id}</td>
                    <td class="fw-bold text-primary">${name}</td>
                    <td>${desc}</td>
                    <td>${dateStr}</td>
                    <td>
                        <button class="btn btn-sm btn-info text-white me-2" 
                            onclick="editTopic(${id}, '${safeName}', '${safeDesc}')">Sửa</button>
                        <button class="btn btn-sm btn-danger text-white" 
                            onclick="deleteTopic(${id})">Xóa</button>
                    </td>
                `;
            });

        } catch (error) {
            console.error("Lỗi:", error);
            topicsTableBody.innerHTML = '<tr><td colspan="5" class="text-danger text-center">Lỗi kết nối Server (8080)!</td></tr>';
        }
    };

    // ===========================
    // 4. THÊM CHỦ ĐỀ MỚI
    // ===========================
    const addForm = document.getElementById('add-topic-form');
    if (addForm) {
        addForm.addEventListener('submit', async (e) => {
            e.preventDefault();
            
            // Lấy dữ liệu từ input (name="name" và name="description")
            const name = addForm.name.value; 
            const description = addForm.description.value;

            // Payload: Gửi cả 2 key để chắc chắn khớp với DTO Backend
            const payload = { 
                topicName: name, 
                name: name,
                description: description 
            };

            try {
                // POST /api/topics
                const res = await authFetch(API_URL, {
                    method: 'POST',
                    body: JSON.stringify(payload)
                });

                if (res && res.ok) {
                    alert('✅ Thêm thành công!');
                    
                    // Đóng modal bằng CoreUI
                    const modalElement = document.getElementById('addTopicModal');
                    const modal = coreui.Modal.getInstance(modalElement);
                    modal.hide();
                    
                    addForm.reset();
                    loadTopics();
                } else {
                    const errData = await res.json().catch(() => ({}));
                    alert(`❌ Lỗi: ${errData.message || 'Không thể thêm chủ đề.'}`);
                }
            } catch (err) { console.error(err); }
        });
    }

    // ===========================
    // 5. SỬA CHỦ ĐỀ
    // ===========================
    window.editTopic = async (id, currentName, currentDesc) => {
        const newName = prompt("Sửa tên chủ đề:", currentName);
        if (newName === null) return; // Hủy bỏ
        
        const newDesc = prompt("Sửa mô tả:", currentDesc);

        const payload = {
            topicName: newName,
            name: newName,
            description: newDesc || ""
        };

        try {
            const res = await authFetch(`${API_URL}/${id}`, {
                method: 'PUT',
                body: JSON.stringify(payload)
            });

            if (res && res.ok) {
                alert('✅ Cập nhật thành công!');
                loadTopics();
            } else {
                alert('❌ Lỗi cập nhật.');
            }
        } catch (err) { console.error(err); }
    };

    // ===========================
    // 6. XÓA CHỦ ĐỀ
    // ===========================
    window.deleteTopic = async (id) => {
        if (!confirm(`Bạn có chắc muốn xóa chủ đề ID ${id}?`)) return;

        try {
            const res = await authFetch(`${API_URL}/${id}`, { method: 'DELETE' });
            if (res && res.ok) {
                alert('✅ Xóa thành công!');
                loadTopics();
            } else {
                alert('❌ Không thể xóa (Có thể đang chứa từ vựng).');
            }
        } catch (err) { console.error(err); }
    };

    // Khởi chạy
    loadTopics();
});