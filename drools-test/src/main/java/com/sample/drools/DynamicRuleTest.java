package com.sample.drools;

import java.io.UnsupportedEncodingException;
import java.text.DecimalFormat;
import java.util.Random;

import org.drools.KnowledgeBase;
import org.drools.KnowledgeBaseFactory;
import org.drools.builder.KnowledgeBuilder;
import org.drools.builder.KnowledgeBuilderError;
import org.drools.builder.KnowledgeBuilderErrors;
import org.drools.builder.KnowledgeBuilderFactory;
import org.drools.builder.ResourceType;
import org.drools.io.ResourceFactory;
import org.drools.runtime.StatefulKnowledgeSession;

import com.sample.drools.bean.Income;

/**
 * @author zhengbo
 * @date 2016年9月23日
 * 
 */
public class DynamicRuleTest {

	public static void main(String[] args) {
		StatefulKnowledgeSession kSession = null;
		try {
			KnowledgeBuilder kb = KnowledgeBuilderFactory.newKnowledgeBuilder();
			// 装入规则，可以装入多个
			kb.add(ResourceFactory.newByteArrayResource(getRules().getBytes("utf-8")), ResourceType.DRL);

			KnowledgeBuilderErrors errors = kb.getErrors();
			for (KnowledgeBuilderError error : errors) {
				System.out.println(error);
			}
			KnowledgeBase kBase = KnowledgeBaseFactory.newKnowledgeBase();
			kBase.addKnowledgePackages(kb.getKnowledgePackages());

			kSession = kBase.newStatefulKnowledgeSession();

			// go !
			long startTime = System.currentTimeMillis();
			Random random = new Random();
			DecimalFormat df = new DecimalFormat("#.00");
			for (int i = 0; i < 100; i++) {
				// System.out.print("员工 " + i + " 本月工资单：");
				
				long startTime1 = System.currentTimeMillis();
				Income income = new Income();
				int rate = random.nextInt(10) + 1;
				income.setIncome(Double.valueOf(df.format(10000 * rate * random.nextDouble())));
				kSession.insert(income);
				kSession.fireAllRules();
				System.out.println("startTime1:" + (System.currentTimeMillis() - startTime1) + "ms");
//				 System.out.println("税前工资=" + df.format(income.getIncome()) +
//				 "; 应缴税款=" + df.format(income.getTax())
//				 + "; 税后工资=" + df.format(income.getTaxed()));
			}
			System.out.println(System.currentTimeMillis() - startTime + "ms");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} finally {
			if (kSession != null)
				kSession.dispose();
		}
	}

	private static String getRules() {
		StringBuffer rules = new StringBuffer();
		rules.append("package com.sample.drools\r\n");
		rules.append("import com.sample.drools.bean.Income;\r\n");

		rules.append("function int getStart(){\r\n");
		rules.append("\treturn 3500;\r\n");
		rules.append("}\r\n");

		rules.append("rule \"under_1500\"\r\n");
		rules.append("\tsalience 70\r\n");
		rules.append("\twhen\r\n");
		rules.append("\t\tm : Income( income > getStart() , income <= getStart()+1500 )\r\n");
		rules.append("\tthen\r\n");
		rules.append("\t\tm.setTax( (m.getIncome() - getStart())* 0.03 );\r\n");
		rules.append("\t\tm.setTaxed( m.getIncome() - m.getTax() );\r\n");
		rules.append("end\r\n");

		rules.append("rule \"under_4500\"\r\n");
		rules.append("\tsalience 60\r\n");
		rules.append("\twhen\r\n");
		rules.append("\t\tm : Income( income > getStart()+1500 , income <= getStart()+4500 )\r\n");
		rules.append("\tthen\r\n");
		rules.append("\t\tm.setTax( (m.getIncome() - getStart())* 0.10 - 105 );\r\n");
		rules.append("\t\tm.setTaxed( m.getIncome() - m.getTax() );\r\n");
		rules.append("end\r\n");

		rules.append("rule \"under_9000\"\r\n");
		rules.append("\tsalience 50\r\n");
		rules.append("\twhen\r\n");
		rules.append("\t\tm : Income( income > getStart()+4500 , income <= getStart()+9000 )\r\n");
		rules.append("\tthen\r\n");
		rules.append("\t\tm.setTax( (m.getIncome() - getStart())* 0.20 - 555 );\r\n");
		rules.append("\t\tm.setTaxed( m.getIncome() - m.getTax() );\r\n");
		rules.append("end\r\n");

		rules.append("rule \"under_35000\"\r\n");
		rules.append("\tsalience 40\r\n");
		rules.append("\twhen\r\n");
		rules.append("\t\tm : Income( income > getStart()+9000 , income <= getStart()+35000 )\r\n");
		rules.append("\tthen\r\n");
		rules.append("\t\tm.setTax( (m.getIncome() - getStart())* 0.25 - 1005 );\r\n");
		rules.append("\t\tm.setTaxed( m.getIncome() - m.getTax() );\r\n");
		rules.append("end\r\n");

		rules.append("rule \"under_55000\"\r\n");
		rules.append("\tsalience 30\r\n");
		rules.append("\twhen\r\n");
		rules.append("\t\tm : Income( income > getStart()+35000 , income <= getStart()+55000 )\r\n");
		rules.append("\tthen\r\n");
		rules.append("\t\tm.setTax( (m.getIncome() - getStart())* 0.30 - 2755 );\r\n");
		rules.append("\t\tm.setTaxed( m.getIncome() - m.getTax() );\r\n");
		rules.append("end\r\n");

		rules.append("rule \"under_80000\"\r\n");
		rules.append("\tsalience 20\r\n");
		rules.append("\twhen\r\n");
		rules.append("\t\tm : Income( income > getStart()+55000 , income <= getStart()+80000 )\r\n");
		rules.append("\tthen\r\n");
		rules.append("\t\tm.setTax( (m.getIncome() - getStart())* 0.35 - 5505 );\r\n");
		rules.append("\t\tm.setTaxed( m.getIncome() - m.getTax() );\r\n");
		rules.append("end\r\n");

		rules.append("rule \"over_80000\"\r\n");
		rules.append("\tsalience 10\r\n");
		rules.append("\twhen\r\n");
		rules.append("\t\tm : Income( income > getStart()+80000 )\r\n");
		rules.append("\tthen\r\n");
		rules.append("\t\tm.setTax( (m.getIncome() - getStart())* 0.45 - 13505 );\r\n");
		rules.append("\t\tm.setTaxed( m.getIncome() - m.getTax() );\r\n");
		rules.append("end\r\n");

		return rules.toString();
	}
}
