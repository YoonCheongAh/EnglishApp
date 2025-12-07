/* eslint-disable */
document.addEventListener('DOMContentLoaded', () => {
    const flashcardsTableBody = document.getElementById('flashcards-table-body');
    const addButton = document.getElementById('btn-add-flashcard');

    // 1. SỬA ĐÚNG ĐƯỜNG DẪN BACKEND
    // Controller FlashcardController map tại /api/flashcards
    const API_URL = 'http://localhost:8080/api/flashcards';

    // 2. LẤY TOKEN & KIỂM TRA ĐĂNG NHẬP
    const token = localStorage.getItem('jwt_token');
    if (!token) {
        alert("Phiên đăng nhập hết hạn! Vui lòng đăng nhập lại.");
        window.location.href = 'login.html';
        return;
    }

    // 3. HÀM GỌI API CÓ TOKEN (authFetch)
    const authFetch = async (url, options = {}) => {
        const headers = {
            'Content-Type': 'application/json',
            'Authorization': `Bearer ${token}`, // Gắn token vào header
            ...options.headers
        };

        const response = await fetch(url, { ...options, headers });

        if (response.status === 401 || response.status === 403) {
            alert("Bạn không có quyền hoặc phiên đăng nhập đã hết.");
            window.location.href = 'login.html';
            return null;
        }
        return response;
    };

    // ======================
    // 4. LOAD TỪ VỰNG (GET /api/flashcards)
    // ======================
    const loadFlashcards = async () => {
        flashcardsTableBody.innerHTML = '<tr><td colspan="7" class="text-center">Đang tải dữ liệu...</td></tr>';
        
        try {
            const res = await authFetch(API_URL); // Gọi GET gốc
            if (!res) return;

            if (!res.ok) throw new Error(`HTTP ${res.status}`);
            
            const cards = await res.json();
            flashcardsTableBody.innerHTML = '';

            if (!cards || cards.length === 0) {
                flashcardsTableBody.innerHTML = '<tr><td colspan="7" class="text-center">Chưa có từ vựng nào.</td></tr>';
                return;
            }

            cards.forEach(card => {
                const hasAudio = card.audioUrl ? '🔊' : '—'; 
                
                // MAP DỮ LIỆU: Kiểm tra kỹ tên trường trả về từ Backend
                // Backend có thể trả về 'id' hoặc 'flashcardId' tùy DTO
                const id = card.id || card.flashcardId;
                const word = card.word || '';
                const type = card.wordType || '-';
                // Xử lý Topic: Backend có thể trả về object Topic hoặc chỉ tên
                const topicName = (typeof card.topic === 'object') ? card.topic.topicName : (card.topic || '—');
                
                const row = flashcardsTableBody.insertRow();
                row.innerHTML = `
                    <td>${id}</td>
                    <td class="fw-bold text-primary">${word}</td>
                    <td>${type}</td>
                    <td>${topicName}</td> 
                    <td>${card.meaningVn || card.meaningEn || '—'}</td>
                    <td class="text-center">${hasAudio}</td>
                    <td>
                        <button class="btn btn-sm btn-info me-1 text-white" 
                            onclick="triggerEdit(${id})">Sửa</button>
                        <button class="btn btn-sm btn-danger text-white" 
                            onclick="deleteFlashcard(${id})">Xóa</button>
                    </td>
                `;
                // Lưu dữ liệu vào row để nút Sửa lấy lại cho dễ (tránh lỗi quote string)
                row.dataset.card = JSON.stringify(card);
            });
        } catch (err) {
            console.error('❌ Lỗi tải flashcards:', err);
            flashcardsTableBody.innerHTML = '<tr><td colspan="7" class="text-danger text-center">Lỗi kết nối Server (8080)!</td></tr>';
        }
    };

    // ======================
    // 5. THÊM TỪ MỚI (POST /api/flashcards)
    // ======================
    if (addButton) {
        addButton.addEventListener('click', async () => {
            const word = prompt('Nhập từ mới (English):');
            if (!word) return;

            const wordType = prompt('Loại từ (n, v, adj...):');
            const meaningVn = prompt('Nghĩa tiếng Việt:');
            const meaningEn = prompt('Nghĩa tiếng Anh (Optional):');
            // Lưu ý: Topic ở đây nhập ID hay Tên tùy thuộc backend xử lý
            const topicId = prompt('Nhập ID Chủ đề (Số):', '1'); 

            // Payload phải khớp với FlashcardRequest.java
            const payload = { 
                word, 
                wordType, 
                meaningVn, 
                meaningEn, 
                topicId: parseInt(topicId) || null, // Backend thường cần ID để link topic
                phonetic: '',
                imageUrl: '',
                audioUrl: ''
            };

            try {
                // Backend: @PostMapping tại class level -> Không có /add
                const res = await authFetch(API_URL, {
                    method: 'POST',
                    body: JSON.stringify(payload)
                });
                
                if (res && res.ok) {
                     alert('✅ Đã thêm từ mới thành công!');
                     loadFlashcards();
                } else {
                    alert('❌ Lỗi thêm từ (Kiểm tra lại Topic ID)');
                }
            } catch (err) {
                console.error(err);
                alert('Lỗi kết nối server');
            }
        });
    }

    // ======================
    // 6. XỬ LÝ SỰ KIỆN SỬA (PUT /api/flashcards/{id})
    // ======================
    // Hàm trung gian để lấy data từ dataset
    window.triggerEdit = (id) => {
        // Tìm dòng chứa nút bấm để lấy data gốc
        const rows = Array.from(flashcardsTableBody.rows);
        const row = rows.find(r => r.innerHTML.includes(`triggerEdit(${id})`));
        if (!row) return;

        const card = JSON.parse(row.dataset.card);
        editFlashcard(id, card);
    }

    window.editFlashcard = async (id, card) => {
        const newWord = prompt('Sửa từ vựng:', card.word);
        if (!newWord) return; // Cancel
        
        const newMeaningVn = prompt('Sửa nghĩa tiếng Việt:', card.meaningVn);
        
        // Payload cập nhật
        const payload = {
            ...card, // Giữ lại các trường cũ (ảnh, audio...)
            word: newWord,
            meaningVn: newMeaningVn,
            // Xử lý topic khi gửi lên (nếu backend cần topicId)
            topicId: (card.topic && card.topic.id) ? card.topic.id : null 
        };

        try {
            const res = await authFetch(`${API_URL}/${id}`, {
                method: 'PUT',
                body: JSON.stringify(payload)
            });

            if (res && res.ok) {
                alert('✅ Cập nhật thành công!');
                loadFlashcards();
            } else {
                alert('❌ Lỗi cập nhật.');
            }
        } catch (err) {
            console.error(err);
        }
    };

    // ======================
    // 7. XÓA TỪ VỰNG (DELETE /api/flashcards/{id})
    // ======================
    window.deleteFlashcard = async (id) => {
        if (!confirm(`Xóa từ ID ${id}?`)) return;
        try {
            const res = await authFetch(`${API_URL}/${id}`, { method: 'DELETE' });
            
            if (res && res.ok) {
                 alert('✅ Đã xóa từ thành công!');
                 loadFlashcards(); 
            } else {
                alert('❌ Không thể xóa (Có thể do lỗi server hoặc quyền hạn).');
            }
        } catch (err) {
            console.error(err);
            alert('Lỗi kết nối.');
        }
    };

    // Khởi động
    loadFlashcards();
});