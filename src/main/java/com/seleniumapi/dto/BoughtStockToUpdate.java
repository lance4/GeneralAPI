package com.seleniumapi.dto;

public class BoughtStockToUpdate {
	
	private String symbol;
	private boolean isBought;
	private int lineToUpdate;
	private double sellPrice;
	private String sellTime;
	private String maxPricetime;
	
	
	
	public double getSellPrice() {
		return sellPrice;
	}
	public void setSellPrice(double sellPrice) {
		this.sellPrice = sellPrice;
	}
	public String getSellTime() {
		return sellTime;
	}
	public void setSellTime(String sellTime) {
		this.sellTime = sellTime;
	}
	public String getMaxPricetime() {
		return maxPricetime;
	}
	public void setMaxPricetime(String maxPricetime) {
		this.maxPricetime = maxPricetime;
	}
	public String getSymbol() {
		return symbol;
	}
	public void setSymbol(String symbol) {
		this.symbol = symbol;
	}
	public boolean isBought() {
		return isBought;
	}
	public void setBought(boolean isBought) {
		this.isBought = isBought;
	}
	public int getLineToUpdate() {
		return lineToUpdate;
	}
	public void setLineToUpdate(int lineToUpdate) {
		this.lineToUpdate = lineToUpdate;
	}
	
	

}
