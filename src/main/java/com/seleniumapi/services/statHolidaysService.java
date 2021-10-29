package com.seleniumapi.services;

import java.util.ArrayList;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.seleniumapi.dto.statHoliday;
import com.seleniumapi.utils.Driver;


public class statHolidaysService {
	
	@Autowired
	public Driver nasdaqSession;
	
	public List<statHoliday> getHolidays()
	{
		
		List<statHoliday> allHolidays = new ArrayList<statHoliday>();
		nasdaqSession.init();
		nasdaqSession.instance.get("https://business.nasdaq.com/trade/US-Options/Holiday-Trading-Hours.html");
		WebElement holidaysTable = nasdaqSession.instance.findElement(By.xpath("//table[@class='table table_design']"));
		WebElement holidaysBody = holidaysTable.findElement(By.tagName("tbody"));
		List<WebElement> allRows = holidaysBody.findElements(By.tagName("tr"));
		
		statHoliday singleHoliday;
		for(WebElement we:allRows)
		{
			singleHoliday = new statHoliday();
			List<WebElement> SingleDate = we.findElements(By.tagName("td"));
			
			singleHoliday.setDate(SingleDate.get(0).getText().toString());
			singleHoliday.setStatus(SingleDate.get(2).getText().toString());
			
			allHolidays.add(singleHoliday);
			
		}
		
		nasdaqSession.instance.close();
		return allHolidays;
	}

}
