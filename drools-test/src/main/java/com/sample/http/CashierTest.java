package com.sample.http;

import java.util.HashMap;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CashierTest {

	private static String HOST = "http://101.71.243.74";
	private static String PORT = "8891";

	public static void main(String[] args) {
		// 设置线程数
		int threadNum = 4;
		if (args.length > 1 && args[1] != null) {
			threadNum = Integer.parseInt(args[1]);
		}
		// 创建线程池
		ExecutorService fixedThreadPool = Executors.newFixedThreadPool(threadNum);
		// 设置执行次数
		long doCount = 0;
		boolean isForever = false;
		if (args.length > 2 && args[2] != null) {
			doCount = Integer.parseInt(args[2]) * threadNum;
		}else{
			doCount = 1;
			isForever = true;
		}
		HttpClient hClient = new HttpClient();
		Random rd = new Random();
		do {
			fixedThreadPool.execute(new Runnable() {
				@Override
				public void run() {
					String uri = "/cashier/openpay/judgeCard";
					StringBuffer url = new StringBuffer("");
					url.append(HOST);
					url.append(":");
					url.append(PORT);
					url.append("/");
					url.append(uri);
					
					HashMap<String, String> paramsMap = new HashMap<String, String>();
					paramsMap.put("cardNo", "6222085234567136");
					paramsMap.put("businessCode", "03");
					paramsMap.put("key", args[0]);
					hClient.post(url.toString(), paramsMap);
					
					paramsMap = null;
					uri = null;
					url = null;
				}
			});
			
			try {
				Thread.sleep(rd.nextInt(1000));
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
			if (!isForever){
				doCount--;
			}
		} while (doCount > 0);
		
		fixedThreadPool.shutdown();
	}
}