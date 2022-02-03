
log_dir=/home/app/worth/service/
if [ ! -d $log_dir ];then
 mkdir $log_dir
fi
project=springboot-wind.jar
logname=nohup.out
pid=`ps -ef|grep $project|grep -v grep | awk '{print $2}'`
if [ -z "$pid" ];
then
 echo "[ not find  pid ]"
else
 echo "find pid: $pid "
 kill -9 $pid
fi

nohup java -Xms512m -Xmx1024m -jar $project  >>$log_dir/$logname --server.port=13012 &
