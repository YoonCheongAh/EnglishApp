/* eslint-disable */

const LOGIN_URL = '/login.html';

function requireAdmin() {
  const token = localStorage.getItem('accessToken');
  const role = localStorage.getItem('role');

  if (!token || role !== 'ADMIN') {
    localStorage.clear();
    window.location.href = LOGIN_URL;
    return false;
  }
  return true;
}

// HIỂN THỊ THÔNG TIN TỪ localStorage (HEADER + PROFILE PAGE)
function loadUserInfo() {
  const fullname = localStorage.getItem('fullname') || localStorage.getItem('username') || 'Admin';
  const username = localStorage.getItem('username') || 'admin'; // fallback
  const email = localStorage.getItem('email') || 'Chưa có email';
  const role = localStorage.getItem('role') || 'ADMIN';
  const avatarUrl = localStorage.getItem('avatarUrl') || 'assets/img/avatars/default.jpg';

  // === HEADER (TẤT CẢ TRANG) ===
  const fullnameEl = document.getElementById('profile-fullname');
  const roleEl = document.getElementById('profile-role');
  const avatarEl = document.getElementById('profile-avatar');

  if (fullnameEl) fullnameEl.textContent = fullname;
  if (roleEl) roleEl.textContent = role;
  if (avatarEl) avatarEl.src = avatarUrl;

  // === PROFILE PAGE (HIỂN THỊ THÔNG TIN) ===
  if (window.location.pathname.includes('profile.html')) {
    const displayAvatar = document.getElementById('display-avatar');
    const displayUsername = document.getElementById('display-username');
    const displayFullname = document.getElementById('display-fullname');
    const displayEmail = document.getElementById('display-email');
    const displayRole = document.getElementById('display-role');

    if (displayAvatar) displayAvatar.src = avatarUrl;
    if (displayUsername) displayUsername.textContent = username;
    if (displayFullname) displayFullname.textContent = fullname;
    if (displayEmail) displayEmail.textContent = email;
    if (displayRole) displayRole.textContent = role;
  }
}

function setupLogout() {
  const btn = document.getElementById('btn-logout');
  if (btn) {
    btn.addEventListener('click', (e) => {
      e.preventDefault();
      if (confirm('Bạn có chắc muốn đăng xuất?')) {
        localStorage.clear();
        window.location.href = LOGIN_URL;
      }
    });
  }
}

// CHẠY KHI DOM SẴN SÀNG
document.addEventListener('DOMContentLoaded', () => {
  if (window.location.pathname.includes('login.html')) return;

  if (!requireAdmin()) return;

  loadUserInfo(); // HIỂN THỊ HEADER + PROFILE
  setupLogout();
});