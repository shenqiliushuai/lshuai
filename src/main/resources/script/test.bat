
::��ȡ����ԱȨ��
%1 mshta vbscript:CreateObject("Shell.Application").ShellExecute("cmd.exe","/c %~s0 ::","","runas",1)(window.close)&&exit
mkdir C:\Users\les.liu\Desktop\�½��ļ���\test\
:: ʹ�ùܵ����������������Ȩ������
echo a123456!| schtasks /change /tn "my once" /sd 2022/11/02 /st 18:56:00