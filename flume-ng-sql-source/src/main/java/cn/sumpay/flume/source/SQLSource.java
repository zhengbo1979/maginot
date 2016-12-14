package cn.sumpay.flume.source;

import java.io.IOException;
import java.io.Writer;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.flume.Context;
import org.apache.flume.Event;
import org.apache.flume.EventDeliveryException;
import org.apache.flume.PollableSource;
import org.apache.flume.conf.Configurable;
import org.apache.flume.conf.ConfigurationException;
import org.apache.flume.event.SimpleEvent;
import org.apache.flume.source.AbstractSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;

import cn.sumpay.flume.bean.WordsCountBean;
import cn.sumpay.flume.metrics.SqlSourceCounter;
import cn.sumpay.flume.utils.JdbcDriver;
import cn.sumpay.flume.utils.SQLSourceUtils;
import cn.sumpay.flume.utils.SqlDBEngine;

public class SQLSource extends AbstractSource implements Configurable,
		PollableSource {
	private static final Logger log = LoggerFactory.getLogger(SQLSource.class);
	private SqlDBEngine mDBEngine;
	protected SQLSourceUtils sqlSourceUtils;
	private boolean isConnected;
	private JdbcDriver driver;
	private Statement statement;
	private SqlSourceCounter sqlSourceCounter;

	public void configure(Context context) throws ConfigurationException {
		log.info("Reading and processing configuration values for source "
				+ getName());

		this.sqlSourceUtils = new SQLSourceUtils(context);

		this.sqlSourceCounter = new SqlSourceCounter("SOURCESQL." + getName());
		this.sqlSourceCounter.start();

		this.mDBEngine = new SqlDBEngine(
				this.sqlSourceUtils.getConnectionURL(),
				this.sqlSourceUtils.getUserDataBase(),
				this.sqlSourceUtils.getPasswordDatabase());

		log.info("Establishing connection to "
				+ this.sqlSourceUtils.getConnectionURL() + " for source  "
				+ getName());

		if (loadDriver(this.sqlSourceUtils.getDriverName())) {
			log.info("Source " + getName() + " CONNECTED to database");
		} else {
			throw new ConfigurationException("Error loading driver "
					+ this.sqlSourceUtils.getDriverName());
		}
	}

	public PollableSource.Status process() throws EventDeliveryException {
		log.error("start SQL source, " + System.currentTimeMillis());
		String query = SQLQueryBuilder.buildQuery(this.sqlSourceUtils,"words_count");
		log.debug("Running query: " + query);
		try {
			ResultSet queryResult = this.mDBEngine.runQuery(query,
					this.statement);
			if (queryResult.isBeforeFirst()) {
				// 游标指到最前
				ChannelWriter cw = new ChannelWriter();
				Gson gson = new Gson();
				long maxId = this.sqlSourceUtils.getIncrementalValue();
				for (; queryResult.next();) {
					WordsCountBean wcb = new WordsCountBean();
					long id = queryResult.getLong("id");
					if (maxId < id) maxId = id;
					wcb.setId(id);
					wcb.setWord(queryResult.getString("word"));
					wcb.setCount(queryResult.getLong("count"));
					wcb.setSpoutThreadName(queryResult
							.getString("spout_thread_name"));
					wcb.setSpoutIp(queryResult.getString("spout_ip"));
					wcb.setSplitThreadName(queryResult
							.getString("split_thread_name"));
					wcb.setSplitIp(queryResult.getString("split_ip"));
					wcb.setCountThreadName(queryResult
							.getString("count_thread_name"));
					wcb.setCountIp(queryResult.getString("count_ip"));
					wcb.setThreadName(queryResult.getString("thread_name"));
					wcb.setClientIp(queryResult.getString("client_ip"));
					wcb.setCreatedAt(queryResult.getString("created_at"));
					
					cw.write(gson.toJson(wcb));
				}
				cw.close();

				queryResult.close();
				this.sqlSourceUtils.updateStatusFile(maxId);
			} else {
				log.debug("Empty ResultSet after running query");
			}

			Thread.sleep(this.sqlSourceUtils.getRunQueryDelay());
			return PollableSource.Status.READY;
		} catch (SQLException e) {
			log.error("SQL exception, check if query for source " + getName()
					+ " is correctly constructed");
			log.error(e.getMessage(), e);
			return PollableSource.Status.BACKOFF;
		} catch (InterruptedException e) {
			log.error("Interruptedexception", e);
			return PollableSource.Status.BACKOFF;
		} catch (Exception e) {
			log.error("Error procesing row", e);
		}
		return PollableSource.Status.BACKOFF;
	}

	public void start(Context context) {
		log.info("Starting sql source {} ...", getName());
		super.start();
	}

	public void stop() {
		log.info("Stopping sql source {} ...", getName());
		try {
			if (this.isConnected) {
				log.info("Closing connection to database "
						+ this.sqlSourceUtils.getConnectionURL());
				this.mDBEngine.CloseConnection();
			} else {
				log.info("Nothing to close for "
						+ this.sqlSourceUtils.getConnectionURL());
			}
		} catch (SQLException e) {
			log.warn("Error closing database connection "
					+ this.sqlSourceUtils.getConnectionURL());
		} catch (Exception e) {
			log.warn("Error CSVWriter object ", e);
		} finally {
			this.sqlSourceCounter.stop();
			super.stop();
		}
	}

	private boolean loadDriver(String driverName) {
		try {
			this.driver = JdbcDriver.valueOf(driverName.toUpperCase());
			switch (this.driver.ordinal()) {
			case 0:
				Class.forName("com.mysql.jdbc.Driver");
				this.mDBEngine.EstablishConnection();
				this.statement = this.mDBEngine.getConnection()
						.createStatement();
				this.isConnected = true;
				break;
			case 1:
				Class.forName("org.postgresql.Driver");
				this.mDBEngine.EstablishConnection();
				this.statement = this.mDBEngine.getConnection()
						.createStatement(1004, 1007);
				this.isConnected = true;
				break;
			case 2:
				Class.forName("org.sqlite.JDBC");
				this.mDBEngine = new SqlDBEngine(
						this.sqlSourceUtils.getConnectionURL());
				this.mDBEngine.EstablishConnection();
				this.statement = this.mDBEngine.getConnection()
						.createStatement(1003, 1007);
				this.isConnected = true;
				break;
			case 3:
				Class.forName("oracle.jdbc.OracleDriver");
				this.mDBEngine.EstablishConnection();
				this.statement = this.mDBEngine.getConnection()
						.createStatement();
				this.isConnected = true;
				break;
			case 4:
				Class.forName("com.ibm.db2.jcc.DB2Driver");
				this.mDBEngine.EstablishConnection();
				this.statement = this.mDBEngine.getConnection()
						.createStatement();
				this.isConnected = true;
				break;
			case 5:
				Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
				this.mDBEngine.EstablishConnection();
				this.statement = this.mDBEngine.getConnection()
						.createStatement(1005, 1008);
				this.isConnected = true;
				break;
			default:
				this.mDBEngine.EstablishConnection();
				this.statement = this.mDBEngine.getConnection()
						.createStatement();
				this.isConnected = true;
			}
		} catch (SQLException e) {
			log.error(
					"Error establishing connection to database for driver {}: {}",
					this.sqlSourceUtils.getDriverName(), e.getMessage());
			this.isConnected = false;
		} catch (ClassNotFoundException e) {
			log.error("Error resgistering dinamic load driver {}: {}",
					this.sqlSourceUtils.getDriverName(), e.getMessage());
			this.isConnected = false;
		}
		return this.isConnected;
	}

	private class ChannelWriter extends Writer {
		private List<Event> events = new ArrayList<Event>();

		private ChannelWriter() {
		}

		public void write(char[] cbuf, int off, int len) throws IOException {
			Event event = new SimpleEvent();

			String s = new String(cbuf);
			event.setBody(s.substring(off, len).getBytes());

			Map<String, String> headers = new HashMap<String, String>();
			headers.put("timestamp", String.valueOf(System.currentTimeMillis()));
			headers.put("beanType", "words_count");
			event.setHeaders(headers);

			this.events.add(event);
			if (this.events.size() >= SQLSource.this.sqlSourceUtils
					.getBatchSize()) {
				flush();
			}
		}

		public void flush() throws IOException {
			SQLSource.this.getChannelProcessor().processEventBatch(this.events);
			this.events.clear();
		}

		public void close() throws IOException {
			flush();
		}
	}

	@Override
	public long getBackOffSleepIncrement() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public long getMaxBackOffSleepInterval() {
		// TODO Auto-generated method stub
		return 0;
	}
}