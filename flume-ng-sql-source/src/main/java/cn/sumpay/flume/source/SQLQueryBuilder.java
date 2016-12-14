package cn.sumpay.flume.source;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.sumpay.flume.utils.JdbcDriver;
import cn.sumpay.flume.utils.SQLSourceUtils;

public class SQLQueryBuilder {
	private static final Logger log = LoggerFactory.getLogger(SQLQueryBuilder.class);

	public static String buildQuery(SQLSourceUtils sqlUtils, String beanType) {
		String incrColumName = sqlUtils.getIncrementalColumnName();
		Long incrValue = Long.valueOf(sqlUtils.getIncrementalValue());
		String table = sqlUtils.getTable();
		String columns = sqlUtils.getColumnsToSelect();
		JdbcDriver driver = JdbcDriver.valueOf(sqlUtils.getDriverName().toUpperCase());
		int maxRows = sqlUtils.getMaxRows();

		String where = incrColumName + ">" + incrValue;
		String query = "";
		if (sqlUtils.getCustomQuery() == null) {
			switch (driver.ordinal()) {
			case 0:
				query = "SELECT " + columns + " FROM " + table + " WHERE " + where + " LIMIT " + maxRows + ";";
				break;
			case 1:
				break;
			case 2:
				break;
			case 3:
				query = "SELECT * FROM (SELECT ROWNUM rn,t.* FROM (select " + columns + " from " + table
						+ " ORDER BY ID) t) WHERE rn >" + incrValue + " and rn <= " + (incrValue + maxRows);
				break;
			case 4:
				break;
			case 5:
				query = "SELECT TOP " + maxRows + " " + columns + " FROM " + table + " WHERE " + where + ";";

				break;
			}
		} else {
			query = buildCustomQuery(sqlUtils, where);
		}
		return query;
	}

	public static String buildCustomQuery(SQLSourceUtils sqlUtils, String where) {
		String customQuery = sqlUtils.getCustomQuery();
		if (!customQuery.contains("@")) {
			log.error("Check custom query, it must contents @ where incremental value should be appears");
			return null;
		}
		customQuery = customQuery.replace("@", where);
		return customQuery;
	}
}