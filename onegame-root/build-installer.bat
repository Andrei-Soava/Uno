@echo off
jpackage ^
  --name OneGameClient ^
  --input target ^
  --main-jar onegame-client-0.0.1-SNAPSHOT.jar ^
  --main-class onegame.client.esecuzione.AppWithMaven ^
  --type exe ^
  --icon src/main/resources/icon.ico ^
  --java-options "--enable-preview" ^
  --runtime-image "%JAVA_HOME%" ^
  --win-menu ^
  --win-shortcut ^
  --win-dir-chooser ^
  --verbose
pause
