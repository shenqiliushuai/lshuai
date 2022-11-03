
::获取管理员权限
%1 mshta vbscript:CreateObject("Shell.Application").ShellExecute("cmd.exe","/c %~s0 ::","","runas",1)(window.close)&&exit
mkdir C:\Users\les.liu\Desktop\新建文件夹\test\
:: 使用管道符传递密码参数，权限密码
echo a123456!| schtasks /change /tn "my once" /sd 2022/11/02 /st 18:56:00