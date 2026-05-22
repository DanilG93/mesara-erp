@echo off
cls
title Pokretanje Mesara ERP-a
echo ===================================================
echo   POKRETANJE APLIKACIJE MESARA ERP
echo ===================================================

echo [1/2] Palim Docker kontejnere...
docker-compose up -d

echo.
echo [2/2] Cekam da se Spring Boot i baza potpuno podignu...
echo Molimo sacekaj, proveravam dostupnost portova...
echo.

:PROVERA
:: Ceka 3 sekunde pre sledeće provere
timeout /t 3 /nobreak >nul

:: Salje tihi zahtev na login stranicu i proverava da li je HTTP status 200 (OK)
curl -s -o NUL -w "%%{http_code}" http://localhost:8080/login.html | findstr "200" >nul

:: Ako login stranica jos uvek ne vraca 200, ispiši tačku i probaj ponovo
if errorlevel 1 (
    set /p ="." <nul
    goto PROVERA
)

echo.
echo ===================================================
echo   APLIKACIJA JE USPESNO PODIGNUTA I SPREMNA!
echo ===================================================
echo.
echo Otvaram login stranicu u tvom browseru...

:: Automatski otvara tvoj podrazumevani pretraživač na login stranici
start http://localhost:8080/login.html

echo.
pause