Set WshShell = CreateObject("WScript.Shell")
WshShell.CurrentDirectory = ".\onegame-client"
WshShell.Run "cmd /c mvn javafx:run", 0, False
