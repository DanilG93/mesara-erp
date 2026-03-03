@echo off
echo ===================================================
echo   ZAPOCINJEM AUTOMATSKO AZURIRANJE MESARA ERP-A
echo ===================================================

echo [1/4] Povlacim najnoviji kod sa Git-a...
git pull origin main

echo [2/4] Gasim trenutne kontejnere (Tvoja baza i pazar ostaju sacuvani)...
docker-compose down

echo [3/4] Bildujem i podizem novu verziju (Ovo moze potrajati par minuta)...
docker-compose up -d --build

echo [4/4] Cistim stare slike da oslobodim prostor na disku...
docker image prune -f

echo ===================================================
echo   NOVA VERZIJA JE USPESNO PODIGNUTA!
echo   Pristupi na: http://localhost:8080
echo ===================================================
pause