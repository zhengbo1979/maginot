package com.sample.drools;

import java.text.DecimalFormat;
import java.util.Random;

/**
 * @author zhengbo
 * @date 2016年9月21日
 * 
 */
public class IncomeTaxCommonTest {
	public static final void main(String[] args) {
		try {
			// go !
			long startTime = System.currentTimeMillis();
			Random random = new Random();
			DecimalFormat df = new DecimalFormat("#.00");
			for (int i = 0; i < 100; i++) {
//				System.out.print("员工 " + i + " 本月工资单：");
				Income income = new Income();
				int rate = random.nextInt(10) + 1;
				income.setIncome(Double.valueOf(df.format(10000 * rate * random.nextDouble())));
				getTax(income);
//				System.out.println("税前工资=" + df.format(income.getIncome()) + "; 应缴税款=" + df.format(income.getTax())
//						+ "; 税后工资=" + df.format(income.getTaxed()));
			}
			System.out.println(System.currentTimeMillis() - startTime + "ms");
		} catch (Throwable t) {
			t.printStackTrace();
		}
	}

	private static int getStart() {
		return 3500;
	}

	private static void getTax(Income income) {
		if (income.getIncome() > getStart() && income.getIncome() <= getStart() + 1500) {
			income.setTax((income.getIncome() - getStart()) * 0.03);
			income.setTaxed(income.getIncome() - income.getTax());
		}
		if (income.getIncome() > getStart()+1500 && income.getIncome() <= getStart()+4500) {
			income.setTax( (income.getIncome() - getStart())* 0.10 - 105 );
			income.setTaxed( income.getIncome() - income.getTax() );
		}
		if (income.getIncome() > getStart()+4500 && income.getIncome() <= getStart()+9000) {
			income.setTax( (income.getIncome() - getStart())* 0.20 - 555 );
			income.setTaxed( income.getIncome() - income.getTax() );
		}
		if (income.getIncome() > getStart()+9000 && income.getIncome() <= getStart()+35000) {
			income.setTax( (income.getIncome() - getStart())* 0.25 - 1005 );
			income.setTaxed( income.getIncome() - income.getTax() );
		}
		if (income.getIncome() > getStart()+35000 && income.getIncome() <= getStart()+55000) {
			income.setTax( (income.getIncome() - getStart())* 0.30 - 2755 );
			income.setTaxed( income.getIncome() - income.getTax() );
		}
		if (income.getIncome() > getStart()+55000 && income.getIncome() <= getStart()+80000) {
			income.setTax( (income.getIncome() - getStart())* 0.35 - 5505 );
			income.setTaxed( income.getIncome() - income.getTax() );
		}
		if (income.getIncome() > getStart()+80000) {
			income.setTax( (income.getIncome() - getStart())* 0.45 - 13505 );
			income.setTaxed( income.getIncome() - income.getTax() );
		}
	}

	public static class Income {

		private String message;

		private double income = 0.0;// 含税金额

		private double tax = 0.0; // 税金

		private double taxed = 0.0; // 税后金额

		public String getMessage() {
			return this.message;
		}

		public void setMessage(String message) {
			this.message = message;
		}

		public double getIncome() {
			return income;
		}

		public void setIncome(double income) {
			this.income = income;
		}

		public double getTax() {
			return tax;
		}

		public void setTax(double tax) {
			this.tax = tax;
		}

		public double getTaxed() {
			return taxed;
		}

		public void setTaxed(double taxed) {
			this.taxed = taxed;
		}
	}
}