package cn.sumpay.flume.metrics;

import org.apache.flume.instrumentation.MonitoredCounterGroup;

public class SqlSourceCounter extends MonitoredCounterGroup implements
		SqlSourceCounterMBean {
	private static long rows_count;
	private static long rows_proc;
	private static long sendThroughput;
	private static long eventCount;
	private static long last_sent;
	private static long start_time;
	private static final String[] ATTRIBUTES = { "rows_count", "rows_proc",
			"sendThroughput", "eventCount", "last_sent", "start_time" };

	public SqlSourceCounter(String name) {
		super(MonitoredCounterGroup.Type.SOURCE, name, ATTRIBUTES);
		rows_count = 0L;
		rows_proc = 0L;
		sendThroughput = 0L;
		eventCount = 0L;
		last_sent = 0L;
		start_time = System.currentTimeMillis();
	}

	public long getRowsCount() {
		return rows_count;
	}

	public void incrementRowsCount() {
		rows_count += 1L;
	}

	public long getRowsProc() {
		return rows_proc;
	}

	public void incrementRowsProc() {
		rows_proc += 1L;
	}

	public void incrementEventCount() {
		last_sent = System.currentTimeMillis();
		eventCount += 1L;
		if (last_sent - start_time >= 1000L) {
			long secondsElapsed = (last_sent - start_time) / 1000L;
			sendThroughput = eventCount / secondsElapsed;
		}
	}

	public long getEventCount() {
		return eventCount;
	}

	public long getSendThroughput() {
		return sendThroughput;
	}
}