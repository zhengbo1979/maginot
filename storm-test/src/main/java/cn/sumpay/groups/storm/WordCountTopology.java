package cn.sumpay.groups.storm;

import org.apache.storm.Config;
import org.apache.storm.LocalCluster;
import org.apache.storm.StormSubmitter;
import org.apache.storm.topology.TopologyBuilder;
import org.apache.storm.tuple.Fields;

import cn.sumpay.groups.storm.bolt.MongoDbBolt;
import cn.sumpay.groups.storm.bolt.SplitSentenceBolt;
import cn.sumpay.groups.storm.bolt.WordCountBolt;
import cn.sumpay.groups.storm.spout.RandomSentenceSpout;

/**
 * This topology demonstrates Storm's stream groupings and multilang
 * capabilities.
 */
public class WordCountTopology {

	public static void main(String[] args) throws Exception {
		int numWorkers = 3;
		try {
			numWorkers = Integer.parseInt(args[1]);
		} catch (Exception e) {
			numWorkers = 3;
		}

		TopologyBuilder builder = new TopologyBuilder();

		builder.setSpout("spout", new RandomSentenceSpout(), 2 * numWorkers);

		builder.setBolt("split", new SplitSentenceBolt(), 4 * numWorkers)
				.shuffleGrouping("spout");
		builder.setBolt("count", new WordCountBolt(), 4 * numWorkers)
				.fieldsGrouping("split", new Fields("word"));
		// fieldsGrouping 按字段分组 ：new Fields("word","count") 按word和count分组
		// new Fields("word") 只按word分组
//		builder.setBolt("Mysql", new MySQLBolt(), 8 * numWorkers)
//				.fieldsGrouping("count", new Fields("word", "count"));
		builder.setBolt("MongoDb", new MongoDbBolt(), 8 * numWorkers)
				.fieldsGrouping("count", new Fields("word", "count"));

		Config conf = new Config();
		conf.setDebug(true);

		if (args != null && args.length > 0) {
			conf.setNumWorkers(numWorkers);
			StormSubmitter.submitTopologyWithProgressBar(args[0], conf,
					builder.createTopology());
		} else {
			conf.setMaxTaskParallelism(3);

			LocalCluster cluster = new LocalCluster();
			cluster.submitTopology("word-count", conf, builder.createTopology());
			
			Thread.sleep(1800000);

			cluster.shutdown();
		}
	}
}