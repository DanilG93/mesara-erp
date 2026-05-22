@echo off
echo ===================================================
echo   ZAPOCINJEM AUTOMATSKO AZURIRANJE MESARA ERP-A
echo ===================================================

echo [1/5] Pravim sigurnosnu kopiju (backup) baze podataka...
if not exist backups mkdir backups

:: Generisanje datuma i vremena za naziv fajla (npr. 20260522_1415)
set "timestamp=%date:~-4,4%%date:~-7,2%%date:~-10,2%_%time:~0,2%%time:~3,2%"
set "timestamp=%timestamp: =0%"

docker exec mesara-mysql mysqldump -u DB_USER -pDB_PASSWORD DB_NAME > backups\backup_%timestamp%.sql

echo Backup uspesan! Sacuvan u folder: backups\backup_%timestamp%.sql

echo [2/5] Povlacim najnoviji kod sa Git-a...
git pull origin main

echo [3/5] Gasim trenutne kontejnere (Tvoja baza i pazar ostaju sacuvani)...
docker-compose down

echo [4/5] Bildujem i podizem novu verziju (Ovo moze potrajati par minuta)...
docker-compose up -d --build

echo [5/5] Cistim stare slike da oslobodim prostor na disku...
docker image prune -f

echo ===================================================
echo   NOVA VERZIJA JE USPESNO PODIGNUTA I BAZA JE SACUVANA!
echo ===================================================
pause