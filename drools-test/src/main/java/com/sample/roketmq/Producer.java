package com.sample.roketmq;

import java.util.concurrent.TimeUnit;

import com.alibaba.rocketmq.client.exception.MQClientException;
import com.alibaba.rocketmq.client.producer.DefaultMQProducer;
import com.alibaba.rocketmq.client.producer.SendResult;
import com.alibaba.rocketmq.common.message.Message;
import com.sample.roketmq.constant.RoketConstant;

//������  
public class Producer {

	public static void main(String[] args) throws MQClientException, InterruptedException {
		/**
		 * һ��Ӧ�ô���һ��Producer����Ӧ����ά���˶��󣬿�������Ϊȫ�ֶ�����ߵ���<br>
		 * ע�⣺ProducerGroupName��Ҫ��Ӧ������֤Ψһ<br>
		 * ProducerGroup����������ͨ����Ϣʱ�����ò��󣬵��Ƿ��ͷֲ�ʽ������Ϣʱ���ȽϹؼ���
		 * ��Ϊ��������ز����Group�µ�����һ��Producer
		 */
		final DefaultMQProducer producer = new DefaultMQProducer("ProducerGroupName");
		// nameserver����,�����;�ֿ�
		producer.setNamesrvAddr(RoketConstant.NAME_SERVER_IP + ":" + RoketConstant.NAME_SERVER_PORT);
		producer.setInstanceName("Producer");

		/**
		 * Producer������ʹ��֮ǰ����Ҫ����start��ʼ������ʼ��һ�μ���<br>
		 * ע�⣺�мǲ�������ÿ�η�����Ϣʱ��������start����
		 */
		producer.start();

		/**
		 * ������δ������һ��Producer������Է��Ͷ��topic�����tag����Ϣ��
		 * ע�⣺send������ͬ�����ã�ֻҪ�����쳣�ͱ�ʶ�ɹ������Ƿ��ͳɹ�Ҳ�ɻ��ж���״̬��<br>
		 * ������Ϣд��Master�ɹ�������Slave���ɹ������������Ϣ���ڳɹ������Ƕ��ڸ���Ӧ���������Ϣ�ɿ���Ҫ�󼫸ߣ�<br>
		 * ��Ҫ������������������⣬��Ϣ���ܻ���ڷ���ʧ�ܵ������ʧ��������Ӧ��������
		 */
		int loopFlag = 1;
		do{
			for (int i = 0; i < 10; i++) {
				try {
					{ // ͨ��topic������Ϣ��tag������Ϣ
						Message msg = new Message("TopicTest1", // topic
								"TagA", // tag ��Ϣ��ǩ��ֻ֧������һ��Tag���������Ϣ����ʹ�ã�
								"OrderID001", // key
								// ��Ϣ�ؼ��ʣ����Key��KEY_SEPARATOR��������ѯ��Ϣʹ�ã�
								("Hello MetaQA").getBytes());// body
						SendResult sendResult = producer.send(msg);
						System.out.println(sendResult);
					}
					
					{
						Message msg = new Message("TopicTest2", // topic
								"TagB", // tag
								"OrderID0034", // key
								("Hello MetaQB").getBytes());// body
						SendResult sendResult = producer.send(msg);
						System.out.println(sendResult);
					}
					
					{
						Message msg = new Message("TopicTest3", // topic
								"TagC", // tag
								"OrderID061", // key
								("Hello MetaQC").getBytes());// body
						SendResult sendResult = producer.send(msg);
						System.out.println(sendResult);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			TimeUnit.MILLISECONDS.sleep(10000);
		}while(loopFlag > 0);

		/**
		 * Ӧ���˳�ʱ��Ҫ����shutdown��������Դ���ر��������ӣ���MetaQ��������ע���Լ�
		 * ע�⣺���ǽ���Ӧ����JBOSS��Tomcat���������˳����������shutdown����
		 */
		// producer.shutdown();
		Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
			public void run() {
				producer.shutdown();
			}
		}));
		System.exit(0);
	}
}