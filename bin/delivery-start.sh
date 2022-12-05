#!/bin/bash
source ~/.bash_profile

CURRENT_DIR=$(cd "$(dirname "$0")";pwd)/..

cd $CURRENT_DIR/conf
$JAVA_HOME/bin/jar -uf $CURRENT_DIR/lib/acware-delivery-*.jar server.json
cd $CURRENT_DIR/logs
$JAVA_HOME/bin/jar -uf $CURRENT_DIR/lib/acware-delivery-*.jar log4j.properties

nohup $JAVA_HOME/bin/java -jar $CURRENT_DIR/lib/acware-delivery-*.jar > /dev/null 2>&1 & echo $! >$CURRENT_DIR/bin/pid
