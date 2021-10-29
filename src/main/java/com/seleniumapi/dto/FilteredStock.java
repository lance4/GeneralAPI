package com.seleniumapi.dto;

public class FilteredStock{
	
	private String date;
	private String symbol;
	private double closingPrice;
	private double avgProfit;
	private double successRate;
	private double propabiltyScore;
	private double datesRank;
	
	public double getDatesRank() {
		return datesRank;
	}
	public void setDatesRank(double datesRank) {
		this.datesRank = datesRank;
	}
	public double getPropabiltyScore() {
		return propabiltyScore;
	}
	public void setPropabiltyScore(double propabiltyScore) {
		this.propabiltyScore = propabiltyScore;
	}
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
	public String getSymbol() {
		return symbol;
	}
	public void setSymbol(String symbol) {
		this.symbol = symbol;
	}
	public double getClosingPrice() {
		return closingPrice;
	}
	public void setClosingPrice(double closingPrice) {
		this.closingPrice = closingPrice;
	}
	public double getAvgProfit() {
		return avgProfit;
	}
	public void setAvgProfit(double avgProfit) {
		this.avgProfit = avgProfit;
	}
	public double getSuccessRate() {
		return successRate;
	}
	public void setSuccessRate(double successRate) {
		this.successRate = successRate;
	}
	
	
	
	
	

}
