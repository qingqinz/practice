
#!/bin/bash

for date in {20201212..20201231}
do
    cp -r 20201211 $date
    for file_old in `ls $date|grep 20201211`
    do
        if test -f $file_old
        then
        file_new=`echo "$file_old"|sed "s/20201211/$date/g"`
        echo $file_new
        mv $date/$file_old $date/$file_new
        fi
    done
done