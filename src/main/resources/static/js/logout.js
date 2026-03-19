function logout() {
    localStorage.removeItem('jwt_token');
    document.cookie = "jwt_token=; path=/; expires=Thu, 01 Jan 1970 00:00:01 GMT; SameSite=Strict";
    window.location.href = '/login.html';
}

document.addEventListener('DOMContentLoaded', () => {
    const logoutBtn = document.getElementById('logoutButton');
    if (logoutBtn) {
        logoutBtn.addEventListener('click', (e) => {
            e.preventDefault();
            logout();
        });
    }
});