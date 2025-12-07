/* eslint-disable */
document.addEventListener('DOMContentLoaded', () => {
    loadProfileFromStorage();
    setupSaveForm();
});

function loadProfileFromStorage() {
    // 1. Lấy dữ liệu từ LocalStorage (hoặc set mặc định)
    const username = localStorage.getItem('username') || 'admin';
    const fullname = localStorage.getItem('fullname') || 'Administrator';
    const email = localStorage.getItem('email') || 'admin@example.com';
    const avatarUrl = localStorage.getItem('avatarUrl') || 'assets/img/avatars/default.jpg';
    const role = localStorage.getItem('role') || 'ADMIN';

    // 2. Hiển thị lên giao diện (VIEW Mode - HTML Text)
    // Header nhỏ góc phải
    if(document.getElementById('nav-fullname')) document.getElementById('nav-fullname').textContent = fullname;
    if(document.getElementById('nav-avatar')) document.getElementById('nav-avatar').src = avatarUrl;

    // Profile Header to đùng
    if(document.getElementById('header-fullname')) document.getElementById('header-fullname').textContent = fullname;
    if(document.getElementById('header-username')) document.getElementById('header-username').textContent = '@' + username;
    if(document.getElementById('header-avatar')) document.getElementById('header-avatar').src = avatarUrl;

    // Info Card
    if(document.getElementById('card-username')) document.getElementById('card-username').textContent = username;
    if(document.getElementById('card-fullname')) document.getElementById('card-fullname').textContent = fullname;
    if(document.getElementById('card-email')) document.getElementById('card-email').textContent = email;
    if(document.getElementById('card-avatar')) document.getElementById('card-avatar').textContent = avatarUrl;

    // 3. Điền vào Modal (EDIT Mode - Input Value)
    if(document.getElementById('input-fullname')) document.getElementById('input-fullname').value = fullname;
    if(document.getElementById('input-email')) document.getElementById('input-email').value = email;
    if(document.getElementById('input-avatarUrl')) {
        document.getElementById('input-avatarUrl').value = avatarUrl;
        document.getElementById('preview-avatar').src = avatarUrl;
    }
}

function setupSaveForm() {
    const form = document.getElementById('profile-form');
    const msg = document.getElementById('profile-msg');

    if (!form) return;

    form.addEventListener('submit', (e) => {
        e.preventDefault();

        // 1. Lấy giá trị từ Input trong Modal
        const fullname = document.getElementById('input-fullname').value.trim();
        const email = document.getElementById('input-email').value.trim();
        const avatarUrl = document.getElementById('input-avatarUrl').value.trim();

        if (!fullname) {
            msg.textContent = 'Tên không được để trống!';
            msg.className = 'text-danger mt-2';
            return;
        }

        // 2. Lưu vào LocalStorage
        localStorage.setItem('fullname', fullname);
        localStorage.setItem('email', email);
        if (avatarUrl) localStorage.setItem('avatarUrl', avatarUrl);

        // 3. Thông báo
        msg.textContent = 'Đã lưu thành công!';
        msg.className = 'text-success mt-2';

        // 4. Reload lại dữ liệu lên giao diện ngay lập tức
        loadProfileFromStorage();

        // 5. Tự động đóng modal sau 1 giây
        setTimeout(() => {
            msg.textContent = '';
            // Dùng CoreUI API để đóng modal (nếu có load thư viện)
            const modalEl = document.getElementById('editProfileModal');
            const modal = coreui.Modal.getInstance(modalEl);
            if(modal) modal.hide();
        }, 1000);
    });
}