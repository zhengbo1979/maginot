package com.sample.http;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.apache.xmlbeans.impl.regex.ParseException;

public class HttpClient {
	/**
	 * 发送 post请求访问本地应用并根据传递参数不同返回不同结果
	 */
	public void post(String url, Map<String, String> paramMap) {
		// 创建默认的httpClient实例.
		CloseableHttpClient httpclient = HttpClients.createDefault();
		// 创建httppost
		HttpPost httppost = new HttpPost(url);
		httppost.addHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.99 Safari/537.36");
		httppost.addHeader("Accept", "application/json, text/javascript, */*; q=0.01");
		httppost.addHeader("Accept-Language", "zh-CN,zh;q=0.8");
		httppost.addHeader("Accept-Encoding", "gzip, deflate");
		httppost.addHeader("Referer", "http://101.71.243.74:8891/cashier/openpay/cashierCenter.html?key=b4628a33-6483-446c-bad1-a52b48a8f168");
		
		// 创建参数队列
		List<NameValuePair> formparams = new ArrayList<NameValuePair>();
		for (Map.Entry<String, String> param : paramMap.entrySet()) {
			formparams.add(new BasicNameValuePair(param.getKey(), param
					.getValue()));
		}
		UrlEncodedFormEntity uefEntity;
		try {
			uefEntity = new UrlEncodedFormEntity(formparams, "UTF-8");
			httppost.setEntity(uefEntity);
			System.out.println("executing request " + httppost.getURI());
			CloseableHttpResponse response = httpclient.execute(httppost);
			try {
				HttpEntity entity = response.getEntity();
				if (entity != null) {
					System.out
							.println("--------------------------------------");
					System.out.println("Response content: "
							+ EntityUtils.toString(entity, "UTF-8"));
					System.out
							.println("--------------------------------------");
				}
			} finally {
				response.close();
			}
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			// 关闭连接,释放资源
			try {
				httpclient.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 发送 get请求
	 */
	public void get(String url) {
		CloseableHttpClient httpclient = HttpClients.createDefault();
		try {
			// 创建httpget.
			HttpGet httpget = new HttpGet(url);
			System.out.println("executing request " + httpget.getURI());
			// 执行get请求.
			CloseableHttpResponse response = httpclient.execute(httpget);
			try {
				// 获取响应实体
				HttpEntity entity = response.getEntity();
				System.out.println("--------------------------------------");
				// 打印响应状态
				System.out.println(response.getStatusLine());
				if (entity != null) {
					// 打印响应内容长度
					System.out.println("Response content length: "
							+ entity.getContentLength());
					// 打印响应内容
					System.out.println("Response content: "
							+ EntityUtils.toString(entity));
				}
				System.out.println("------------------------------------");
			} finally {
				response.close();
			}
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			// 关闭连接,释放资源
			try {
				httpclient.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public static void main(String args[]) throws InterruptedException {
		do{
			HttpClient hClient = new HttpClient();
			
			// hClient.get("http://www.cuncunle.com");
			int targetId = 47;
			StringBuffer ids = new StringBuffer("");
			Random rd = new Random();
			for (int i = 0; i < 9; i++) {
				int id = rd.nextInt(100);
				ids.append(id+",");
			}
			ids.append(targetId);
			
			HashMap<String, String> paramsMap = new HashMap<String, String>();
			paramsMap.put("ids", ids.toString());
			hClient.post("http://180.96.27.74:8099/vote5/web/vote", paramsMap);
			
			Thread.sleep(5000);
		}while(true);
	}
}