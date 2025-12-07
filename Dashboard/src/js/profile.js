/* eslint-disable */
document.addEventListener('DOMContentLoaded', () => {
    loadProfileFromStorage();
    setupSaveForm();
});

function loadProfileFromStorage() {
    const username = localStorage.getItem('username') || 'admin';
    const fullname = localStorage.getItem('fullname') || 'Administrator';
    const email = localStorage.getItem('email') || 'admin@example.com';
    const avatarUrl = localStorage.getItem('avatarUrl') || 'assets/img/avatars/default.jpg';
    const role = localStorage.getItem('role') || 'ADMIN';

    if(document.getElementById('nav-fullname')) document.getElementById('nav-fullname').textContent = fullname;
    if(document.getElementById('nav-avatar')) document.getElementById('nav-avatar').src = avatarUrl;

    if(document.getElementById('header-fullname')) document.getElementById('header-fullname').textContent = fullname;
    if(document.getElementById('header-username')) document.getElementById('header-username').textContent = '@' + username;
    if(document.getElementById('header-avatar')) document.getElementById('header-avatar').src = avatarUrl;

    if(document.getElementById('card-username')) document.getElementById('card-username').textContent = username;
    if(document.getElementById('card-fullname')) document.getElementById('card-fullname').textContent = fullname;
    if(document.getElementById('card-email')) document.getElementById('card-email').textContent = email;
    if(document.getElementById('card-avatar')) document.getElementById('card-avatar').textContent = avatarUrl;

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

        const fullname = document.getElementById('input-fullname').value.trim();
        const email = document.getElementById('input-email').value.trim();
        const avatarUrl = document.getElementById('input-avatarUrl').value.trim();

        if (!fullname) {
            msg.textContent = 'Tên không được để trống!';
            msg.className = 'text-danger mt-2';
            return;
        }

        localStorage.setItem('fullname', fullname);
        localStorage.setItem('email', email);
        if (avatarUrl) localStorage.setItem('avatarUrl', avatarUrl);

        msg.textContent = 'Đã lưu thành công!';
        msg.className = 'text-success mt-2';

        loadProfileFromStorage();

        setTimeout(() => {
            msg.textContent = '';
            const modalEl = document.getElementById('editProfileModal');
            const modal = coreui.Modal.getInstance(modalEl);
            if(modal) modal.hide();
        }, 1000);
    });
}