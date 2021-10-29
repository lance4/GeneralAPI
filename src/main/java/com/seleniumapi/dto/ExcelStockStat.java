package com.seleniumapi.dto;

public class ExcelStockStat {
	
	private String date;
	private String symbol;
	private double day1ClosingPrice;
	private double avgProfit;
	private double successRate;
	private double score;
	private Price day2;
	private Price day3;
	private double maxGain;
	private double openGain;
	private double potentialGain;
	private double maxGainDay2;
	private double openGainDay2;
	private double potentialGainDay2;
	
	
	public double getMaxGainDay2() {
		return maxGainDay2;
	}
	public void setMaxGainDay2(double maxGainDay2) {
		this.maxGainDay2 = maxGainDay2;
	}
	public double getOpenGainDay2() {
		return openGainDay2;
	}
	public void setOpenGainDay2(double openGainDay2) {
		this.openGainDay2 = openGainDay2;
	}
	public double getPotentialGainDay2() {
		return potentialGainDay2;
	}
	public void setPotentialGainDay2(double potentialGainDay2) {
		this.potentialGainDay2 = potentialGainDay2;
	}
	public double getPotentialGain() {
		return potentialGain;
	}
	public void setPotentialGain(double potentialGain) {
		this.potentialGain = potentialGain;
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
	public double getDay1ClosingPrice() {
		return day1ClosingPrice;
	}
	public void setDay1ClosingPrice(double day1ClosingPrice) {
		this.day1ClosingPrice = day1ClosingPrice;
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
	public double getScore() {
		return score;
	}
	public void setScore(double score) {
		this.score = score;
	}
	public Price getDay2() {
		return day2;
	}
	public void setDay2(Price day2) {
		this.day2 = day2;
	}
	public Price getDay3() {
		return day3;
	}
	public void setDay3(Price day3) {
		this.day3 = day3;
	}
	public double getMaxGain() {
		return maxGain;
	}
	public void setMaxGain(double maxGain) {
		this.maxGain = maxGain;
	}
	public double getOpenGain() {
		return openGain;
	}
	public void setOpenGain(double openGain) {
		this.openGain = openGain;
	}
	
	
	
	
	

}
