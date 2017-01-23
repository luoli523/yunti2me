# Set Hadoop-specific environment variables here.

# The only required environment variable is JAVA_HOME.  All others are
# optional.  When running a distributed configuration it is best to
# set JAVA_HOME in this file, so that it is correctly defined on
# remote nodes.

export HADOOP_HOME=$HOME/hadoop-current

# The java implementation to use.  Required.
export JAVA_HOME=$HOME/java-current

# Where log files are stored.  $HADOOP_HOME/logs by default.
export HADOOP_LOG_DIR=$HOME/cluster-data/logs

# Extra Java CLASSPATH elements.  Optional.
export HADOOP_CLASSPATH=$HOME/extra-jar

# The maximum amount of heap to use, in MB. Default is 1000.
#export HADOOP_HEAPSIZE=81920

# Extra Java runtime options.  Empty by default.
# export HADOOP_OPTS=-server

# Command specific options appended to HADOOP_OPTS when specified
export HADOOP_NAMENODE_OPTS="-Xmx133120m -Xms133120m -Xmn4096m -Xbootclasspath/p:/home/hadoop/java-current/hack/socket_patch.jar -verbose:gc -Xloggc:$HADOOP_LOG_DIR/namenode.gc.log -XX:ErrorFile=$HADOOP_LOG_DIR/hs_err_pid.log -XX:+PrintGCDateStamps -XX:+PrintGCDetails -XX:+HeapDumpOnOutOfMemoryError -XX:+UseConcMarkSweepGC -XX:CMSInitiatingOccupancyFraction=85 -XX:+UseCMSCompactAtFullCollection -XX:CMSMaxAbortablePrecleanTime=1000 -XX:+CMSClassUnloadingEnabled -XX:+DisableExplicitGC -Dcom.sun.management.jmxremote $HADOOP_NAMENODE_OPTS"
export HADOOP_SECONDARYNAMENODE_OPTS="-Xms133120m -Xmx133120m -Xmn4096M -XX:SurvivorRatio=10 -verbose:gc -Xloggc:$HADOOP_LOG_DIR/secondarynamenode.gc.log -XX:ErrorFile=$HADOOP_LOG_DIR/hs_err_pid.log -XX:+PrintGCDateStamps -XX:+PrintGCDetails -XX:+HeapDumpOnOutOfMemoryError -XX:+UseConcMarkSweepGC -XX:CMSInitiatingOccupancyFraction=85 -XX:+UseCMSCompactAtFullCollection -XX:CMSMaxAbortablePrecleanTime=1000 -XX:+CMSClassUnloadingEnabled -XX:+DisableExplicitGC"
export HADOOP_DATANODE_OPTS="-Xmx1536m -XX:+HeapDumpOnOutOfMemoryError -Dcom.sun.management.jmxremote -Xloggc:$HADOOP_LOG_DIR/datanode.gc.log -XX:+PrintGCDateStamps -XX:+PrintGCDetails $HADOOP_DATANODE_OPTS"
export HADOOP_BALANCER_OPTS="-Xmx1536m -Dcom.sun.management.jmxremote $HADOOP_BALANCER_OPTS"
export HADOOP_JOBTRACKER_OPTS="-Xmx122880m -Xms122880m -Xmn4096m -verbose:gc -Xloggc:$HADOOP_LOG_DIR/jobtracker.gc.log -XX:ErrorFile=$HADOOP_LOG_DIR/hs_err_pid.log -XX:+PrintPromotionFailure -XX:+PrintGCDateStamps -XX:+PrintGCDetails -XX:+HeapDumpOnOutOfMemoryError -XX:+UseConcMarkSweepGC -XX:CMSInitiatingOccupancyFraction=85 -XX:+UseCMSCompactAtFullCollection -XX:CMSMaxAbortablePrecleanTime=1000 -XX:+CMSClassUnloadingEnabled -XX:+DisableExplicitGC -Dcom.sun.management.jmxremote $HADOOP_JOBTRACKER_OPTS"
export HADOOP_TASKTRACKER_OPTS="-Xmx1536m -XX:+HeapDumpOnOutOfMemoryError -Xloggc:$HADOOP_LOG_DIR/tasktracker.gc.log -XX:+PrintGCDateStamps -XX:+PrintGCDetails $HADOOP_TASKTRACKER_OPTS"

# The following applies to multiple commands (fs, dfs, fsck, distcp etc)
export HADOOP_CLIENT_OPTS="-Xmx1536m $HADOOP_CLIENT_OPTS"

# Extra ssh options.  Empty by default.
export HADOOP_SSH_OPTS="-o ConnectTimeout=3  -o StrictHostKeyChecking=no -o SendEnv=HADOOP_CONF_DIR"

# File naming remote slave hosts.  $HADOOP_HOME/conf/slaves by default.
# export HADOOP_SLAVES=${HADOOP_HOME}/conf/slaves

# host:path where hadoop code should be rsync'd from.  Unset by default.
# export HADOOP_MASTER=master:/home/$USER/src/hadoop

# Seconds to sleep between slave commands.  Unset by default.  This
# can be useful in large clusters, where, e.g., slave rsyncs can
# otherwise arrive faster than the master can service them.
# export HADOOP_SLAVE_SLEEP=0.1

# The directory where pid files are stored. /tmp by default.
export HADOOP_PID_DIR=$HOME/cluster-data/pids

# A string representing this instance of hadoop. $USER by default.
# export HADOOP_IDENT_STRING=$USER

# The scheduling priority for daemon processes.  See 'man nice'.
# export HADOOP_NICENESS=10

# open core dump
#ulimit -c unlimited
ulimit -u 5000
