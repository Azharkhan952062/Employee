/**
 * Employee Management System - JavaScript Interactions
 */

// 1. Password Visibility Toggle (Available immediately)
window.togglePasswordVisibility = function (inputId, btn) {
    const input = document.getElementById(inputId);
    if (!input || !btn) return;

    const eyeOpen = btn.querySelector('.eye-open');
    const eyeClosed = btn.querySelector('.eye-closed');

    if (input.type === 'password') {
        input.type = 'text';
        if (eyeOpen) eyeOpen.style.display = 'none';
        if (eyeClosed) eyeClosed.style.display = 'block';
    } else {
        input.type = 'password';
        if (eyeOpen) eyeOpen.style.display = 'block';
        if (eyeClosed) eyeClosed.style.display = 'none';
    }
};

document.addEventListener('DOMContentLoaded', function () {
    // 2. Three-Dot Kebab Navigation Menu
    const kebabBtn = document.getElementById('kebabBtn');
    const kebabDropdown = document.getElementById('kebabDropdown');
    const kebabWrapper = document.getElementById('kebabWrapper');

    if (kebabBtn && kebabDropdown) {
        // Toggle on click
        kebabBtn.addEventListener('click', function (e) {
            e.stopPropagation();
            const isOpen = kebabDropdown.classList.contains('open');
            if (isOpen) {
                closeKebabMenu();
            } else {
                openKebabMenu();
            }
        });

        function openKebabMenu() {
            kebabDropdown.classList.add('open');
            kebabBtn.classList.add('active');
            kebabBtn.setAttribute('aria-expanded', 'true');
        }

        function closeKebabMenu() {
            kebabDropdown.classList.remove('open');
            kebabBtn.classList.remove('active');
            kebabBtn.setAttribute('aria-expanded', 'false');
        }

        // Close on clicking anywhere outside
        document.addEventListener('click', function (e) {
            if (kebabWrapper && !kebabWrapper.contains(e.target)) {
                closeKebabMenu();
            }
        });

        // Close when clicking an item inside dropdown
        kebabDropdown.addEventListener('click', function (e) {
            const item = e.target.closest('.dropdown-item');
            if (item) {
                closeKebabMenu();
            }
        });

        // Close on Escape key
        document.addEventListener('keydown', function (e) {
            if (e.key === 'Escape') {
                closeKebabMenu();
            }
        });
    }

    // 3. Auto-dismiss Flash Alerts
    const alerts = document.querySelectorAll('.alert');
    alerts.forEach(function (alert) {
        setTimeout(function () {
            alert.style.transition = 'opacity 0.4s ease, transform 0.4s ease';
            alert.style.opacity = '0';
            alert.style.transform = 'translateY(-6px)';
            setTimeout(() => alert.remove(), 400);
        }, 5000);

        const closeBtn = alert.querySelector('.alert-close');
        if (closeBtn) {
            closeBtn.addEventListener('click', function () {
                alert.remove();
            });
        }
    });

    // 4. Delete Confirmation Modal
    window.openDeleteModal = function (employeeId, employeeName) {
        const modal = document.getElementById('deleteModal');
        const form = document.getElementById('deleteForm');
        const nameSpan = document.getElementById('deleteEmployeeName');

        if (modal && form) {
            form.action = '/employees/' + employeeId + '/delete';
            if (nameSpan) {
                nameSpan.textContent = employeeName;
            }
            modal.classList.add('open');
        }
    };

    window.openDeleteModalFromBtn = function (btn) {
        if (!btn) return;
        const id = btn.getAttribute('data-id');
        const name = btn.getAttribute('data-name');
        window.openDeleteModal(id, name);
    };

    window.closeDeleteModal = function () {
        const modal = document.getElementById('deleteModal');
        if (modal) {
            modal.classList.remove('open');
        }
    };

    // Close modal on Escape key
    document.addEventListener('keydown', function (e) {
        if (e.key === 'Escape') {
            window.closeDeleteModal();
        }
    });
});
