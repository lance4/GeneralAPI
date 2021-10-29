package com.seleniumapi.dto;

import java.util.List;

public class DayStat {
	
	private String date;
	private List<Double> openGain;
	private List<Double> MaxGain;
	private List<Double> PotentialGain;
	private List<Double> openGainDay2;
	private List<Double> MaxGainDay2;
	private List<Double> PotentialGainDay2;
	private double openAvgProfit;
	private double potentialAvgProfit;
	private double potentialAvgProfitDay2;
	
	
	
	
	public double getPotentialAvgProfitDay2() {
		return potentialAvgProfitDay2;
	}
	public void setPotentialAvgProfitDay2(double potentialAvgProfitDay2) {
		this.potentialAvgProfitDay2 = potentialAvgProfitDay2;
	}
	public List<Double> getOpenGainDay2() {
		return openGainDay2;
	}
	public void setOpenGainDay2(List<Double> openGainDay2) {
		this.openGainDay2 = openGainDay2;
	}
	public List<Double> getMaxGainDay2() {
		return MaxGainDay2;
	}
	public void setMaxGainDay2(List<Double> maxGainDay2) {
		MaxGainDay2 = maxGainDay2;
	}
	public List<Double> getPotentialGainDay2() {
		return PotentialGainDay2;
	}
	public void setPotentialGainDay2(List<Double> potentialGainDay2) {
		PotentialGainDay2 = potentialGainDay2;
	}
	public List<Double> getPotentialGain() {
		return PotentialGain;
	}
	public void setPotentialGain(List<Double> potentialGain) {
		PotentialGain = potentialGain;
	}
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
	public List<Double> getOpenGain() {
		return openGain;
	}
	public void setOpenGain(List<Double> openGain) {
		this.openGain = openGain;
	}
	public List<Double> getMaxGain() {
		return MaxGain;
	}
	public void setMaxGain(List<Double> maxGain) {
		MaxGain = maxGain;
	}
	public double getOpenAvgProfit() {
		return openAvgProfit;
	}
	public void setOpenAvgProfit(double openAvgProfit) {
		this.openAvgProfit = openAvgProfit;
	}
	public double getPotentialAvgProfit() {
		return potentialAvgProfit;
	}
	public void setPotentialAvgProfit(double potentialAvgProfit) {
		this.potentialAvgProfit = potentialAvgProfit;
	}
	
	
	
	
	
	
	
	

}
