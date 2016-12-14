package com.sample.esper;

import java.util.HashMap;
import java.util.Map;

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

class AvgMapListener implements UpdateListener {
	public void update(EventBean[] newEvents, EventBean[] oldEvents) {
		if (newEvents != null) {
			for (EventBean eventBean : newEvents){
				Double avg = (Double) eventBean.get("avg(age)");
				System.out.println("Person's average age is " + avg);
			}
		}
	}
}

class SumMapListener implements UpdateListener {
	public void update(EventBean[] newEvents, EventBean[] oldEvents) {
		if (newEvents != null) {
			for (EventBean eventBean : newEvents){
				int sum = (int) eventBean.get("sum(age)");
				System.out.println("Person's sum age is " + sum);
			}
		}
	}
}

class TotalMapListener implements UpdateListener {
	public void update(EventBean[] newEvents, EventBean[] oldEvents) {
		if (newEvents != null) {
			for (EventBean eventBean : newEvents){
				Double total = (Double) eventBean.get("total");
				System.out.println("Person's total age is " + total);
			}
		}
	}
}

class AllMapListener implements UpdateListener {
	public void update(EventBean[] newEvents, EventBean[] oldEvents) {
		if (newEvents != null) {
			for (EventBean eventBean : newEvents){
				int price = (int) eventBean.get("age");
				System.out.println("Person's age is " + price);
			}
		}
	}
}

public class EsperMapTest {
	public static void main(String[] args) throws InterruptedException {
		EPServiceProvider epService = EPServiceProviderManager.getDefaultProvider();
		EPAdministrator admin = epService.getEPAdministrator();

		// Person定义  
        Map<String,Object> person = new HashMap<String,Object>();
        person.put("name", String.class);
        person.put("age", int.class);
          
        // 注册Person到Esper  
        admin.getConfiguration().addEventType("Person", person);
        
		String epl = "select sum(age),avg(age) from Person.win:length_batch(4) where age>30";
//		String epl = "select total from " + product + ".win:length_batch(3).stat:uni(age)";
//		String epl = "select name,age from " + product + ".win:length_batch(4) where age >=6";

		EPStatement state = admin.createEPL(epl);
		state.addListener(new AvgMapListener());
		state.addListener(new SumMapListener());
		state.addListener(new TotalMapListener());
		state.addListener(new AllMapListener());
		
		EPRuntime runtime = epService.getEPRuntime();

		Map<String,Object> person1 = new HashMap<String,Object>();
		person1.put("name", "zhengbo1");
		person1.put("age", 15);
		runtime.sendEvent(person1, "Person");

		Map<String,Object> person2 = new HashMap<String,Object>();
		person2.put("name", "zhengbo2");
		person2.put("age", 25);
		runtime.sendEvent(person2, "Person");

		Map<String,Object> person3 = new HashMap<String,Object>();
		person3.put("name", "zhengbo3");
		person3.put("age", 35);
		runtime.sendEvent(person3, "Person");
		
		Map<String,Object> person4 = new HashMap<String,Object>();
		person4.put("name", "zhengbo4");
		person4.put("age", 45);
		runtime.sendEvent(person4, "Person");
		
		Map<String,Object> person5 = new HashMap<String,Object>();
		person5.put("name", "zhengbo5");
		person5.put("age", 55);
		runtime.sendEvent(person5, "Person");
		
		Map<String,Object> person6 = new HashMap<String,Object>();
		person6.put("name", "zhengbo6");
		person6.put("age", 65);
		runtime.sendEvent(person6, "Person");
	}
}
