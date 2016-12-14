package com.sample.drools.bean;

public class Message {
	public static final int HELLO = 0;
	public static final int GOODBYE = 1;

	private String message;

	private int flag;

	private int status;

	public String getMessage() {
		return this.message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public int getFlag() {
		return flag;
	}

	public void setFlag(int flag) {
		this.flag = flag;
	}

	public int getStatus() {
		return this.status;
	}

	public void setStatus(int status) {
		this.status = status;
	}
}
