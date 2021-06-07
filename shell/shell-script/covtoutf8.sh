#/bin/sh
echo "begin"
for file_old in `ls | grep .txt`
do file_new=`echo "$file_old"|sed 's/.txt/.txt.utf8/g'`
iconv -f gbk -t utf8 $file_old>$file_new
#do file_new=`echo "$file_old"|sed 's/ABC20200731001/wangchunyu001/g'`
  echo $file_new
  mv $file_new $file_old
done