package cn.sumpay.flume.sink;

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

import com.alibaba.rocketmq.client.exception.MQClientException;
import com.alibaba.rocketmq.client.producer.DefaultMQProducer;
import com.alibaba.rocketmq.client.producer.SendResult;
import com.alibaba.rocketmq.common.message.Message;

import cn.sumpay.flume.metrics.SqlSourceCounter;
import cn.sumpay.flume.utils.MQSourceUtils;

public class RocketSink extends AbstractSink implements Configurable {
	private static final Logger log = LoggerFactory.getLogger(RocketSink.class);
	private MQSourceUtils mqSourceUtils;
	private SqlSourceCounter sqlSourceCounter;
	private DefaultMQProducer producer;

	public void configure(Context context) throws ConfigurationException {
		log.info("Reading and processing configuration values for source " + getName());

		this.mqSourceUtils = new MQSourceUtils(context);
		this.producer = new DefaultMQProducer(mqSourceUtils.getProducerGroup());
		this.sqlSourceCounter = new SqlSourceCounter("SOURCESQL." + getName());
		this.sqlSourceCounter.start();

		// nameserver服务,多个以;分开
		producer.setNamesrvAddr(mqSourceUtils.getNamesrvAddr());
		producer.setInstanceName(mqSourceUtils.getInstanceName());
		try {
			producer.start();
		} catch (MQClientException e) {
			e.printStackTrace();
		}
	}

	@Override
	public Sink.Status process() throws EventDeliveryException {
		Transaction tx = getChannel().getTransaction();
		try {
			tx.begin();
			Event e = getChannel().take();
			while (e != null) {
				try {
					String data = new String(e.getBody());
					log.info("event from channel :" + data);

					Message msg = new Message(mqSourceUtils.getTopic(), mqSourceUtils.getTags(),
							System.currentTimeMillis() + "", data.getBytes());
					SendResult sendResult = producer.send(msg);
					log.info("event send result for mq :" + sendResult);
				} catch (Exception ex) {
					throw ex;
				}
				e = getChannel().take();
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
		log.info("Starting rocket sink {} ...", getName());
		super.start();
	}

	public void stop() {
		log.info("Stopping rocket sink {} ...", getName());
		super.stop();
	}
}