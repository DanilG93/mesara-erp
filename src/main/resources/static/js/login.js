// Čekamo da korisnik klikne "Prijavi se"
document.getElementById('loginForm').addEventListener('submit', async function(event) {
    // Sprečavamo da browser sam osveži stranicu (staro HTML ponašanje)
    event.preventDefault();

    const usernameInput = document.getElementById('username').value;
    const passwordInput = document.getElementById('password').value;
    const errorMsg = document.getElementById('errorMessage');

    try {
        // Šaljemo podatke na našu "Biletarnicu" koju smo malopre napravili
        const response = await fetch('/api/auth/login', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json' // Govorimo serveru da šaljemo JSON
            },
            body: JSON.stringify({
                username: usernameInput,
                password: passwordInput
            })
        });

        if (response.ok) {
            // Ako je server rekao "OK (200)", vadimo token iz odgovora
            const data = await response.json();

            // OVO JE KLJUČNO: Čuvamo token u memoriji browsera!
            localStorage.setItem('jwt_token', data.token);

            document.cookie = `jwt_token=${data.token}; path=/; max-age=86400; SameSite=Strict`;
            // Uspešno logovanje! Prebacujemo korisnika na glavnu stranicu (koju ćemo tek napraviti)
            window.location.href = '/entries';
        } else {
            // Ako je server vratio 403 Forbidden (pogrešna šifra)
            errorMsg.style.display = 'block';
        }
    } catch (error) {
        console.error('Error:', error);
        errorMsg.style.display = 'block';
    }
});