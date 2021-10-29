package com.seleniumapi.scheduler;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import com.seleniumapi.dto.FilteredStock;
import com.seleniumapi.services.seleniumAPIService;
import com.seleniumapi.utils.ExcelUtil;

@Component
public class Scheduler {
	
	@Autowired
	public seleniumAPIService seleniumapiservice;
	
	@Autowired
	private ExcelUtil excelUtil;

	
	@Scheduled(cron = "0 2 16 * * MON-FRI")
	public void dailyDataCollection() throws InterruptedException {
		
		if(!isStatHolioday()) {
			List<FilteredStock> advancedStocks = seleniumapiservice.getAdvancedStocks();
			excelUtil.insertAdvancedStocks(advancedStocks); //TODO
			
			String day3Date = getDay3Date();//TODO
			seleniumapiservice.getFollowingDayData(day3Date, 2);
			
			String day2Date = getDay2Date();//TODO
			seleniumapiservice.getFollowingDayData(day2Date, 2);
		}
		
		
	}

	private String getDay2Date() {
		// TODO Auto-generated method stub
		return null;
	}

	private String getDay3Date() {
		// TODO Auto-generated method stub
		return null;
	}

	private boolean isStatHolioday() {
		
		List<String> statHolidayDates = InitDates();
		Date today = Calendar.getInstance().getTime();
		SimpleDateFormat crunchifyFormat = new SimpleDateFormat("yyyy-MM-dd");
		String currentTime = crunchifyFormat.format(today);
		
		if(statHolidayDates.contains(currentTime)) 
			return true;
		else
			return false;
		
	}

	private List<String> InitDates() {
		List<String> statHolidayDates = new ArrayList<String>();
		statHolidayDates.add("2021-07-05");
		statHolidayDates.add("2021-09-06");
		statHolidayDates.add("2021-11-25");
		statHolidayDates.add("2021-11-26");
		statHolidayDates.add("2021-12-24");
		return statHolidayDates;
	}

}
