' TypeRush Native Silent Windows Launcher
' Launches TypeRush without any black command prompt window
Set WshShell = CreateObject("WScript.Shell")
Set fso = CreateObject("Scripting.FileSystemObject")
strPath = fso.GetParentFolderName(WScript.ScriptFullName)

If fso.FileExists(strPath & "\TypeRush.jar") Then
    WshShell.Run "javaw -jar """ & strPath & "\TypeRush.jar""", 0, False
ElseIf fso.FileExists(strPath & "\bin\com\typerush\Main.class") Then
    WshShell.Run "javaw -cp """ & strPath & "\bin;" & strPath & "\lib\*"" com.typerush.Main", 0, False
Else
    WshShell.Run "cmd /c cd /d """ & strPath & """ && run.bat", 1, False
End If
