package cn.sumpay.flume.utils;

import org.apache.flume.Context;
import org.apache.flume.conf.ConfigurationException;

public class MQSourceUtils {
	private String namesrvAddr;
	private String producerGroup;
	private String instanceName;
	private String topic;
	private String tags;

	public MQSourceUtils(Context context) throws ConfigurationException {
		this.namesrvAddr = context.getString("namesrv.addr");
		this.producerGroup = context.getString("producer.group","RiskControlGroup");
		this.instanceName = context.getString("instance.name","RiskProducer");
		this.topic = context.getString("topic","RiskTopic");
		this.tags = context.getString("tags","RiskTags");

		checkMandatoryProperties();
	}

	private void checkMandatoryProperties() throws ConfigurationException {
		if (getNamesrvAddr() == null) {
			throw new ConfigurationException("namesrv.addr property not set");
		}
		if (getProducerGroup() == null) {
			throw new ConfigurationException("producer.group property not set");
		}
		if (getInstanceName() == null) {
			throw new ConfigurationException("instance.name property not set");
		}
		if (getTopic() == null) {
			throw new ConfigurationException("topic property not set");
		}
		if (getTags() == null) {
			throw new ConfigurationException("tags property not set");
		}
	}

	public String getNamesrvAddr() {
		return namesrvAddr;
	}

	public void setNamesrvAddr(String namesrvAddr) {
		this.namesrvAddr = namesrvAddr;
	}

	public String getProducerGroup() {
		return producerGroup;
	}

	public void setProducerGroup(String producerGroup) {
		this.producerGroup = producerGroup;
	}

	public String getInstanceName() {
		return instanceName;
	}

	public void setInstanceName(String instanceName) {
		this.instanceName = instanceName;
	}

	public String getTopic() {
		return topic;
	}

	public void setTopic(String topic) {
		this.topic = topic;
	}

	public String getTags() {
		return tags;
	}

	public void setTags(String tags) {
		this.tags = tags;
	}
}