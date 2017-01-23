DRUID_TMP_DIR='/tmp/druid'
mkdir -p ${DRUID_TMP_DIR}
java `cat conf-quickstart/druid/historical/jvm.config | xargs` -cp "conf-quickstart/druid/_common:conf-quickstart/druid/historical:lib/*" io.druid.cli.Main server historical 1>${DRUID_TMP_DIR}/historical.log 2>&1 &
java `cat conf-quickstart/druid/broker/jvm.config | xargs` -cp "conf-quickstart/druid/_common:conf-quickstart/druid/broker:lib/*" io.druid.cli.Main server broker 1>${DRUID_TMP_DIR}/broker.log 2>&1 &
java `cat conf-quickstart/druid/coordinator/jvm.config | xargs` -cp "conf-quickstart/druid/_common:conf-quickstart/druid/coordinator:lib/*" io.druid.cli.Main server coordinator 1>${DRUID_TMP_DIR}/coordinator.log 2>&1 &
java `cat conf-quickstart/druid/overlord/jvm.config | xargs` -cp "conf-quickstart/druid/_common:conf-quickstart/druid/overlord:lib/*" io.druid.cli.Main server overlord 1>${DRUID_TMP_DIR}/overlord.log 2>&1 &
java `cat conf-quickstart/druid/middleManager/jvm.config | xargs` -cp "conf-quickstart/druid/_common:conf-quickstart/druid/middleManager:lib/*" io.druid.cli.Main server middleManager 1>${DRUID_TMP_DIR}/middleManager.log 2>&1 &
