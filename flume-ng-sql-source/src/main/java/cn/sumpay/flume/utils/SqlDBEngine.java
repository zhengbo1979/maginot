package cn.sumpay.flume.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class SqlDBEngine {
	private String mURL;
	private String mUser;
	private String mPassword;
	private Connection mConnection;

	public SqlDBEngine(String URL, String Username, String Password) {
		this.mURL = URL;
		this.mUser = Username;
		this.mPassword = Password;
	}

	public SqlDBEngine(String URL) {
		this.mURL = URL;
	}

	public void EstablishConnection() throws SQLException {
		this.mConnection = DriverManager.getConnection(this.mURL, this.mUser,
				this.mPassword);
	}

	public void CloseConnection() throws SQLException {
		this.mConnection.close();
	}

	public Connection getConnection() {
		return this.mConnection;
	}

	public ResultSet runQuery(String mQuery, Statement statement)
			throws SQLException {
		return statement.executeQuery(mQuery);
	}
	
	public int runUpdate(String mQuery, Statement statement)
			throws SQLException {
		return statement.executeUpdate(mQuery);
	}
}