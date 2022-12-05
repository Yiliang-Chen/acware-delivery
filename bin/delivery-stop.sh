#!/bin/bash

CURRENT_DIR=$(cd "$(dirname "$0")";pwd)/..
pid=`cat $CURRENT_DIR/bin/pid`

kill -9 $pid

rm $CURRENT_DIR/bin/pid