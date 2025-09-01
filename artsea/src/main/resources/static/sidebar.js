function toggleMenu(event) {
    const sidebar = document.getElementById("sidebar");
    sidebar.classList.toggle("show");

    // For desktop - toggle between collapsed and expanded
    if (window.innerWidth > 768) {
        sidebar.classList.toggle("collapsed");
        document.querySelector('.main').style.marginLeft = sidebar.classList.contains('collapsed') ? '80px' : '260px';
    }

    event.stopPropagation(); // <-- Prevent document click from immediately closing
}


// Close sidebar when clicking outside on mobile
document.addEventListener('click', function (event) {
    const sidebar = document.getElementById('sidebar');
    const toggleBtn = document.querySelector('.menu-toggle');

    if (window.innerWidth <= 768 && !sidebar.contains(event.target) && event.target !== toggleBtn) {
        sidebar.classList.remove('show');
    }
});

// Auto-collapse sidebar on smaller screens
function handleResize() {
    const sidebar = document.getElementById('sidebar');
    if (window.innerWidth <= 768) {
        sidebar.classList.remove('collapsed');
        document.querySelector('.main').style.marginLeft = '0';
    } else {
        sidebar.classList.remove('show');
    }
}

window.addEventListener('resize', handleResize);
handleResize(); // Initialize