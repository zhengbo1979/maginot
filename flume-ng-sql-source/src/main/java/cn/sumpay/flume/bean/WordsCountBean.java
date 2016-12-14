package cn.sumpay.flume.bean;

public class WordsCountBean extends BaseBean {
	private String word;
	private long count;
	private String spoutThreadName;
	private String spoutIp;
	private String splitThreadName;
	private String splitIp;
	private String countThreadName;
	private String countIp;
	private String threadName;
	private String clientIp;
	private String createdAt;

	public String getWord() {
		return word;
	}

	public void setWord(String word) {
		this.word = word;
	}

	public long getCount() {
		return count;
	}

	public void setCount(long count) {
		this.count = count;
	}

	public String getSpoutThreadName() {
		return spoutThreadName;
	}

	public void setSpoutThreadName(String spoutThreadName) {
		this.spoutThreadName = spoutThreadName;
	}

	public String getSpoutIp() {
		return spoutIp;
	}

	public void setSpoutIp(String spoutIp) {
		this.spoutIp = spoutIp;
	}

	public String getSplitThreadName() {
		return splitThreadName;
	}

	public void setSplitThreadName(String splitThreadName) {
		this.splitThreadName = splitThreadName;
	}

	public String getSplitIp() {
		return splitIp;
	}

	public void setSplitIp(String splitIp) {
		this.splitIp = splitIp;
	}

	public String getCountThreadName() {
		return countThreadName;
	}

	public void setCountThreadName(String countThreadName) {
		this.countThreadName = countThreadName;
	}

	public String getCountIp() {
		return countIp;
	}

	public void setCountIp(String countIp) {
		this.countIp = countIp;
	}

	public String getThreadName() {
		return threadName;
	}

	public void setThreadName(String threadName) {
		this.threadName = threadName;
	}

	public String getClientIp() {
		return clientIp;
	}

	public void setClientIp(String clientIp) {
		this.clientIp = clientIp;
	}

	public String getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(String createdAt) {
		this.createdAt = createdAt;
	}
}
