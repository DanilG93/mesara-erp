@echo off
cls
title Gasenje Mesara ERP-a
echo ===================================================
echo   SIGURNO GASENJE APLIKACIJE MESARA ERP
echo ===================================================
echo.
echo [1/1] Zaustavljam kontejnere i gasim servise...
echo (Molimo sacekaj da MySQL baza bezbedno zapise sve podatke...)
echo.

:: Gasenje kontejnera i oslobadjanje mreze (podaci u bazi ostaju sacuvani)
docker-compose down

echo.
echo ===================================================
echo   SVI SERVISI SU USPESNO I BEZBEDNO ZAUSTAVLJENI!
echo   Tvoji podaci u bazi su sacuvani.
echo ===================================================
echo.
pause