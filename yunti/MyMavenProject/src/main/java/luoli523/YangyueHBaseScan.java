package luoli523;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.client.HTable;

import java.io.IOException;

public class YangyueHBaseScan {

    private static HTable htable = null;

    public static void init() throws IOException {
        Configuration conf = new Configuration();
        conf.set("hbase.zookeeper.property.clientPort", "2181");
        conf.set("hbase.zookeeper.quorum", "hbase-test");
        htable = new HTable(conf, "file");
    }
}
