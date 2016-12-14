package cn.sumpay.groups.storm.utils;

import java.beans.PropertyVetoException;
import java.sql.Connection;
import java.sql.SQLException;

import com.mchange.v2.c3p0.ComboPooledDataSource;

public class DBPool {
	private static DBPool dbPool;
	private static ComboPooledDataSource dataSource;

	private static final String dirverClassName = "com.mysql.jdbc.Driver";
	private static final String url = "jdbc:mysql://192.168.8.9:3306/storm?autoReconnect=true&useUnicode=true&characterEncoding=UTF8";
	private static final String user = "zhengbo";
	private static final String pswd = "sumpay";
	private static final int initPoolSize = 10;
	private static final int minPoolSize = 5;
	private static final int maxPoolSize = 20;
	private static final int maxStatements = 50;
	private static final int maxIdleTime = 10;
	static {
		dbPool = new DBPool();
	}

	private DBPool() {
		try {
			dataSource = new ComboPooledDataSource();
			dataSource.setUser(user);
			dataSource.setPassword(pswd);
			dataSource.setJdbcUrl(url);
			dataSource.setDriverClass(dirverClassName);
			dataSource.setInitialPoolSize(initPoolSize);
			dataSource.setMinPoolSize(minPoolSize);
			dataSource.setMaxPoolSize(maxPoolSize);
			dataSource.setMaxStatements(maxStatements);
			dataSource.setMaxIdleTime(maxIdleTime);
		} catch (PropertyVetoException e) {
			throw new RuntimeException(e);
		}
	}

	public final static DBPool getInstance() {
		return (dbPool != null) ? dbPool : new DBPool();
	}

	public final Connection getConnection() {
		try {
			return dataSource.getConnection();
		} catch (SQLException e) {
			throw new RuntimeException("无法从数据源获取连接 ", e);
		}
	}
}