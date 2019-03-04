package luoli523.connection_pool;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class BasicConnectionPool implements ConnectionPool {

  private String url;
  private String user;
  private String password;
  private List<Connection> connectionPool;
  private List<Connection> usedConnections = new ArrayList<Connection>();
  private static int INITIAL_POOL_SIZE = 10;

  private  BasicConnectionPool(String url,
                               String user,
                               String password,
                               List<Connection> pool) {
    this.url = url;
    this.user = user;
    this.password = password;
    this.connectionPool = pool;
  }

  public static BasicConnectionPool create(String url,
                                           String user,
                                           String password) throws SQLException {
    List<Connection> pool = new ArrayList<Connection>(INITIAL_POOL_SIZE);
    for (int i = 0; i < INITIAL_POOL_SIZE; i++) {
      pool.add(createConnection(url, user, password));
    }
    return new BasicConnectionPool(url, user, password, pool);
  }

  private static Connection createConnection(String url,
                                             String user,
                                             String password) throws SQLException {
    return DriverManager.getConnection(url, user, password);
  }

  public Connection getConnection() {
    Connection con = connectionPool.remove(connectionPool.size() - 1);
    usedConnections.add(con);
    return con;
  }

  public boolean releaseConnection(Connection connection) {
    connectionPool.add(connection);
    return usedConnections.remove(connection);
  }

  public String getUrl() {
    return this.url;
  }

  public String getUser() {
    return this.user;
  }

  public String getPassword() {
    return this.password;
  }

  public int getSize() {
    return connectionPool.size() + usedConnections.size();
  }
}
