package com.sample.drools.bean;

public class Income {

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
