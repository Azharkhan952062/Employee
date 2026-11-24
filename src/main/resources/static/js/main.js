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
    // 2. Left Collapsible Sidebar with Horizontal Three-Dot Toggle (⋯)
    const sidebarToggleBtn = document.getElementById('sidebarToggleBtn');
    const appSidebar = document.getElementById('appSidebar');
    const sidebarOverlay = document.getElementById('sidebarOverlay');
    const sidebarCloseBtn = document.getElementById('sidebarCloseBtn');

    function openSidebar() {
        if (!appSidebar) return;
        appSidebar.classList.add('open');
        if (sidebarOverlay) sidebarOverlay.classList.add('active');
        if (sidebarToggleBtn) {
            sidebarToggleBtn.classList.add('active');
            sidebarToggleBtn.setAttribute('aria-expanded', 'true');
        }
        document.body.classList.add('sidebar-open');
    }

    function closeSidebar() {
        if (!appSidebar) return;
        appSidebar.classList.remove('open');
        if (sidebarOverlay) sidebarOverlay.classList.remove('active');
        if (sidebarToggleBtn) {
            sidebarToggleBtn.classList.remove('active');
            sidebarToggleBtn.setAttribute('aria-expanded', 'false');
        }
        document.body.classList.remove('sidebar-open');
    }

    function toggleSidebar() {
        if (!appSidebar) return;
        if (appSidebar.classList.contains('open')) {
            closeSidebar();
        } else {
            openSidebar();
        }
    }

    if (sidebarToggleBtn && appSidebar) {
        // Toggle on horizontal three-dot button click
        sidebarToggleBtn.addEventListener('click', function (e) {
            e.stopPropagation();
            toggleSidebar();
        });

        // Close button inside sidebar
        if (sidebarCloseBtn) {
            sidebarCloseBtn.addEventListener('click', function (e) {
                e.stopPropagation();
                closeSidebar();
            });
        }

        // Close on clicking the backdrop overlay (e.g. mobile/desktop outside click)
        if (sidebarOverlay) {
            sidebarOverlay.addEventListener('click', function () {
                closeSidebar();
            });
        }

        // Close on clicking anywhere outside when sidebar is open
        document.addEventListener('click', function (e) {
            if (appSidebar.classList.contains('open') &&
                !appSidebar.contains(e.target) &&
                !sidebarToggleBtn.contains(e.target)) {
                closeSidebar();
            }
        });

        // Close when clicking an item inside sidebar
        appSidebar.addEventListener('click', function (e) {
            const item = e.target.closest('.sidebar-nav-item');
            if (item && !item.classList.contains('sidebar-logout-btn')) {
                closeSidebar();
            }
        });

        // Close on Escape key
        document.addEventListener('keydown', function (e) {
            if (e.key === 'Escape' && appSidebar.classList.contains('open')) {
                closeSidebar();
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
            form.action = '/employees/' + encodeURIComponent(employeeId) + '/delete';
            if (nameSpan) {
                nameSpan.textContent = employeeName;
            }

            // Ensure CSRF input is populated from meta tag if not already set
            let csrfInput = form.querySelector('input[name="_csrf"]');
            if (!csrfInput || !csrfInput.value) {
                const metaCsrf = document.querySelector('meta[name="_csrf"]');
                if (metaCsrf && metaCsrf.content) {
                    if (!csrfInput) {
                        csrfInput = document.createElement('input');
                        csrfInput.type = 'hidden';
                        csrfInput.name = '_csrf';
                        form.appendChild(csrfInput);
                    }
                    csrfInput.value = metaCsrf.content;
                }
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
