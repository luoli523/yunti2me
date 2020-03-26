#!/bin/bash

if [ $# -lt 2 ];then
  echo "Usage: $(basename $0) [groupId] [artifactId]"
  echo -e "\texample: $(basename $0) [org.alluxio] [alluxio-parent]\n"
  exit 1
fi

groupId=$1
shift
artifictId=$1
shift

mvn archetype:generate \
  -DgroupId=${groupId} \
  -DartifactId=${artifictId} \
  -DarchetypeArtifactId=maven-archetype-quickstart \
  -DinteractiveMode=false
