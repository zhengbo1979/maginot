package com.sample.roketmq;

import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Date;
/**
 * @author zhengbo
 * @date 2016��9��28��
 * 
 */
import java.util.List;

import com.alibaba.rocketmq.client.consumer.DefaultMQPushConsumer;
import com.alibaba.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import com.alibaba.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import com.alibaba.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import com.alibaba.rocketmq.client.exception.MQClientException;
import com.alibaba.rocketmq.common.message.MessageExt;
import com.sample.roketmq.constant.RoketConstant;

//������ push  
public class Consumer {
	private static long msgCount = 0;

	/**
	 * ��ǰ������Consumer�÷���ʹ�÷�ʽ���û��о�����Ϣ��RocketMQ�������Ƶ���Ӧ�ÿͻ��ˡ�<br>
	 * ����ʵ��Consumer�ڲ���ʹ�ó���ѯPull��ʽ��MetaQ����������Ϣ��Ȼ���ٻص��û�Listener����<br>
	 * 
	 * @throws MQClientException
	 */
	public static void main(String[] args) throws MQClientException {
		/**
		 * һ��Ӧ�ô���һ��Consumer����Ӧ����ά���˶��󣬿�������Ϊȫ�ֶ�����ߵ���<br>
		 * ע�⣺ConsumerGroupName��Ҫ��Ӧ������֤Ψһ
		 * ,���ʹ�÷���İ�������ͬһ����,һ��Consumer���ϵ����ƣ�����Consumerͨ������һ����Ϣ���������߼�һ��
		 * PushConsumer��Consumer��һ�֣�Ӧ��ͨ����Consumerע��һ��Listener��������
		 * Consumer�յ���Ϣ���̻ص�Listener������
		 * PullConsumer��Consumer��һ�֣�Ӧ��ͨ����������Consumer����ȡ��Ϣ������Broker����Ϣ������Ȩ��Ӧ�ÿ���
		 */
		DefaultMQPushConsumer consumer = new DefaultMQPushConsumer("RiskControlGroup");
		// //nameserver����
		consumer.setNamesrvAddr(RoketConstant.NAME_SERVER_IP + ":" + RoketConstant.NAME_SERVER_PORT);
		consumer.setInstanceName("RiskConsumber");
		// �����������Ѹ���
		// consumer.setConsumeMessageBatchMaxSize(10);

		/**
		 * ����ָ��topic��tags�ֱ����TagA��TagC��TagD
		 */
		// consumer.subscribe("TopicTest1", "TagA || TagC || TagD");
		/**
		 * ����ָ��topic��������Ϣ<br>
		 * ע�⣺һ��consumer������Զ��Ķ��topic
		 * 
		 */
		// consumer.subscribe("TopicTest2", "*");

		consumer.subscribe("RiskTopic", "*");

		consumer.registerMessageListener(new MessageListenerConcurrently() {
			public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> msgs, ConsumeConcurrentlyContext context) {
				/// ������Ϣ����msgs.size()
				System.out.println(Thread.currentThread().getName() + " Receive New Messages: " + msgs.size()
						+ " ,total msg count : " + (++msgCount));
				MessageExt msg = msgs.get(0);
				if (msg.getTopic().equals("TopicTest1")) {
					// ִ��TopicTest1�������߼�
					if (msg.getTags() != null && msg.getTags().equals("TagA")) {
						// ִ��TagA������
						System.out.println(msg.getTopic() + " " + msg.getTags() + new String(msg.getBody()));
					} else if (msg.getTags() != null && msg.getTags().equals("TagC")) {
						// ִ��TagC������
						System.out.println(msg.getTopic() + " " + msg.getTags() + new String(msg.getBody()));
					} else if (msg.getTags() != null && msg.getTags().equals("TagD")) {
						// ִ��TagD������
						System.out.println(msg.getTopic() + " " + msg.getTags() + new String(msg.getBody()));
					}
				} else if (msg.getTopic().equals("TopicTest2")) {
					System.out.println(msg.getTopic() + " " + msg.getTags() + new String(msg.getBody()));
				} else if (msg.getTopic().equals("RiskTopic")) {
					try {
						System.out.println(new SimpleDateFormat("YYYY-MM-dd HH:mm:ss,SSS").format(new Date()) + " RiskTopic recive msg:" + new String(msg.getBody(), "UTF-8"));
					} catch (UnsupportedEncodingException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
			}
		});

		/**
		 * Consumer������ʹ��֮ǰ����Ҫ����start��ʼ������ʼ��һ�μ���<br>
		 */
		consumer.start();
		System.out.println("ConsumerStarted.");
	}
}