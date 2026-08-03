@ECHO OFF
WHERE gradle >NUL 2>NUL
IF %ERRORLEVEL% NEQ 0 (
  ECHO Gradle 8.7 n'est pas installe sur cette machine.
  ECHO Installe Gradle 8.7 ou utilise le workflow GitHub Actions fourni.
  EXIT /B 1
)
gradle %*
