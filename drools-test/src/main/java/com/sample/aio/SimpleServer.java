package com.sample.aio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.concurrent.ExecutionException;

/**
 * @author zhengbo
 * @date 2016年10月12日
 * 
 */
public class SimpleServer {

	public SimpleServer(int port) throws IOException, InterruptedException {
		final AsynchronousServerSocketChannel listener = AsynchronousServerSocketChannel.open()
				.bind(new InetSocketAddress("192.168.8.9",port));

		listener.accept(null, new CompletionHandler<AsynchronousSocketChannel, Void>() {
			public void completed(AsynchronousSocketChannel ch, Void att) {
				// 接受下一个连接
				listener.accept(null, this);
				// 处理当前连接
				handle(ch);
			}

			public void failed(Throwable exc, Void att) {

			}
		});

		// 主线程继续自己的行为
		while (true) {
			System.out.println("main thread");
			Thread.sleep(1000);
		}
	}

	public void handle(AsynchronousSocketChannel ch) {
		ByteBuffer byteBuffer = ByteBuffer.allocate(32);
		try {
			ch.read(byteBuffer).get();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		byteBuffer.flip();
		System.out.println(byteBuffer.get());
		// Do something
	}

	public static void main(String args[]) throws Exception {
		new SimpleServer(9888);
	}
}