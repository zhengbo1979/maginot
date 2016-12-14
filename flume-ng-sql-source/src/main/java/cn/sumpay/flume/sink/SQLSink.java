package cn.sumpay.flume.sink;

import java.sql.SQLException;
import java.sql.Statement;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.flume.Context;
import org.apache.flume.Event;
import org.apache.flume.EventDeliveryException;
import org.apache.flume.Sink;
import org.apache.flume.Transaction;
import org.apache.flume.conf.Configurable;
import org.apache.flume.conf.ConfigurationException;
import org.apache.flume.sink.AbstractSink;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;

import cn.sumpay.flume.bean.ComCstInfoBean;
import cn.sumpay.flume.bean.WordsCountBean;
import cn.sumpay.flume.metrics.SqlSourceCounter;
import cn.sumpay.flume.utils.JdbcDriver;
import cn.sumpay.flume.utils.SQLSourceUtils;
import cn.sumpay.flume.utils.SqlDBEngine;

public class SQLSink extends AbstractSink implements Configurable {
	private static final Logger log = LoggerFactory.getLogger(SQLSink.class);
	private SqlDBEngine mDBEngine;
	protected SQLSourceUtils sqlSourceUtils;
	private boolean isConnected;
	private JdbcDriver driver;
	private Statement statement;
	private SqlSourceCounter sqlSourceCounter;

	public void configure(Context context) throws ConfigurationException {
		log.info("Reading and processing configuration values for source " + getName());

		this.sqlSourceUtils = new SQLSourceUtils(context);

		this.sqlSourceCounter = new SqlSourceCounter("SOURCESQL." + getName());
		this.sqlSourceCounter.start();

		this.mDBEngine = new SqlDBEngine(this.sqlSourceUtils.getConnectionURL(), this.sqlSourceUtils.getUserDataBase(),
				this.sqlSourceUtils.getPasswordDatabase());

		log.info("Establishing connection to " + this.sqlSourceUtils.getConnectionURL() + " for source  " + getName());

		if (loadDriver(this.sqlSourceUtils.getDriverName())) {
			log.info("Source " + getName() + " CONNECTED to database");
		} else {
			throw new ConfigurationException("Error loading driver " + this.sqlSourceUtils.getDriverName());
		}
	}

	@Override
	public Sink.Status process() throws EventDeliveryException {
		Transaction tx = getChannel().getTransaction();
		try {
			tx.begin();
			for (int i = 0; i < this.sqlSourceUtils.getMaxRows(); i++) {
				Event e = getChannel().take();
				if (e == null) {
					break;
				}
				try {
					String data = new String(e.getBody());
					Map<String, String> header = e.getHeaders();
					log.error("event from channel :" + data);

					Gson gson = new Gson();
					String insert = "";
					if (StringUtils.isNotEmpty(header.get("beanType"))
							&& header.get("beanType").equals("com_cst_info")) {
						ComCstInfoBean wcb = gson.fromJson(data, ComCstInfoBean.class);
						insert = SQLSinkBuilder.buildComCstInfoQuery(this.sqlSourceUtils, wcb);
					} else if (StringUtils.isNotEmpty(header.get("beanType"))
							&& header.get("beanType").equals("words_count")) {
						WordsCountBean wcb = gson.fromJson(data, WordsCountBean.class);
						insert = SQLSinkBuilder.buildWordsCountQuery(this.sqlSourceUtils, wcb);
					}
					this.mDBEngine.runUpdate(insert, this.statement);
				} catch (Exception ex) {
					throw ex;
				}
			}
			tx.commit();
			return Status.READY;
		} catch (Exception ex) {
			tx.rollback();
			if (log.isErrorEnabled()) {
				log.error(this.getName() + " - Exception while publishing...", ex);
			}
			return Status.BACKOFF;
		} finally {
			tx.close();
		}
	}

	public void start(Context context) {
		log.info("Starting sql source {} ...", getName());
		super.start();
	}

	public void stop() {
		log.info("Stopping sql source {} ...", getName());
		try {
			if (this.isConnected) {
				log.info("Closing connection to database " + this.sqlSourceUtils.getConnectionURL());
				this.mDBEngine.CloseConnection();
			} else {
				log.info("Nothing to close for " + this.sqlSourceUtils.getConnectionURL());
			}
		} catch (SQLException e) {
			log.warn("Error closing database connection " + this.sqlSourceUtils.getConnectionURL());
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
				this.statement = this.mDBEngine.getConnection().createStatement();
				this.isConnected = true;
				break;
			case 1:
				Class.forName("org.postgresql.Driver");
				this.mDBEngine.EstablishConnection();
				this.statement = this.mDBEngine.getConnection().createStatement(1004, 1007);
				this.isConnected = true;
				break;
			case 2:
				Class.forName("org.sqlite.JDBC");
				this.mDBEngine = new SqlDBEngine(this.sqlSourceUtils.getConnectionURL());
				this.mDBEngine.EstablishConnection();
				this.statement = this.mDBEngine.getConnection().createStatement(1003, 1007);
				this.isConnected = true;
				break;
			case 3:
				Class.forName("oracle.jdbc.OracleDriver");
				this.mDBEngine.EstablishConnection();
				this.statement = this.mDBEngine.getConnection().createStatement();
				this.isConnected = true;
				break;
			case 4:
				Class.forName("com.ibm.db2.jcc.DB2Driver");
				this.mDBEngine.EstablishConnection();
				this.statement = this.mDBEngine.getConnection().createStatement();
				this.isConnected = true;
				break;
			case 5:
				Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
				this.mDBEngine.EstablishConnection();
				this.statement = this.mDBEngine.getConnection().createStatement(1005, 1008);
				this.isConnected = true;
				break;
			default:
				this.mDBEngine.EstablishConnection();
				this.statement = this.mDBEngine.getConnection().createStatement();
				this.isConnected = true;
			}
		} catch (SQLException e) {
			log.error("Error establishing connection to database for driver {}: {}",
					this.sqlSourceUtils.getDriverName(), e.getMessage());
			this.isConnected = false;
		} catch (ClassNotFoundException e) {
			log.error("Error resgistering dinamic load driver {}: {}", this.sqlSourceUtils.getDriverName(),
					e.getMessage());
			this.isConnected = false;
		}
		return this.isConnected;
	}
}