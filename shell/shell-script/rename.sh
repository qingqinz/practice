#/bin/sh
echo "begin"
for file_old in `ls | grep ALL.txt`
#do file_new=`echo "$file_old"|sed 's/20200729/20200730/g'`
do file_new=`echo "$file_old"|sed 's/ALL.txt/.txt/g'`
  echo $file_new
  mv $file_old $file_new
done

