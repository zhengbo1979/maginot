package com.sample.esper;

import com.espertech.esper.client.EPAdministrator;
import com.espertech.esper.client.EPRuntime;
import com.espertech.esper.client.EPServiceProvider;
import com.espertech.esper.client.EPServiceProviderManager;
import com.espertech.esper.client.EPStatement;
import com.espertech.esper.client.EventBean;
import com.espertech.esper.client.UpdateListener;

/**
 * @author zhengbo
 * @date 2016年10月9日
 * 
 */
class Apple {
	private int id;
	private int price;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getPrice() {
		return price;
	}

	public void setPrice(int price) {
		this.price = price;
	}
}

class AvgListener implements UpdateListener {
	public void update(EventBean[] newEvents, EventBean[] oldEvents) {
		if (newEvents != null) {
			for (EventBean eventBean : newEvents) {
				Double avg = (Double) eventBean.get("avg(price)");
				System.out.println("Apple's average price is " + avg);
			}
		}
	}
}

class SumListener implements UpdateListener {
	public void update(EventBean[] newEvents, EventBean[] oldEvents) {
		if (newEvents != null) {
			for (EventBean eventBean : newEvents) {
				int sum = (int) eventBean.get("sum(price)");
				System.out.println("Apple's sum price is " + sum);
			}
		}
	}
}

class TotalListener implements UpdateListener {
	public void update(EventBean[] newEvents, EventBean[] oldEvents) {
		if (newEvents != null) {
			for (EventBean eventBean : newEvents) {
				Double total = (Double) eventBean.get("total");
				System.out.println("Apple's total price is " + total);
			}
		}
	}
}

class AllListener implements UpdateListener {
	public void update(EventBean[] newEvents, EventBean[] oldEvents) {
		if (newEvents != null) {
			for (EventBean eventBean : newEvents) {
				int price = (int) eventBean.get("price");
				System.out.println("Apple's price is " + price);
			}
		}
	}
}

public class EsperPojoTest {
	public static void main(String[] args) throws InterruptedException {
		EPServiceProvider epService = EPServiceProviderManager
				.getDefaultProvider();
		EPAdministrator admin = epService.getEPAdministrator();

		String product = Apple.class.getName();
		String epl1 = "select sum(price),avg(price) from " + product
				+ ".win:length_batch(3)";
		String epl2 = "select total from " + product
				+ ".win:length_batch(9).stat:uni(price)";
		String epl3 = "select id,price from " + product
				+ ".win:length_batch(3) where price >=6";

		EPStatement state = admin.createEPL(epl1);

		state.addListener(new AvgListener());
		state.addListener(new SumListener());

		state = admin.createEPL(epl2);
		state.addListener(new TotalListener());

		state = admin.createEPL(epl3);
		state.addListener(new AllListener());

		EPRuntime runtime = epService.getEPRuntime();

		Apple apple1 = new Apple();
		apple1.setId(1);
		apple1.setPrice(5);
		runtime.sendEvent(apple1);

		Apple apple2 = new Apple();
		apple2.setId(2);
		apple2.setPrice(7);
		runtime.sendEvent(apple2);

		Apple apple3 = new Apple();
		apple3.setId(3);
		apple3.setPrice(6);
		runtime.sendEvent(apple3);

		Apple apple4 = new Apple();
		apple4.setId(4);
		apple4.setPrice(8);
		runtime.sendEvent(apple4);

		// admin.destroyAllStatements();// 需要多epl并存可以不用这句：destroyAllStatements
		// state = admin.createEPL(epl3);
		// state.addListener(new AvgListener());
		// state.addListener(new SumListener());
		// state.addListener(new TotalListener());
		// state.addListener(new AllListener());

		Apple apple5 = new Apple();
		apple5.setId(1);
		apple5.setPrice(9);
		runtime.sendEvent(apple5);

		Apple apple6 = new Apple();
		apple6.setId(2);
		apple6.setPrice(10);
		runtime.sendEvent(apple6);

		Apple apple7 = new Apple();
		apple7.setId(3);
		apple7.setPrice(11);
		runtime.sendEvent(apple7);

		Apple apple8 = new Apple();
		apple8.setId(4);
		apple8.setPrice(12);
		runtime.sendEvent(apple8);
		
		Apple apple9 = new Apple();
		apple9.setId(4);
		apple9.setPrice(13);
		runtime.sendEvent(apple9);
	}
}
