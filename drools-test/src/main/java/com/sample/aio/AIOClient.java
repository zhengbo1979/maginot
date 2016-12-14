package com.sample.aio;

/**
 * @author zhengbo
 * @date 2016Äê10ÔÂ12ÈÕ
 * 
 */
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;

public class AIOClient {

	public static void main(String... args) throws Exception {
		for (int i=0;i<10;i++){
			AsynchronousSocketChannel client = AsynchronousSocketChannel.open();
			client.connect(new InetSocketAddress("192.168.8.9", 9888));
			client.write(ByteBuffer.wrap("test".getBytes())).get();
			client.close();
			Thread.sleep(2000);
		}
	}
}