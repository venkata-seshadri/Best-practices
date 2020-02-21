package com.example.demo.entity;

public class Record {
	private Long reference;
	private String accountNumber;
	private String description;
	private String startBalance;
	private String mutation;
	private String endBalance;
	
	public String getAccountNumber() {
		return accountNumber;
	}
	public void setAccountNumber(String accountNumber) {
		this.accountNumber = accountNumber;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getStartBalance() {
		return startBalance;
	}
	public void setStartBalance(String startBalance) {
		this.startBalance = startBalance;
	}
	public String getMutation() {
		return mutation;
	}
	public void setMutation(String mutation) {
		this.mutation = mutation;
	}
	public String getEndBalance() {
		return endBalance;
	}
	public void setEndBalance(String endBalance) {
		this.endBalance = endBalance;
	}
	public Long getReference() {
		return reference;
	}
	public void setReference(Long reference) {
		this.reference = reference;
	}
	

}
