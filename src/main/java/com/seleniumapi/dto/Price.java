package com.seleniumapi.dto;


import java.text.DateFormat;
import java.text.SimpleDateFormat;

import java.util.Date;
import java.util.TimeZone;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;


@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Price {
	
	
	private String date;
	private double open;
	private double high;
	private double low;
	private double close;
	private int volume;
	
	public String getDate() {
		return date;
	}
	
	public void setDate(int date) {
		this.date = convertEpocToString(date);
		
	}
	public void setDate(String date) {
		this.date = date;
		
	}
	private String convertEpocToString(int date2) {
		Date date = new Date(date2 * 1000L);
		  DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		  format.setTimeZone(TimeZone.getTimeZone("Etc/UTC"));
		  String formatted = format.format(date);
		  
		return formatted;
	}
	public double getOpen() {
		return open;
	}
	public void setOpen(double open) {
		this.open = open;
	}
	public double getHigh() {
		return high;
	}
	public void setHigh(double high) {
		this.high = high;
	}
	public double getLow() {
		return low;
	}
	public void setLow(double low) {
		this.low = low;
	}
	public double getClose() {
		return close;
	}
	public void setClose(double close) {
		this.close = close;
	}
	public int getVolume() {
		return volume;
	}
	public void setVolume(int volume) {
		this.volume = volume;
	}
	
	
	

}
