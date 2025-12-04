package com.seleniumapi.controller;

import java.awt.AWTException;
import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.seleniumapi.dto.AdvancedStock;
import com.seleniumapi.dto.CurrentPrice;
import com.seleniumapi.dto.FilteredStock;
import com.seleniumapi.dto.Price;
import com.seleniumapi.dto.statHoliday;
import com.seleniumapi.services.seleniumAPIService;
import com.seleniumapi.services.statHolidaysService;
import com.seleniumapi.utils.Driver;

@RestController
public class APIController {

	@Autowired
	public seleniumAPIService seleniumapiservice;

	@RequestMapping("/getstatholidays")
	public List<statHoliday> getStatHolidays() {

		List<statHoliday> statHolidays = seleniumapiservice.getHolidays();

		return statHolidays;

	}

	@RequestMapping("/getmostgainedstocks")
	public List<FilteredStock> getMostGainedStocks() throws InterruptedException {

		List<FilteredStock> advancedStocks = seleniumapiservice.getAdvancedStocks();
		return advancedStocks;
	}

	@RequestMapping("/getDayTwoData/{date}")
	public List<Price> getDayTwoData(@PathVariable("date") String date) throws InterruptedException {

		List<Price> advancedStocks = seleniumapiservice.getFollowingDayData(date, 1);
		return advancedStocks;
	}

	@RequestMapping("/updateexcel")
	public List<FilteredStock> updateexcel() throws InterruptedException {

		List<FilteredStock> advancedStocks = seleniumapiservice.updateexcel();
		return advancedStocks;
	}

	@RequestMapping("/updatethirddaystats/{date}")
	public void updateThirdDayStats(@PathVariable("date") String date) throws InterruptedException {

		seleniumapiservice.updateThirdDayStats(date);

	}

	@RequestMapping("/getDayThreeData/{date}")
	public List<Price> getDayThreeData(@PathVariable("date") String date) throws InterruptedException {

		List<Price> advancedStocks = seleniumapiservice.getFollowingDayData(date, 2);
		return advancedStocks;
	}

	@GetMapping("/gethighestpricelastyear/{symbol}")
	public double getHighestPriceLastYear(@PathVariable("symbol") String symbol) {

		double highestPrice;
		highestPrice = seleniumapiservice.getHighestPriceLastYear(symbol);
		return highestPrice;
	}

	@RequestMapping("/addstockstoyahooportfolio/{symbols}")
	public void addStocktoYahooPortfolio(@PathVariable("symbols") String symbols) {

		try {
			seleniumapiservice.addStockToYahooPortfolio(symbols);
		} catch (AWTException e) {
			e.printStackTrace();
		}
	}

	@RequestMapping("/openyahooportfolio")
	public void openYahooPortfolio() {
		seleniumapiservice.openYahooPortfolio();
	}

	@RequestMapping("/getcurrentprice/{symbol}")
	public double getCurrentPrice(@PathVariable("symbol") String symbol) {
		double currentPrice = seleniumapiservice.getCurrentPrice(symbol);
		return currentPrice;
	}

	@PostMapping("/getallcurrentprices")
	public List<CurrentPrice> getAllCurrentPrices(@RequestBody List<CurrentPrice> currentPrices) {
		currentPrices = seleniumapiservice.updateAllCurrentPrices(currentPrices);
		return currentPrices;

	}

	@PostMapping("/buyStock/{symbol}&{shares}")
	public void buyStock(@PathVariable("symbol") String symbol, @PathVariable("shares") int shares) {
		seleniumapiservice.buyStock(symbol, shares);
		System.out.println("symbol " + symbol + ". shares: " + shares);
	}

	@GetMapping("/getIntradayHistory")
	public void getIntradayHistory() throws JsonProcessingException {
		seleniumapiservice.collectStocksIntraDay();
	}

	@GetMapping("/getdataforstock/{symbol}&{date}")
	public void getDataForStock(@PathVariable("symbol") String symbol, @PathVariable("symbol") String date)
			throws JsonProcessingException {
		seleniumapiservice.getDataForStock(symbol, date);
	}

	@GetMapping("/migratedata")
	public void migrateData() throws JsonProcessingException {
		seleniumapiservice.migrateData();
	}

}
