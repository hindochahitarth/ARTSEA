// Mobile View Scipt

// Mobile Menu Toggle
const hamburger = document.getElementById('hamburger');
const overlayMenu = document.getElementById('overlay-menu');
const overlayClose = document.getElementById('overlay-close');

hamburger.addEventListener('click', () => {
    overlayMenu.classList.add('active');
    document.body.style.overflow = 'hidden';
});

overlayClose.addEventListener('click', () => {
    overlayMenu.classList.remove('active');
    document.body.style.overflow = '';
});

// Close overlay on outside click
overlayMenu.addEventListener('click', (e) => {
    if (e.target === overlayMenu) {
        overlayMenu.classList.remove('active');
        document.body.style.overflow = '';
    }
});

// Dropdown functionality for mobile menu
document.querySelectorAll('.overlay-link').forEach(link => {
    link.addEventListener('click', function () {
        const submenuId = this.getAttribute('data-submenu');
        const submenu = document.getElementById(submenuId);
        const plusIcon = this.querySelector('.plus');

        this.classList.toggle('active');

        if (submenu.style.display === 'flex') {
            submenu.style.display = 'none';
        } else {
            submenu.style.display = 'flex';
        }
    });
});