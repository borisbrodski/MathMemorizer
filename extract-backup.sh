 (echo -ne "�     " ; dd if=backup.ab bs=1 skip=24) | gzip -dc - | tar -xvf - 
