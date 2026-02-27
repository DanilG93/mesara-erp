// Pomocne funkcije za selektovanje u filterima
function selectAll(name) {
    document.querySelectorAll(`input[name="${name}"]`).forEach(el => el.checked = true);
    if(event) event.preventDefault();
}

function deselectAll(name) {
    document.querySelectorAll(`input[name="${name}"]`).forEach(el => el.checked = false);
    if(event) event.preventDefault();
}

// Funkcija za štampu (Samo uspravno)
function handlePrint() {
    // Filtriramo šta se štampa na osnovu checkboxova unutar kartica
    document.querySelectorAll('.print-card').forEach(card => {
        const chk = card.querySelector('.print-chk');
        if (chk && !chk.checked) card.classList.add('d-none-print');
        else card.classList.remove('d-none-print');
    });

    window.print();
}

$(document).ready(function() {
    // 1. Logika za Doughnut grafikone (Krugovi)
    if (typeof categoryData !== 'undefined' && categoryData) {
        const labels = categoryData.map(d => d.categoryName);
        const colors = ['#28a745', '#007bff', '#dc3545', '#ffc107', '#17a2b8', '#6610f2'];
        const config = (data) => ({
            type: 'doughnut',
            data: { labels: labels, datasets: [{ data: data, backgroundColor: colors }] },
            options: { responsive: true, maintainAspectRatio: false, plugins: { legend: { position: 'bottom' }}}
        });

        new Chart(document.getElementById('purchasePie'), config(categoryData.map(d => d.totalReceived)));
        new Chart(document.getElementById('salesPie'), config(categoryData.map(d => d.totalSold)));
        new Chart(document.getElementById('wastePie'), config(categoryData.map(d => d.totalWaste)));
    }

    // 2. Logika za Grouped Bar Chart (Glavni grafikon)
    if (typeof labelsData !== 'undefined' && labelsData.length > 0) {
        const ctx = document.getElementById('groupedChart').getContext('2d');
        const findVal = (store, product, type) => {
            const entry = contributionsData.find(c => c.storeName === store && c.productName === product);
            return entry ? entry[type] : 0;
        };

        const datasets = [];
        storeNamesData.forEach((store, index) => {
            const opacity = 1 - (index * 0.15);
            datasets.push({ label: store + ' (U)', data: labelsData.map(l => findVal(store, l, 'received')), backgroundColor: `rgba(40, 167, 69, ${opacity})`, stack: '0' });
            datasets.push({ label: store + ' (P)', data: labelsData.map(l => findVal(store, l, 'sold')), backgroundColor: `rgba(0, 123, 255, ${opacity})`, stack: '1' });
            datasets.push({ label: store + ' (O)', data: labelsData.map(l => findVal(store, l, 'waste')), backgroundColor: `rgba(220, 53, 69, ${opacity})`, stack: '2' });
        });

        new Chart(ctx, {
            type: 'bar',
            data: { labels: labelsData, datasets: datasets },
            options: {
                responsive: true,
                maintainAspectRatio: false,
                scales: { y: { stacked: true, beginAtZero: true } },
                plugins: { legend: { position: 'bottom' }}
            }
        });
    }
});






!-----------------------------


$(document).ready(function() {
        const catData = [[${categoryData}]];
        if (catData) {
            const labels = catData.map(d => d.categoryName);
            const colors = ['#28a745', '#007bff', '#dc3545', '#ffc107', '#17a2b8', '#6610f2'];
            const config = (data) => ({
                type: 'doughnut',
                data: { labels: labels, datasets: [{ data: data, backgroundColor: colors }] },
                options: {
                    responsive: true,
                    maintainAspectRatio: false,
                    plugins: { legend: { position: 'bottom', labels: { boxWidth: 10, font: { size: 9 } } }}
                }
            });
            new Chart(document.getElementById('purchasePie'), config(catData.map(d => d.totalReceived)));
            new Chart(document.getElementById('salesPie'), config(catData.map(d => d.totalSold)));
            new Chart(document.getElementById('wastePie'), config(catData.map(d => d.totalWaste)));
        }

        const labels = [[${labels}]];
        const contributions = [[${storeContributions}]];
        const storeNames = [[${uniqueStoreNames}]];
        if (labels && labels.length > 0) {
            const ctx = document.getElementById('groupedChart').getContext('2d');
            const findVal = (store, product, type) => {
                const entry = contributions.find(c => c.storeName === store && c.productName === product);
                return entry ? entry[type] : 0;
            };

            const datasets = [];
            storeNames.forEach((store, index) => {
                const opacity = 1 - (index * 0.15);
                datasets.push({ label: store + ' (U)', data: labels.map(l => findVal(store, l, 'received')), backgroundColor: `rgba(40, 167, 69, ${opacity})`, stack: '0' });
                datasets.push({ label: store + ' (P)', data: labels.map(l => findVal(store, l, 'sold')), backgroundColor: `rgba(0, 123, 255, ${opacity})`, stack: '1' });
                datasets.push({ label: store + ' (O)', data: labels.map(l => findVal(store, l, 'waste')), backgroundColor: `rgba(220, 53, 69, ${opacity})`, stack: '2' });
            });

            const displayLabels = labels.map(l => l.length > 30 ? l.substring(0, 30) + '...' : l);

            new Chart(ctx, {
                type: 'bar',
                data: { labels: displayLabels, datasets: datasets },
                options: {
                    responsive: true,
                    maintainAspectRatio: false,
                    scales: { y: { stacked: true, beginAtZero: true } },
                    plugins: { legend: { position: 'bottom', labels: { font: { size: 9 } } }}
                }
            });
        }
    });




