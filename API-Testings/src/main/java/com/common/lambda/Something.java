package com.common.lambda;

public class Something {

	private Double amount;

	public Something(Double amount) {
		this.amount = amount;
	}

	public Double getAmount() {
		return amount;
	}

	public void setAmount(Double amount) {
		this.amount = amount;
	}

	@Override
	public String toString() {
		return "Something [amount=" + amount + "]";
	}

}
