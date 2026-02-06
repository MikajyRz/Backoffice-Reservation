@echo off
chcp 65001 >nul
setlocal enabledelayedexpansion


set APP_NAME=BackofficeReservation
set WAR_NAME=%APP_NAME%.war

set JAVA_HOME=C:\Program Files\Java\jdk-17
set PATH=%JAVA_HOME%\bin;%PATH%


echo ======================================
echo 1) V├®rification de la structure
echo ======================================
set FRAMEWORK_SRC=..\Framework
echo Recherche des fichiers framework...
if exist "%FRAMEWORK_SRC%\src\com\framework\FrontServlet.java" (
    echo Ô£à Fichiers framework trouv├®s dans: %FRAMEWORK_SRC%
) else (
    echo ÔÜá´©Å Fichiers framework introuvables
    exit /b 1
)

echo ======================================
echo 2) Compilation du framework
echo ======================================
echo Compilation du framework...
if exist "%FRAMEWORK_SRC%" (
    if not exist framework_build mkdir framework_build

    rem Lister tous les fichiers .java du framework (annotations + framework + utils + classes + exceptions) avec leurs chemins complets
    dir /s /b ^
        "%FRAMEWORK_SRC%\src\com\framework\*.java" ^
        "%FRAMEWORK_SRC%\src\com\annotations\*.java" ^
        "%FRAMEWORK_SRC%\src\com\utils\*.java" ^
        "%FRAMEWORK_SRC%\src\com\interfaces\*.java" ^
        "%FRAMEWORK_SRC%\src\com\classes\*.java" ^
        "%FRAMEWORK_SRC%\src\com\exceptions\*.java" > sources_framework.txt 2>nul

    if exist sources_framework.txt (
        javac -d framework_build -cp "%FRAMEWORK_SRC%\lib\servlet-api.jar" @sources_framework.txt
        if errorlevel 1 (
            echo ÔÜá´©Å Erreur lors de la compilation du framework
            del sources_framework.txt
            exit /b 1
        )
        del sources_framework.txt
    ) else (
        echo ÔÜá´©Å Aucun fichier source Java du framework trouvé
        exit /b 1
    )
    
    cd framework_build
    jar cf framework.jar com
    move framework.jar ..\
    cd ..
    echo Ô£à Framework compil├® : framework.jar
) else (
    echo ÔÜá´©Å Dossier framework introuvable: %FRAMEWORK_SRC%
    exit /b 1
)

echo ======================================
echo 3) Compilation des fichiers de test
echo ======================================
echo Nettoyage des anciennes classes...
if exist webapp\WEB-INF\classes rmdir /s /q webapp\WEB-INF\classes 2>nul
mkdir webapp\WEB-INF\classes
echo Compilation des classes Java de test...
dir /s /b java\*.java > sources_test.txt 2>nul
if exist sources_test.txt (
    findstr /r "." sources_test.txt >nul 2>&1
    if !errorlevel! equ 0 (
        javac -parameters -cp "%FRAMEWORK_SRC%\lib\servlet-api.jar;framework.jar" -d webapp\WEB-INF\classes @sources_test.txt
        if !errorlevel! equ 0 (
            echo Ô£à Fichiers de test compilés
        ) else (
            echo ÔÜá´©Å Erreur lors de la compilation des test
            del sources_test.txt
            exit /b 1
        )
    ) else (
        echo ÔÜá´©Å Aucun fichier Java trouvé dans java\
        del sources_test.txt
        exit /b 1
    )
    del sources_test.txt
) else (
    echo ÔÜá´©Å Aucun fichier de test trouvé dans java\
    exit /b 1
)

echo ======================================
echo 4) Pr├®paration du projet test
echo ======================================
echo Cr├®ation de la structure WEB-INF...
if not exist webapp\WEB-INF\lib mkdir webapp\WEB-INF\lib

echo Copie de framework.properties dans WEB-INF\classes...
if exist framework.properties (
    copy framework.properties webapp\WEB-INF\classes\ >nul
    echo Ô£à framework.properties copi├® dans WEB-INF\classes
) else (
    echo ÔÜá´©Å framework.properties introuvable ├á la racine de test\
)

echo Copie du framework dans WEB-INF\lib...
copy framework.jar webapp\WEB-INF\lib\ >nul
del webapp\WEB-INF\lib\servlet-api.jar 2>nul
echo Ô£à Framework JAR copi├® dans WEB-INF\lib

for %%F in (postgresql-*.jar) do (
    copy "%%F" webapp\WEB-INF\lib\ >nul
)
for %%F in ("%FRAMEWORK_SRC%\lib\postgresql-*.jar") do (
    if exist "%%~fF" copy "%%~fF" webapp\WEB-INF\lib\ >nul
)

echo ======================================
echo 5) G├®n├®ration du WAR
echo ======================================
if not exist build mkdir build
cd webapp
jar cf ..\build\%WAR_NAME% *
cd ..
echo Ô£à WAR g├®n├®r├® : build\%WAR_NAME%

echo ======================================
echo 6) V├®rification du contenu
echo ======================================
if exist "framework_build\com\framework\FrontServlet.class" (
    echo Ô£à FrontServlet.class trouv├®
) else (
    echo ÔÜá´©Å FrontServlet.class introuvable
    echo Classes trouv├®es dans framework_build:
    dir framework_build /s /b
)

echo ======================================
echo 7) D├®ploiement dans Tomcat
echo ======================================
set TOMCAT_HOME=C:\tomcat\apache-tomcat-10.1.28
if exist "%TOMCAT_HOME%\webapps\%WAR_NAME%" (
    echo Suppression de l'ancienne version...
    del "%TOMCAT_HOME%\webapps\%WAR_NAME%"
    rmdir /s /q "%TOMCAT_HOME%\webapps\%APP_NAME%" 2>nul
)

copy build\%WAR_NAME% "%TOMCAT_HOME%\webapps\"
echo Ô£à Application d├®ploy├®e dans Tomcat
echo.
echo Ô£à Application disponible sur : http://localhost:8080/%APP_NAME%/
echo.

pause
