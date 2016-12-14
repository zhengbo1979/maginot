package cn.sumpay.groups.storm.bolt;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Map;

import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.BasicOutputCollector;
import org.apache.storm.topology.IBasicBolt;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Tuple;
import org.apache.storm.tuple.Values;

import clojure.string__init;

/**
 * @author zhengbo
 * @date 2016年10月19日
 * 
 */
public class SplitSentenceBolt implements IBasicBolt {
	public void prepare(Map conf, TopologyContext context) {
	}

	public void execute(Tuple tuple, BasicOutputCollector collector) {
		String sentence = tuple.getString(0);
		String ip = "";
		try {
			ip = InetAddress.getLocalHost().getHostAddress();
		} catch (UnknownHostException e1) {
			e1.printStackTrace();
		}
		for (String word : sentence.split(" ")) {
			collector.emit(new Values(word, tuple.getString(1), tuple
					.getString(2), Thread.currentThread().getName(), ip));
		}
	}

	public void cleanup() {
	}

	public void declareOutputFields(OutputFieldsDeclarer declarer) {
		declarer.declare(new Fields("word", "spout_thread_name", "spout_ip",
				"split_thread_name", "split_ip"));
	}

	public Map<String, Object> getComponentConfiguration() {
		return null;
	}
}