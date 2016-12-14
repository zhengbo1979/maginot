package cn.sumpay.groups.storm.bolt;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Date;
import java.util.Map;

import org.apache.http.client.utils.DateUtils;
import org.apache.storm.task.OutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseRichBolt;
import org.apache.storm.tuple.Tuple;
import org.bson.Document;

import cn.sumpay.groups.storm.utils.MongoDbPool;

import com.mongodb.client.MongoCollection;

/**
 * @author zhengbo
 * @date 2016年10月20日
 * 
 */
@SuppressWarnings("serial")
public class MongoDbBolt extends BaseRichBolt {
	private MongoCollection<Document> collection = null;

	public void declareOutputFields(OutputFieldsDeclarer arg0) {
	}

	public void prepare(@SuppressWarnings("rawtypes") Map conf,
			TopologyContext context, OutputCollector collector) {
		getConn();
	}

	private void getConn() {
		try {
			collection = MongoDbPool.getCollection();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void execute(Tuple tuple) {
		InsertDB(tuple);
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

		try {
			String dateString = DateUtils.formatDate(new Date(),
					"yy-MMM-dd HH:mm:ss");
			// 单个插入
			Document doc = new Document("word", word).append("count", count)
					.append("spout_thread_name", spout_thread_name)
					.append("spout_ip", spout_ip)
					.append("split_thread_name", split_thread_name)
					.append("split_ip", split_ip)
					.append("count_thread_name", count_thread_name)
					.append("count_ip", count_ip).append("thread_name", thread)
					.append("client_ip", ip).append("created_date", dateString);
			collection.insertOne(doc);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}