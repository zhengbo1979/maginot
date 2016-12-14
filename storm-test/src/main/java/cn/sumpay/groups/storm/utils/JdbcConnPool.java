package cn.sumpay.groups.storm.utils;

import java.io.PrintWriter;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;

import javax.sql.DataSource;

/**
 * 一个简单的DataSource实现
 * 
 * @author leizhimin 2010-1-14 0:03:17
 */
public class JdbcConnPool implements DataSource,Serializable {
	private static final String dirverClassName = "com.mysql.jdbc.Driver";
	private static final String url = "jdbc:mysql://192.168.8.9:3306/storm";
	private static final String user = "zhengbo";
	private static final String pswd = "sumpay";
	// 连接池
	private static List<Connection> pool = (List<Connection>) Collections
			.synchronizedList(new LinkedList<Connection>());
	private static JdbcConnPool instance = new JdbcConnPool();

	static {
		try {
			Class.forName(dirverClassName);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

	private JdbcConnPool() {
	}

	/**
	 * 获取数据源单例
	 * 
	 * @return 数据源单例
	 */
	public synchronized static JdbcConnPool instance() {
		if (instance == null)
			instance = new JdbcConnPool();
		return instance;
	}

	/**
	 * 获取一个数据库连接
	 * 
	 * @return 一个数据库连接
	 * @throws SQLException
	 */
	public Connection getConnection() throws SQLException {
		synchronized (pool) {
			if (pool.size() > 0)
				return pool.remove(0);
			else
				return makeConnection();
		}
	}

	/**
	 * 连接归池
	 * 
	 * @param conn
	 */
	public void freeConnection(Connection conn) {
		pool.add(conn);
	}

	private Connection makeConnection() throws SQLException {
		return DriverManager.getConnection(url, user, pswd);
	}

	public Connection getConnection(String username, String password) throws SQLException {
		return DriverManager.getConnection(url, username, password);
	}

	public PrintWriter getLogWriter() throws SQLException {
		return null;
	}

	public void setLogWriter(PrintWriter out) throws SQLException {

	}

	public void setLoginTimeout(int seconds) throws SQLException {

	}

	public int getLoginTimeout() throws SQLException {
		return 0;
	}

	public <T> T unwrap(Class<T> iface) throws SQLException {
		return null;
	}

	public boolean isWrapperFor(Class<?> iface) throws SQLException {
		return false;
	}

	public Logger getParentLogger() throws SQLFeatureNotSupportedException {
		return null;
	}
}