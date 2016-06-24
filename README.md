# ExternalSDCard
外置SD卡路径的获取和读取 VIVO X5 


* 无法在外置SD卡上执行:File的createNewFile()方法;
* 只能通过Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);的方法在外置SD卡上创建新的文件或文件夹;
* 可以执行Linux上的下列命令:
  1.ls
  2.ls -l
  3.ls -l "/storage"
  4.mount
  5.不可以执行:ls -lh的命令，且ls -l命令返回的结果里面没有相应文件或者文件夹的大小值;
* 可以读取系统上的下列文件:
  1./proc/mounts; 
  2./system/etc/permissions/platform.xml
* 执行who和w命令时报错:
  java.io.IOException: Error running exec(). Command: [who,          	/storage/emulated/0]Working Directory: null Environment: null;

