/* eslint-disable */
document.addEventListener('DOMContentLoaded', () => {
    const flashcardsTableBody = document.getElementById('flashcards-table-body');
    const addButton = document.getElementById('btn-add-flashcard');


    const API_URL = 'http://localhost:8080/api/flashcards';
    const token = localStorage.getItem('jwt_token');
    if (!token) {
        alert("Phiên đăng nhập hết hạn! Vui lòng đăng nhập lại.");
        window.location.href = 'login.html';
        return;
    }
    const authFetch = async (url, options = {}) => {
        const headers = {
            'Content-Type': 'application/json',
            'Authorization': `Bearer ${token}`, 
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

    const loadFlashcards = async () => {
        flashcardsTableBody.innerHTML = '<tr><td colspan="7" class="text-center">Đang tải dữ liệu...</td></tr>';
        
        try {
            const res = await authFetch(API_URL); 
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
                const id = card.id || card.flashcardId;
                const word = card.word || '';
                const type = card.wordType || '-';
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
                row.dataset.card = JSON.stringify(card);
            });
        } catch (err) {
            console.error('❌ Lỗi tải flashcards:', err);
            flashcardsTableBody.innerHTML = '<tr><td colspan="7" class="text-danger text-center">Lỗi kết nối Server (8080)!</td></tr>';
        }
    };

    if (addButton) {
        addButton.addEventListener('click', async () => {
            const word = prompt('Nhập từ mới (English):');
            if (!word) return;

            const wordType = prompt('Loại từ (n, v, adj...):');
            const meaningVn = prompt('Nghĩa tiếng Việt:');
            const meaningEn = prompt('Nghĩa tiếng Anh (Optional):');
            const topicId = prompt('Nhập ID Chủ đề (Số):', '1'); 
            const payload = { 
                word, 
                wordType, 
                meaningVn, 
                meaningEn, 
                topicId: parseInt(topicId) || null,
                phonetic: '',
                imageUrl: '',
                audioUrl: ''
            };

            try {
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
    window.triggerEdit = (id) => {
        const rows = Array.from(flashcardsTableBody.rows);
        const row = rows.find(r => r.innerHTML.includes(`triggerEdit(${id})`));
        if (!row) return;

        const card = JSON.parse(row.dataset.card);
        editFlashcard(id, card);
    }

    window.editFlashcard = async (id, card) => {
        const newWord = prompt('Sửa từ vựng:', card.word);
        if (!newWord) return;
        
        const newMeaningVn = prompt('Sửa nghĩa tiếng Việt:', card.meaningVn);
        
        const payload = {
            ...card, 
            word: newWord,
            meaningVn: newMeaningVn,
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

    loadFlashcards();
});