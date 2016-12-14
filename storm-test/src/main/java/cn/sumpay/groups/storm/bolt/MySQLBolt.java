package cn.sumpay.groups.storm.bolt;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Map;

import org.apache.storm.task.OutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseRichBolt;
import org.apache.storm.tuple.Tuple;

import cn.sumpay.groups.storm.utils.DBPool;

/**
 * @author zhengbo
 * @date 2016年10月20日
 * 
 */
@SuppressWarnings("serial")
public class MySQLBolt extends BaseRichBolt {
	private Connection conn = null;
	private String table = "words_count"; // 表名

	public void declareOutputFields(OutputFieldsDeclarer arg0) {
	}

	public void prepare(@SuppressWarnings("rawtypes") Map conf, TopologyContext context, OutputCollector collector) {
		getConn();
	}

	private void getConn() {
		try {
			conn = DBPool.getInstance().getConnection();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void execute(Tuple tuple) {
		// SelectDB(tuple);
		InsertDB(tuple);
	}

	public void SelectDB(Tuple tuple) {
		String sql = "SELECT * FROM words_count WHERE 1=1 AND id>0";
		try {
			Statement statement = conn.createStatement();
			ResultSet rs = statement.executeQuery(sql);
			rs.next();
			rs.getString("word");
		} catch (SQLException e) {
			e.printStackTrace();
		} 
	}

	public void InsertDB(Tuple tuple) {
		String ip = "";
		try {
			ip = InetAddress.getLocalHost().getHostAddress();
		} catch (UnknownHostException e1) {
			e1.printStackTrace();
		}
		String word = tuple.getString(0);
		int count = tuple.getInteger(1);
		String spout_thread_name = tuple.getString(2);
		String spout_ip = tuple.getString(3);
		String split_thread_name = tuple.getString(4);
		String split_ip = tuple.getString(5);
		String count_thread_name = tuple.getString(6);
		String count_ip = tuple.getString(7);
		String thread = Thread.currentThread().getName();

		String sql = "replace into " + this.table
				+ "(word, count,spout_thread_name,spout_ip,split_thread_name,split_ip,count_thread_name,count_ip,thread_name,client_ip) values ('"
				+ word + "'," + count + ",'" + spout_thread_name + "','" + spout_ip + "','" + split_thread_name + "','"
				+ split_ip + "','" + count_thread_name + "','" + count_ip + "','" + thread + "','" + ip + "')";
		try {
			Statement statement = conn.createStatement();
			statement.executeUpdate(sql);
		} catch (SQLException e) {
			e.printStackTrace();
		} 
	}
}