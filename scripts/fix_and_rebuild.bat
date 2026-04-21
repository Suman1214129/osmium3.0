@echo off
echo Cleaning project...
cd /d "%~dp0"

echo Deleting build directories...
if exist "app\build" rmdir /S /Q "app\build"
if exist "build" rmdir /S /Q "build"
if exist ".gradle" rmdir /S /Q ".gradle"
if exist ".idea" rmdir /S /Q ".idea"

echo.
echo ========================================
echo Cleanup complete!
echo ========================================
echo.
echo Next steps:
echo 1. Open Android Studio
echo 2. File ^> Invalidate Caches ^> Invalidate and Restart
echo 3. After restart, File ^> Sync Project with Gradle Files
echo 4. Build ^> Rebuild Project
echo 5. Run the app
echo.
pause
