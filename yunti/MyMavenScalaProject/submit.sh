#!/bin/bash

home="${HOME}"
spark_home="${HOME}/soft/spark-2.4.0-bin-hadoop2.7"
submit_script="$spark_home/bin/spark-submit"
submit_dir="${HOME}/dev/git/luoli/yunti2me/yunti/MyMavenScalaProject"
submit_jar="scala-maven-example-1.0.0-SNAPSHOT.jar"

clz=$1
shift

bash ${submit_script} \
            --master local[*] \
            --class ${clz} \
            ${submit_dir}/target/${submit_jar} \
            ${spark_home} \
            $@
