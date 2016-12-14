package cn.sumpay.flume.metrics;

public abstract interface SqlSourceCounterMBean {
	public abstract long getRowsCount();

	public abstract void incrementRowsCount();

	public abstract long getRowsProc();

	public abstract void incrementRowsProc();

	public abstract long getEventCount();

	public abstract void incrementEventCount();

	public abstract long getSendThroughput();
}