package com.seleniumapi.dto;




public class CurrentPrice {
	
	private String symbol;
	private double currentPrice;
	
	public String getSymbol() {
		return symbol;
	}
	public void setSymbol(String symbol) {
		this.symbol = symbol;
	}
	
	public double getCurrentPrice() {
		return currentPrice;
	}
	public void setCurrentPrice(double currentPrice) {
		this.currentPrice = currentPrice;
	}
	@Override
	public String toString() {
		return "CurrentPrice [symbol=" + symbol + ", currentPrice=" + currentPrice + "]";
	}
	
	

}
