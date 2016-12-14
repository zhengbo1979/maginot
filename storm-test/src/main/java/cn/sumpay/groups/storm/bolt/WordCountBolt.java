package cn.sumpay.groups.storm.bolt;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;

import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.BasicOutputCollector;
import org.apache.storm.topology.IBasicBolt;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Tuple;
import org.apache.storm.tuple.Values;

/**
 * @author zhengbo
 * @date 2016年10月19日
 * 
 */
public class WordCountBolt implements IBasicBolt {
	private Map<String, Integer> _counts = new HashMap<String, Integer>();

	public void prepare(Map conf, TopologyContext context) {
	}

	public void execute(Tuple tuple, BasicOutputCollector collector) {
		String word = tuple.getString(0);
		int count;
		if (_counts.containsKey(word)) {
			count = _counts.get(word);
		} else {
			count = 0;
		}
		count++;
		_counts.put(word, count);
		String ip = "";
		try {
			ip = InetAddress.getLocalHost().getHostAddress();
		} catch (UnknownHostException e1) {
			e1.printStackTrace();
		}
		collector.emit(new Values(word, count, tuple.getString(1), tuple
				.getString(2), tuple.getString(3), tuple.getString(4), Thread
				.currentThread().getName(), ip));
	}

	public void cleanup() {
	}

	public void declareOutputFields(OutputFieldsDeclarer declarer) {
		declarer.declare(new Fields("word", "count", "spout_thread_name",
				"spout_ip", "split_thread_name", "split_ip",
				"count_thread_name", "count_ip"));
	}

	public Map<String, Object> getComponentConfiguration() {
		return null;
	}
}