package com.seleniumapi.dto;

import java.io.Serializable;





public class AdvancedStock{
	
	private String symbol;
	private String companyName;
	private double dayOneClosingPrice;
	private double dayOneChangeInPrice;
	private int shareVolume;
	private int shareVolumeAVGThreeMonths;
	private double changeInPercents;
	private Double marketCap;
	
	
	
	
	public Double getMarketCap() {
		return marketCap;
	}
	public void setMarketCap(Double marketCap) {
		this.marketCap = marketCap;
	}
	public int getShareVolumeAVGThreeMonths() {
		return shareVolumeAVGThreeMonths;
	}
	public void setShareVolumeAVGThreeMonths(int shareVolumeAVGThreeMonths) {
		this.shareVolumeAVGThreeMonths = shareVolumeAVGThreeMonths;
	}
	public String getSymbol() {
		return symbol;
	}
	public void setSymbol(String symbol) {
		this.symbol = symbol;
	}
	public String getCompanyName() {
		return companyName;
	}
	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}
	public double getDayOneClosingPrice() {
		return dayOneClosingPrice;
	}
	public void setDayOneClosingPrice(double dayOneClosingPrice) {
		this.dayOneClosingPrice = dayOneClosingPrice;
	}
	public double getDayOneChangeInPrice() {
		return dayOneChangeInPrice;
	}
	public void setDayOneChangeInPrice(double dayOneChangeInPrice) {
		this.dayOneChangeInPrice = dayOneChangeInPrice;
	}
	public int getShareVolume() {
		return shareVolume;
	}
	public void setShareVolume(int shareVolume) {
		this.shareVolume = shareVolume;
	}
	public double getChangeInPercents() {
		return changeInPercents;
	}
	public void setChangeInPercents(double changeInPercents) {
		this.changeInPercents = changeInPercents;
	}
	@Override
	public String toString() {
		return "AdvancedStock [symbol=" + symbol + ", companyName=" + companyName + ", dayOneClosingPrice="
				+ dayOneClosingPrice + ", dayOneChangeInPrice=" + dayOneChangeInPrice + ", shareVolume=" + shareVolume
				+ ", changeInPercents=" + changeInPercents + "]";
	}
	
	
	
}