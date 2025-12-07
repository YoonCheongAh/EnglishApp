/* eslint-disable */

document.addEventListener('DOMContentLoaded', () => {
    // ==========================================
    // 1. FAKE SỐ LIỆU THỐNG KÊ (STAT CARDS)
    // ==========================================
    const stats = {
        users: 12580,
        flashcards: 4500,
        quizzes: 890,
        rate: "85.4%"
    };

    // Hàm chạy số nhảy nhảy cho đẹp (Animation count up)
    const animateValue = (id, start, end, duration) => {
        const obj = document.getElementById(id);
        if (!obj) return;
        let startTimestamp = null;
        const step = (timestamp) => {
            if (!startTimestamp) startTimestamp = timestamp;
            const progress = Math.min((timestamp - startTimestamp) / duration, 1);
            // Nếu là phần trăm thì không làm tròn số nguyên
            if (typeof end === 'string') {
                obj.innerHTML = end;
            } else {
                obj.innerHTML = Math.floor(progress * (end - start) + start).toLocaleString('vi-VN');
            }
            if (progress < 1) {
                window.requestAnimationFrame(step);
            }
        };
        window.requestAnimationFrame(step);
    };

    animateValue("total-users", 0, stats.users, 1500);
    animateValue("total-flashcards", 0, stats.flashcards, 1500);
    animateValue("total-quizzes", 0, stats.quizzes, 1500);
    document.getElementById("avg-correct-rate").innerText = stats.rate;

    // ==========================================
    // 2. FAKE BIỂU ĐỒ (CHARTS.JS)
    // ==========================================
    
    // Cấu hình chung cho Chart nhỏ
    const commonOptions = {
        plugins: { legend: { display: false } },
        maintainAspectRatio: false,
        scales: {
            x: { display: false },
            y: { display: false }
        },
        elements: {
            line: { borderWidth: 2, tension: 0.4 },
            point: { radius: 0, hitRadius: 10, hoverRadius: 4 }
        }
    };

    // --- Chart 1: Users (Màu xanh dương) ---
    const ctxUsers = document.getElementById('card-chart-users');
    if (ctxUsers) {
        new Chart(ctxUsers, {
            type: 'line',
            data: {
                labels: ['T1', 'T2', 'T3', 'T4', 'T5', 'T6', 'T7'],
                datasets: [{
                    label: 'Users',
                    backgroundColor: 'transparent',
                    borderColor: 'rgba(255,255,255,.55)',
                    data: [65, 59, 84, 84, 51, 55, 40]
                }]
            },
            options: commonOptions
        });
    }

    // --- Chart 2: Flashcards (Màu xanh nhạt - Info) ---
    const ctxFlashcards = document.getElementById('card-chart-flashcards');
    if (ctxFlashcards) {
        new Chart(ctxFlashcards, {
            type: 'line',
            data: {
                labels: ['T1', 'T2', 'T3', 'T4', 'T5', 'T6', 'T7'],
                datasets: [{
                    label: 'Flashcards',
                    backgroundColor: 'transparent',
                    borderColor: 'rgba(255,255,255,.55)',
                    data: [1, 18, 9, 17, 34, 22, 11]
                }]
            },
            options: commonOptions
        });
    }

    // --- Chart 3: Quizzes (Màu vàng - Warning) ---
    const ctxQuizzes = document.getElementById('card-chart-quizzes');
    if (ctxQuizzes) {
        new Chart(ctxQuizzes, {
            type: 'line',
            data: {
                labels: ['T1', 'T2', 'T3', 'T4', 'T5', 'T6', 'T7'],
                datasets: [{
                    label: 'Quizzes',
                    backgroundColor: 'rgba(255,255,255,.2)',
                    borderColor: 'rgba(255,255,255,.55)',
                    data: [78, 81, 80, 45, 34, 12, 40],
                    fill: true
                }]
            },
            options: commonOptions
        });
    }

    // --- Chart 4: Rate (Màu đỏ - Danger) ---
    const ctxRate = document.getElementById('card-chart-rate');
    if (ctxRate) {
        new Chart(ctxRate, {
            type: 'bar',
            data: {
                labels: ['T1', 'T2', 'T3', 'T4', 'T5', 'T6', 'T7', 'T8', 'T9', 'T10', 'T11', 'T12', 'T13', 'T14', 'T15', 'T16'],
                datasets: [{
                    label: 'Rate',
                    backgroundColor: 'rgba(255,255,255,.2)',
                    borderColor: 'rgba(255,255,255,.55)',
                    data: [78, 81, 80, 45, 34, 12, 40, 75, 34, 89, 32, 68, 54, 72, 18, 98],
                    barPercentage: 0.6
                }]
            },
            options: commonOptions
        });
    }

    // --- MAIN CHART: Hoạt động người dùng (Chart to đùng ở giữa) ---
    const ctxMain = document.getElementById('main-activity-chart');
    if (ctxMain) {
        new Chart(ctxMain, {
            type: 'line',
            data: {
                labels: ['Tháng 1', 'Tháng 2', 'Tháng 3', 'Tháng 4', 'Tháng 5', 'Tháng 6', 'Tháng 7'],
                datasets: [
                    {
                        label: 'Lượt học từ vựng',
                        backgroundColor: 'rgba(220, 220, 220, 0.2)',
                        borderColor: 'rgba(220, 220, 220, 1)',
                        pointBackgroundColor: 'rgba(220, 220, 220, 1)',
                        pointBorderColor: '#fff',
                        data: [150, 200, 180, 250, 300, 280, 350],
                        tension: 0.4,
                        fill: true
                    },
                    {
                        label: 'Lượt làm Quiz',
                        backgroundColor: 'rgba(151, 187, 205, 0.2)',
                        borderColor: 'rgba(151, 187, 205, 1)',
                        pointBackgroundColor: 'rgba(151, 187, 205, 1)',
                        pointBorderColor: '#fff',
                        data: [80, 120, 140, 100, 150, 170, 200],
                        tension: 0.4,
                        fill: true
                    }
                ]
            },
            options: {
                maintainAspectRatio: false,
                plugins: {
                    legend: { display: true }
                },
                scales: {
                    x: { grid: { drawOnChartArea: false } },
                    y: { ticks: { beginAtZero: true, maxTicksLimit: 5 } }
                }
            }
        });
    }

    // ==========================================
    // 3. FAKE DANH SÁCH NGƯỜI DÙNG MỚI (TABLE)
    // ==========================================
    const fakeUsers = [
        { id: 1001, name: "Nguyễn Văn An", email: "an.nguyen@example.com", progress: 85, date: "2023-10-25" },
        { id: 1002, name: "Trần Thị Bích", email: "bich.tran@example.com", progress: 40, date: "2023-10-24" },
        { id: 1003, name: "Lê Hoàng Cường", email: "cuong.le@example.com", progress: 92, date: "2023-10-24" },
        { id: 1004, name: "Phạm Minh Duy", email: "duy.pham@example.com", progress: 15, date: "2023-10-23" },
        { id: 1005, name: "Hoàng Thùy Linh", email: "linh.hoang@example.com", progress: 60, date: "2023-10-22" },
        { id: 1006, name: "Đặng Văn Hậu", email: "hau.dang@example.com", progress: 75, date: "2023-10-20" }
    ];

    const tableBody = document.getElementById('latest-users-body');
    if (tableBody) {
        tableBody.innerHTML = ''; // Xóa dòng "Đang tải..."
        
        fakeUsers.forEach(user => {
            // Logic màu thanh tiến độ
            let colorClass = 'bg-success';
            if (user.progress < 30) colorClass = 'bg-danger';
            else if (user.progress < 70) colorClass = 'bg-warning';

            // Dùng dịch vụ UI Avatars để tạo ảnh đại diện giả theo tên
            const avatarUrl = `https://ui-avatars.com/api/?name=${encodeURIComponent(user.name)}&background=random&color=fff`;

            const row = `
                <tr class="align-middle">
                    <td>
                        <div class="fw-semibold">#${user.id}</div>
                    </td>
                    <td>
                        <div class="d-flex align-items-center">
                            <img class="avatar avatar-md rounded-circle me-3" src="${avatarUrl}" alt="${user.name}" width="40" height="40">
                            <div>
                                <div class="fw-semibold">${user.name}</div>
                                <div class="small text-body-secondary">Registered: ${user.date}</div>
                            </div>
                        </div>
                    </td>
                    <td>
                        <div class="clearfix">
                            <div class="float-start">
                                <div class="fw-semibold">${user.progress}%</div>
                            </div>
                        </div>
                        <div class="progress progress-thin">
                            <div class="progress-bar ${colorClass}" role="progressbar" style="width: ${user.progress}%" aria-valuenow="${user.progress}" aria-valuemin="0" aria-valuemax="100"></div>
                        </div>
                    </td>
                    <td class="text-center">
                         <div class="fw-semibold text-body-secondary">${user.email}</div>
                    </td>
                    <td>
                        <div class="small text-body-secondary">Vừa truy cập 5 phút trước</div>
                    </td>
                    <td>
                        <div class="dropdown">
                            <button class="btn btn-transparent p-0" type="button" data-coreui-toggle="dropdown" aria-haspopup="true" aria-expanded="false">
                                <svg class="icon"><use xlink:href="node_modules/@coreui/icons/sprites/free.svg#cil-options"></use></svg>
                            </button>
                            <div class="dropdown-menu dropdown-menu-end">
                                <a class="dropdown-item" href="#">Xem chi tiết</a>
                                <a class="dropdown-item" href="#">Sửa</a>
                                <a class="dropdown-item text-danger" href="#">Xóa</a>
                            </div>
                        </div>
                    </td>
                </tr>
            `;
            tableBody.innerHTML += row;
        });
    }
});