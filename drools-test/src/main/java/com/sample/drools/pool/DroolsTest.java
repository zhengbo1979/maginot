package com.sample.drools.pool;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.kie.api.KieServices;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;

import com.sample.drools.bean.Message;
import com.sample.roketmq.constant.RoketConstant;

/**
 * This is a sample class to launch a rule.
 */
public class DroolsTest {
	private static int THREAD_COUNT = 1000;
	private int thread_ok_number = 0;

	public void callback(long thread_ok_number, long startTime) {
		this.thread_ok_number++;
		if (this.thread_ok_number == THREAD_COUNT) {
			System.out.println("all thread is ok , in:" + (System.currentTimeMillis() - startTime) + "ms");
		}
	}

	public static final void main(String[] args) {
		try {
			// load up the knowledge base
			KieServices ks = KieServices.Factory.get();
			KieContainer kContainer = ks.getKieClasspathContainer();
			KieSession kSession = kContainer.newKieSession("ksession-rules");
			// 创建线程池
			ExecutorService fixedThreadPool = Executors.newFixedThreadPool(RoketConstant.THREAD_POOL_COUNT);
			DroolsTest dt = new DroolsTest();

			// go !
			long startTime = System.currentTimeMillis();
			for (int i = 0; i < THREAD_COUNT; i++) {
				fixedThreadPool.execute(new DroolsTestThread(dt, startTime, kSession, i));
			}
		} catch (Throwable t) {
			t.printStackTrace();
		}
	}
}

class DroolsTestThread implements Runnable {
	private DroolsTest dt;
	private long startTime;
	private KieSession kSession;
	private int num;

	public DroolsTestThread(DroolsTest dt, Long startTime, KieSession kSession, int num) {
		this.dt = dt;
		this.startTime = startTime;
		this.kSession = kSession;
		this.num = num;
	}

	public void run() {
		// System.out.println("******************** " + num + "
		// ********************");
		Message message = new Message();
		message.setMessage("杭州市Hello World i=" + num);
		message.setFlag(num % 2);
		message.setStatus(Message.HELLO);
		kSession.insert(message);
		kSession.fireAllRules();

		dt.callback(num, startTime);
	}
}