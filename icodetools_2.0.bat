cd %~dp0
set aapt_path=D:\Android_tools\AndroidSdk\build-tools\23.0.1\aapt.exe
java -jar libs\icode_tools_2.0.jar %~dp0 src.apk %aapt_path% JW cn.wjdiankong.jw.utils.JWUtils printStackTrace
adb install -r signed.apk
pause..
