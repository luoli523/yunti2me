package luoli523.connection_pool;

import org.apache.commons.dbcp.BasicDataSource;

import java.sql.Connection;
import java.sql.SQLException;

public class DBCPataSource {

  private static BasicDataSource ds = new BasicDataSource();

  static {
    ds.setUrl("jdbc:h2:mem:test");
    ds.setUsername("user");
    ds.setPassword("password");
    ds.setMinIdle(5);
    ds.setMaxIdle(10);
    ds.setMaxOpenPreparedStatements(100);
  }

  public static Connection getConnection() throws SQLException {
    return ds.getConnection();
  }

  private DBCPataSource() {}

}
