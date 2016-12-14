package cn.sumpay.flume.sink;

import cn.sumpay.flume.bean.ComCstInfoBean;
import cn.sumpay.flume.bean.WordsCountBean;
import cn.sumpay.flume.utils.JdbcDriver;
import cn.sumpay.flume.utils.SQLSourceUtils;

public class SQLSinkBuilder {
	public static String buildWordsCountQuery(SQLSourceUtils sqlUtils, WordsCountBean wcb) {
		String table = sqlUtils.getTable();
		JdbcDriver driver = JdbcDriver.valueOf(sqlUtils.getDriverName().toUpperCase());

		String query = "";
		switch (driver.ordinal()) {
		case 0:
			query = "replace into " + table
					+ "(id,word, count,spout_thread_name,spout_ip,split_thread_name,split_ip,count_thread_name,count_ip,thread_name,client_ip,created_at) values ("
					+ wcb.getId() + ",'" + wcb.getWord() + "'," + wcb.getCount() + ",'" + wcb.getSpoutThreadName()
					+ "','" + wcb.getSpoutIp() + "','" + wcb.getSplitThreadName() + "','" + wcb.getSplitIp() + "','"
					+ wcb.getCountThreadName() + "','" + wcb.getCountIp() + "','" + wcb.getThreadName() + "','"
					+ wcb.getClientIp() + "','" + wcb.getCreatedAt() + "')";
			break;
		case 1:
			break;
		case 2:
			break;
		case 3:
			break;
		case 4:
			break;
		case 5:
			query = "replace into " + table
					+ "(id,word, count,spout_thread_name,spout_ip,split_thread_name,split_ip,count_thread_name,count_ip,thread_name,client_ip,created_at) values ("
					+ wcb.getId() + ",'" + wcb.getWord() + "'," + wcb.getCount() + ",'" + wcb.getSpoutThreadName()
					+ "','" + wcb.getSpoutIp() + "','" + wcb.getSplitThreadName() + "','" + wcb.getSplitIp() + "','"
					+ wcb.getCountThreadName() + "','" + wcb.getCountIp() + "','" + wcb.getThreadName() + "','"
					+ wcb.getClientIp() + "','" + wcb.getCreatedAt() + "')";
			break;
		}
		return query;
	}

	public static String buildComCstInfoQuery(SQLSourceUtils sqlUtils, ComCstInfoBean wcb) {
		String table = sqlUtils.getTable();
		JdbcDriver driver = JdbcDriver.valueOf(sqlUtils.getDriverName().toUpperCase());

		String query = "";
		switch (driver.ordinal()) {
		case 0:
			query = "replace into " + table
					+ "(id,cst_no, cst_name,cst_state,cst_address,cst_email,reputation_grade,cst_email_validate,activate_time,create_time,reg_channel,cst_authdate) values ("
					+ wcb.getId() + ",'" + wcb.getCstNo() + "','" + wcb.getCstName() + "','" + wcb.getCstState() + "','"
					+ wcb.getCstAddress() + "','" + wcb.getCstEmail() + "','" + wcb.getReputationGrade() + "','"
					+ wcb.getCstEmailValidate() + "','" + wcb.getActivateTime() + "','" + wcb.getCreateTime() + "','"
					+ wcb.getRegChannel() + "','" + wcb.getCstAuthdate() + "')";
			break;
		case 1:
			break;
		case 2:
			break;
		case 3:
			break;
		case 4:
			break;
		case 5:
			query = "replace into " + table
					+ "(id,cst_no, cst_name,cst_state,cst_address,cst_email,reputation_grade,cst_email_validate,activate_time,create_time,reg_channel,cst_authdate) values ("
					+ wcb.getId() + ",'" + wcb.getCstNo() + "','" + wcb.getCstName() + "','" + wcb.getCstState() + "','"
					+ wcb.getCstAddress() + "','" + wcb.getCstEmail() + "','" + wcb.getReputationGrade() + "','"
					+ wcb.getCstEmailValidate() + "','" + wcb.getActivateTime() + "','" + wcb.getCreateTime() + "','"
					+ wcb.getRegChannel() + "','" + wcb.getCstAuthdate() + "')";
			break;
		}
		return query;
	}
}