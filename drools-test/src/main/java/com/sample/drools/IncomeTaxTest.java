package com.sample.drools;

import java.text.DecimalFormat;
import java.util.Random;

import org.kie.api.KieServices;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;

import com.sample.drools.bean.Income;

/**
 * @author zhengbo
 * @date 2016��9��21��
 * 
 */
public class IncomeTaxTest {
	public static final void main(String[] args) {
		// load up the knowledge base
		KieServices ks = KieServices.Factory.get();
		KieContainer kContainer = ks.getKieClasspathContainer();
		KieSession kSession = kContainer.newKieSession("ksession-incometax");
		try {
			// go !
			long startTime = System.currentTimeMillis();
			Random random = new Random();
			DecimalFormat df = new DecimalFormat("#.00");
			for (int i = 0; i < 100; i++) {
//				System.out.print("Ա�� " + i + " ���¹��ʵ���");
				Income income = new Income();
				int rate = random.nextInt(10) + 1;
				income.setIncome(Double.valueOf(df.format(10000 * rate * random.nextDouble())));
				kSession.insert(income);
				kSession.fireAllRules();

//				System.out.println("˰ǰ����=" + df.format(income.getIncome()) + "; Ӧ��˰��=" + df.format(income.getTax())
//						+ "; ˰����=" + df.format(income.getTaxed()));
			}
			System.out.println(System.currentTimeMillis() - startTime + "ms");
		} catch (Throwable t) {
			t.printStackTrace();
		} finally {
			if (kSession != null)
				kSession.dispose();
		}
	}
}