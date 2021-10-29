package com.seleniumapi.services;

import java.awt.AWTException;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.apache.commons.math3.analysis.function.Add;import org.apache.http.impl.conn.tsccm.WaitingThread;
import org.json.JSONObject;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;

import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.StreamUtils;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ser.std.DateSerializer;
import com.google.common.net.InetAddresses.TeredoInfo;
import com.seleniumapi.dto.AdvancedStock;
import com.seleniumapi.dto.AlphaVantageInfo;
import com.seleniumapi.dto.BoughtStockToUpdate;
import com.seleniumapi.dto.CurrentPrice;
import com.seleniumapi.dto.DayStat;
import com.seleniumapi.dto.ExcelStockStat;
import com.seleniumapi.dto.FilteredStock;
import com.seleniumapi.dto.HistoricalDataStock;
import com.seleniumapi.dto.MarketWatchTransaction;
import com.seleniumapi.dto.Price;
import com.seleniumapi.dto.statHoliday;
import com.seleniumapi.utils.Driver;
import com.seleniumapi.utils.ExcelUtil;


@Service
public class seleniumAPIService {

	private static final Logger LOGGER = LoggerFactory.getLogger(seleniumAPIService.class);
	private static final ObjectMapper mapper = new ObjectMapper();

	@Autowired
	public Driver webSession;
	
	@Autowired
	private ExcelUtil excelUtil;

	@Value("${ALPHEVANTAGE_KEY}")
	private String ALPHEVANTAGE_KEY;
	
	@Value("${yahooUserName}")
	private String yahooUserName;
	
	@Value("${yahooPassword}")
	private String yahooPassword;
	
	@Value("${investopediaUserName}")
	private String investopediaUserName;
	
	@Value("${investopediaPassword}")
	private String investopediaPassword;
	
	
	
	
	
	static private WebDriver portfolioDriver;

	public List<statHoliday> getHolidays() {

		List<statHoliday> allHolidays = new ArrayList<statHoliday>();
		webSession.init();
		webSession.instance.get("https://business.nasdaq.com/trade/US-Options/Holiday-Trading-Hours.html");
		WebElement holidaysTable = webSession.instance.findElement(By.xpath("//table[@class='table table_design']"));
		WebElement holidaysBody = holidaysTable.findElement(By.tagName("tbody"));
		List<WebElement> allRows = holidaysBody.findElements(By.tagName("tr"));

		statHoliday singleHoliday;
		for (WebElement we : allRows) {
			singleHoliday = new statHoliday();
			List<WebElement> SingleDate = we.findElements(By.tagName("td"));

			singleHoliday.setDate(SingleDate.get(0).getText().toString());
			singleHoliday.setStatus(SingleDate.get(2).getText().toString());

			allHolidays.add(singleHoliday);

		}

		webSession.close();
		return allHolidays;
	}

	public List<Price> getFollowingDayData(String inputDate, int day) {
		List<String> symbols = excelUtil.getSymbolsFromExcel(inputDate);
		List<Price> priceList = new ArrayList<Price>();
		for(String s: symbols) {
			List<Price> callHistoricalDataAPICSV = callHistoricalDataAPICSV(s);
			
			for(int i=0; i<callHistoricalDataAPICSV.size();i++) {
				Price p = callHistoricalDataAPICSV.get(i);
				if(p.getDate().equalsIgnoreCase(inputDate)) {
					Price nextPrice = callHistoricalDataAPICSV.get(i+day);
					//System.out.println(s+", "+nextPrice.getOpen()+", "+nextPrice.getHigh()+", "+nextPrice.getLow()+", "+nextPrice.getClose());
					System.out.println(nextPrice.getOpen()+", "+nextPrice.getHigh()+", "+nextPrice.getLow()+", "+nextPrice.getClose());
					priceList.add(nextPrice);
				}
			}
		}
		return priceList;
	}
	
	public List<FilteredStock> updateexcel() {
		
		
		List<ExcelStockStat> excelStockStat = excelUtil.getExcelStockStat();
		List<DayStat> daysStats = new ArrayList<DayStat>();
		/*try {
			System.out.println("excelStockStat: "+mapper.writeValueAsString(excelStockStat));
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}*/
		DayStat dayStat = new DayStat();
		List<Double> openGain = new ArrayList<Double>();
		List<Double> MaxGain = new ArrayList<Double>();
		List<Double> potentialGain = new ArrayList<Double>();
		List<Double> openGainDay2 = new ArrayList<Double>();
		List<Double> MaxGainDay2 = new ArrayList<Double>();
		List<Double> potentialGainDay2 = new ArrayList<Double>();
		int counter = 0;
		
		for(int j=0;j<excelStockStat.size();j++) { //
			ExcelStockStat es = excelStockStat.get(j);
			
			if(daysStats.size()==0) {
				//System.out.println("first date: "+es.getDate());
				
				dayStat.setDate(es.getDate());
				openGain.add(es.getOpenGain());
				MaxGain.add(es.getMaxGain());
				openGainDay2.add(es.getOpenGainDay2());
				MaxGainDay2.add(es.getMaxGainDay2());
				potentialGain.add(es.getPotentialGain());
				potentialGainDay2.add(es.getPotentialGainDay2());
				dayStat.setOpenGain(openGain);
				dayStat.setMaxGain(MaxGain);
				dayStat.setPotentialGain(potentialGain);
				dayStat.setPotentialGainDay2(potentialGainDay2);
				daysStats.add(dayStat);
			}else {
				
				boolean inseartNewDate = true; 
				for(int i=0;i<daysStats.size();i++) {
					
					DayStat ds = daysStats.get(i);
					if(ds.getDate().equals(es.getDate())) {
						//System.out.println("date exists: "+es.getDate());
						ds.getMaxGain().add(es.getMaxGain());
						ds.getOpenGain().add(es.getOpenGain());
						openGainDay2.add(es.getOpenGainDay2());
						MaxGainDay2.add(es.getMaxGainDay2());
						ds.getPotentialGain().add(es.getPotentialGain());
						ds.getPotentialGainDay2().add(es.getPotentialGainDay2());
						inseartNewDate = false;
						break;
					} 
				  
				}
				if(inseartNewDate) {
					openGain = new ArrayList<Double>();
					MaxGain = new ArrayList<Double>();
					potentialGain = new ArrayList<Double>();
					potentialGainDay2 = new ArrayList<Double>();
					
					dayStat = new DayStat();
					dayStat.setDate(es.getDate());
					openGain.add(es.getOpenGain());
					MaxGain.add(es.getMaxGain());
					openGainDay2.add(es.getOpenGainDay2());
					MaxGainDay2.add(es.getMaxGainDay2());
					potentialGain.add(es.getPotentialGain());
					potentialGainDay2.add(es.getPotentialGainDay2());
					dayStat.setOpenGain(openGain);
					dayStat.setMaxGain(MaxGain);
					dayStat.setPotentialGain(potentialGain);
					dayStat.setPotentialGainDay2(potentialGainDay2);
					daysStats.add(dayStat);
				}
			}
			
		}
		try {
			System.out.println("daysStats: "+mapper.writeValueAsString(daysStats));
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		//calculateDayStats(daysStats);
		System.out.println("_______________________");
		calculateProbabilyty(excelStockStat);
		//calculatePotentialGain (excelStockStat);
		
		
		
		//System.out.println("above 3% rate: "+Double.valueOf(above3Counter)/Double.valueOf(stocksToUpdate.size())*100);
		return null;
	}

	private void calculateDayStats(List<DayStat> daysStats) {
		for(DayStat ds: daysStats) {
			List<Double> potentialProfitList = ds.getPotentialGain();
			List<Double> potentialProfitListDay2 = ds.getPotentialGainDay2();
			List<Double> maxOpenList = ds.getOpenGain();
			Double maxPotentialTotal = 0.0;
			Double maxPotentialTotalDay2 = 0.0;
			Double maxGainTotal = 0.0;
			Double maxOpenTotal = 0.0;
			
			for(Double d: potentialProfitList) {
				maxPotentialTotal = maxPotentialTotal+d;
			}
			
			for(Double d1: potentialProfitListDay2) {
				maxPotentialTotalDay2 = maxPotentialTotalDay2+d1;
			}
			
			for(Double d: maxOpenList) {
				maxOpenTotal = maxOpenTotal+d;
			}
			
			Double totalProfit = maxPotentialTotal/Double.valueOf(potentialProfitList.size());
			if(totalProfit>0)
				ds.setPotentialAvgProfit(totalProfit/100+1);
			else
				ds.setPotentialAvgProfit((100+totalProfit)/100);
			//System.out.println(ds.getDate()+", "+maxPotentialTotalDay2+", "+Double.valueOf(potentialProfitListDay2.size()));
			Double totalProfitDay2 = maxPotentialTotalDay2/Double.valueOf(potentialProfitListDay2.size());
			
			if(totalProfitDay2>0)
				ds.setPotentialAvgProfitDay2(totalProfitDay2/100+1);
			else
				ds.setPotentialAvgProfitDay2((100+totalProfitDay2)/100);
			System.out.println(ds.getPotentialAvgProfitDay2());
		}
		
	}

	private void calculateProbabilyty(List<ExcelStockStat> excelStockStat) {
		
		for(ExcelStockStat es:excelStockStat) {
			List<Price> historicalPricelist = callHistoricalDataAPICSV(es.getSymbol());
			
			FilteredStock analyzeHistoricalData = analyzeHistoricalData(historicalPricelist, es.getSymbol());
			System.out.println(analyzeHistoricalData.getSymbol()+", "+analyzeHistoricalData.getPropabiltyScore());
				
		}
		
		
	}

	private void calculatePotentialGain (List<ExcelStockStat> excelStockStat) {
		System.out.println("potential gain list");
		for(ExcelStockStat fs: excelStockStat) {
			
			if(fs.getOpenGain()>3) {
				fs.setPotentialGain(fs.getOpenGain());
			}else {
				if(fs.getMaxGain()>3 && fs.getOpenGain() <3)
					fs.setPotentialGain(3.02);
				else
					fs.setPotentialGain((fs.getDay3().getClose()-fs.getDay2().getClose())/fs.getDay2().getClose()*100);
			}
			//System.out.println(fs.getDate()+", "+fs.getSymbol()+", "+fs.getOpenGainDay2()+", "+fs.getMaxGainDay2());
			if(fs.getOpenGainDay2()>3) {
				fs.setPotentialGainDay2(fs.getOpenGainDay2());
			}else {
				if(fs.getMaxGainDay2()>3 && fs.getOpenGainDay2() <3)
					fs.setPotentialGainDay2(3.02);
				else
					fs.setPotentialGainDay2((fs.getDay2().getClose()-fs.getDay1ClosingPrice())/fs.getDay1ClosingPrice()*100);
			}
			
			//System.out.println(fs.getPotentialGain());
			System.out.println(fs.getPotentialGainDay2());
			
			//getAllStocksData(fs);
			
			
		}
	}

	private void getAllStocksData(ExcelStockStat fs) {
		List<Price> callHistoricalDataAPICSV = callHistoricalDataAPICSV(fs.getSymbol());
		FilteredStock analyzeHistoricalData = analyzeHistoricalData(callHistoricalDataAPICSV,fs.getSymbol());
		for(int i=0;i<callHistoricalDataAPICSV.size();i++) {
			Price p = callHistoricalDataAPICSV.get(i);
			if(p.getDate().equals(fs.getDate())) {
				Price day2 = callHistoricalDataAPICSV.get(i+1);
				Price day3 = callHistoricalDataAPICSV.get(i+2);
				
				//System.out.println(day2.getOpen()+", "+day2.getHigh()+", "+day2.getLow()+", "+day2.getClose()+", "+day3.getOpen()+", "+day3.getHigh()+", "+day3.getLow()+", "+day3.getClose());
				double gain = (day3.getHigh()-day2.getClose())/day2.getClose()*100;
			
				//double gain = (day2.getHigh()-p.getClose())/p.getClose()*100;
				
				System.out.println(fs.getDate()+", "+fs.getSymbol()+", "+p.getClose()+", "+analyzeHistoricalData.getAvgProfit()+", "+analyzeHistoricalData.getSuccessRate()+", "+analyzeHistoricalData.getPropabiltyScore());
				
			}
		}
	}
	
	

	public List<FilteredStock> getAdvancedStocks() throws InterruptedException {
		List<AdvancedStock> allStocks = new ArrayList<AdvancedStock>();
		List<AdvancedStock> filteredAllStocks = new ArrayList<AdvancedStock>();
		List<FilteredStock> finalAllStocks = new ArrayList<FilteredStock>();
		getCurrentDatetimeAsEpoch();
		allStocks = getRAWAdvancedStocks();
		
		filteredAllStocks = allStocks
				.stream()
				.filter(as ->as.getDayOneClosingPrice()>10 && as.getShareVolume()> 2000000 && as.getShareVolumeAVGThreeMonths()>2000000 && as.getMarketCap()>2 && as.getChangeInPercents()<70 &&(as.getShareVolume()*as.getDayOneClosingPrice()>100000000))
				.collect(Collectors.toList());
		
		finalAllStocks = filterByHistoricalData(filteredAllStocks);
		
		//Sort the results by success rate
		Collections.sort(finalAllStocks, (FilteredStock fs1, FilteredStock fs2) -> Double.compare(fs1.getPropabiltyScore(), fs2.getPropabiltyScore()));
		System.out.println("finalAllStocks: "+finalAllStocks.toString());
		//Reverse the order to descending 
		Collections.reverse(finalAllStocks);
		
		for(FilteredStock fs: finalAllStocks) {
			System.out.println(fs.getDate()+", "+fs.getSymbol()+", "+fs.getClosingPrice()+", "+ fs.getAvgProfit()+", "+fs.getPropabiltyScore());
		}
		
		
		return finalAllStocks;
	}
	
	
	
	private List<FilteredStock> filterByHistoricalData(List<AdvancedStock> filteredAllStocks) {
		
		List<FilteredStock> filteredStocks = new ArrayList<FilteredStock>();
		
		for(AdvancedStock as: filteredAllStocks) {
			List<Price> historicalPricelist = callHistoricalDataAPICSV(as.getSymbol());
			//System.out.println(as.getSymbol()+": "+historicalPricelist.size());
			if(historicalPricelist.size()>100)
				filteredStocks.add(analyzeHistoricalData(historicalPricelist, as.getSymbol()));
		}
		
		return filteredStocks;
	}

	private String getCurrentDate() {
		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");  
		LocalDateTime now = LocalDateTime.now(); 
		return dtf.format(now);
		
	}

	private List<Price> callHistoricalDataAPICSV(String symbol) {
		List<Price> pricesList = new ArrayList<Price>();
		String startDate = "1581305632";
		
		String endDate = getCurrentDatetimeAsEpoch();
		String uri = "https://query1.finance.yahoo.com/v7/finance/download/"+symbol+"?period1="+startDate+"&period2="+endDate+"&interval=1d&events=history&includeAdjustedClose=false";
		try {
			
			InputStream input = new URL(uri).openStream();
			Scanner inputStream = new Scanner(input);
			while (inputStream.hasNext()) {
				Price price = new  Price();
		        String data = inputStream.nextLine();
		        price = mapRawDataToObject(data);
		        if(price!=null)
		        	pricesList.add(price);
		        //System.out.println(data);
		    }
		    inputStream.close();
		    
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return pricesList;
	}

	private String getCurrentDatetimeAsEpoch() {
		
		
		Date today = Calendar.getInstance().getTime();
		SimpleDateFormat crunchifyFormat = new SimpleDateFormat("MMM dd yyyy HH:mm:ss.SSS zzz");
		String currentTime = crunchifyFormat.format(today);

		try {
			 
			// parse() parses text from the beginning of the given string to produce a date.
			Date date = crunchifyFormat.parse(currentTime);
 
			// getTime() returns the number of milliseconds since January 1, 1970, 00:00:00 GMT represented by this Date object.
			long epochTime = date.getTime();
 
			String epochAsString = String.valueOf(epochTime);
		    return epochAsString;
		} catch (ParseException e) {
			e.printStackTrace();
			return null;
		}
		
		
	}

	private Price mapRawDataToObject(String rawData) {
		String[] splitData = rawData.split(",");
		Price price = new Price();
		if(splitData[0].equals("Date"))
			return null;
		else {
			price.setDate(splitData[0].trim());
			price.setOpen(Double.parseDouble(splitData[1].trim()));
			price.setHigh(Double.parseDouble(splitData[2].trim()));
			price.setLow(Double.parseDouble(splitData[3].trim()));
			price.setClose(Double.parseDouble(splitData[4].trim()));
			return price;
		}
	}

	private HistoricalDataStock callHistoricalDataAPI(String symbol) {
		
		final String uri = "https://apidojo-yahoo-finance-v1.p.rapidapi.com/stock/v3/get-historical-data?symbol="+symbol+"&region=US";
		HttpHeaders headers = new HttpHeaders();
		headers.set("x-rapidapi-key", "0259e92ac8msh4b958da6b0ac130p157b0ajsndcb6d20372d0");
		headers.set("x-rapidapi-host", "apidojo-yahoo-finance-v1.p.rapidapi.com");
		HttpEntity<String> requestEntity = new HttpEntity<>(null, headers);
		
		RestTemplate restTemplate = new RestTemplate();
		ResponseEntity<String> yahooHistoricalDataResponse = restTemplate.exchange(uri,HttpMethod.GET,  requestEntity, String.class);
		HistoricalDataStock historicalDataStock;
		try {
			historicalDataStock = mapper.readValue("", HistoricalDataStock.class);
			return historicalDataStock;
		} catch (JsonParseException e) {
			e.printStackTrace();
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		//System.out.println("yahoo response for sfix: "+exchange.getBody());
        
		return null;
	}

	private FilteredStock analyzeHistoricalData(List<Price> pricesList, String symbol) {
		//Collections.reverse(pricesList);
		FilteredStock filteredStocks = new FilteredStock();
		int countGainedDays=0;
		
		double closingPrice = pricesList.get(pricesList.size()-1).getClose();		
		double totalPercentProfit = 0;
		List <Double> successDates = new ArrayList<Double> ();
		List <Double> failDates = new ArrayList<Double> ();
		
		
		
		for(int i=0;i<pricesList.size()-3;i++) {
			Price previousDayStat = pricesList.get(i);
			Price day1Stat = pricesList.get(i+1);
			Price day2Stat = pricesList.get(i+2);
			Price day3Stat = pricesList.get(i+3);
			
			
			double gained =(day1Stat.getClose()-previousDayStat.getClose())/previousDayStat.getClose()*100;
			if(gained>6.5) {
				//TODO check if it wasn't gained the day before
				
				countGainedDays++;
				
				double day3gain = (day3Stat.getHigh()-day2Stat.getClose())/day2Stat.getClose()*100;
				totalPercentProfit = totalPercentProfit+day3gain;
				if((day3gain>3)){
					successDates.add(day3gain);
					//successGainedDays++;
					//System.out.println("success: "+day1Stat.getDate()+" gain: "+gained+", profit:"+day3gain);
				}else {
					failDates.add(day3gain);
					//failedGainedDays++;
					//System.out.println("failed: "+day1Stat.getDate()+" gain: "+gained+", profit:"+day3gain);
				}
			}
		}
		
		
		double prpability = calculatePropability(successDates, failDates);
	
		
		filteredStocks.setDate(getCurrentDate());
		filteredStocks.setSymbol(symbol);
		filteredStocks.setClosingPrice(closingPrice);
		filteredStocks.setAvgProfit(totalPercentProfit/countGainedDays);
		//filteredStocks.setSuccessRate(Double.valueOf(successGainedDays)/Double.valueOf(countGainedDays)*100);
		filteredStocks.setPropabiltyScore(prpability);
		
		System.out.println(filteredStocks.getSymbol()+", "+closingPrice+", "+ totalPercentProfit+", "+countGainedDays+", "+filteredStocks.getPropabiltyScore());

		return filteredStocks;
	}

	

	private double calculatePropability(List<Double> successDates, List<Double> failDates) {
		Double totalWins = 0.0;
		Double averageWin = 0.0;
		Double totalLosses = 0.0;
		Double averageLoss = 0.0;
		Double ChanceToWin = 0.0;
		Double ChanceToLose = 0.0;
		
		ChanceToWin = Double.valueOf(successDates.size())/(Double.valueOf((successDates.size())+Double.valueOf((failDates.size()))));
		ChanceToLose = 1-ChanceToWin;
		
		for(Double w: successDates) {
			totalWins = totalWins+w;
		}
		averageWin = totalWins/Double.valueOf(successDates.size());
		
		for(Double f: failDates) {
			totalLosses = totalLosses+f;
		}
		averageLoss = totalLosses/Double.valueOf(failDates.size());
		//System.out.println(ChanceToWin+" * "+averageWin+" + "+ChanceToLose+" * "+averageLoss);
		Double propability = ChanceToWin*averageWin+ChanceToLose*averageLoss;
		
		return propability;
	}

	private double rankDates(List<String> successDates, List<String> failDates) {
		DecimalFormat df = new DecimalFormat("#.####");
		double totalRank = 0;
		
		for(String s:successDates) {
			//calculate the rank and add it to a map
			double rank = rankSingleDate(s);
			totalRank = totalRank+rank;
		}
		
		for(String f:failDates) {
			//calculate the rank and add it to a map
			double rank = rankSingleDate(f);
			totalRank = totalRank-rank;
		}
		
		return totalRank;
	}

	private double rankSingleDate(String dateToCompareAsString) {
		double rank = 1;
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");  
			Date currentDate = sdf.parse(getCurrentDate());
			Date dateToCompare = sdf.parse(dateToCompareAsString);
			long diffInMillies = Math.abs(currentDate.getTime() - dateToCompare.getTime());
		    long diff = TimeUnit.DAYS.convert(diffInMillies, TimeUnit.MILLISECONDS);
		    int months = (int) (diff/30);
		    
		    switch (months) {
		    	case 0:
		    		rank = 1.13;
		    		break;
		    	case 1:
		    		rank = 1.12;
		    		break;
		    	case 2:
		    		rank = 11.1;
		    		break;
		    	case 3:
		    		rank =0; //1.10
		    		break;
		    	case 4:
		    		rank = 0; //1.09
		    		break;
		    	case 5:
		    		rank = 0; //1.08
		    		break;
		    	case 6:
		    		rank = 0; //1.07
		    		break;
		    	case 7:
		    		rank = 0; //1.06
		    		break;
		    	case 8:
		    		rank = 0; //1.05
		    		break;
		    	case 9:
		    		rank = 0; //1.04
		    		break;
		    	case 10:
		    		rank = 0; //1.03
		    		break;
		    	case 11:
		    		rank = 0; //1.02
		    		break;
		    	case 12:
		    		rank = 0; //1.01
		    		break;
		    }
		    
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return rank;
	}

	private List<AdvancedStock> getRAWAdvancedStocks() throws InterruptedException {

		List<AdvancedStock> allStocks = new ArrayList<AdvancedStock>();
		webSession.init();
		WebDriverWait wait = new WebDriverWait(webSession.instance, 5);

		
/*
		WebElement mosAdvanced = wait.until(
				ExpectedConditions.elementToBeClickable(webSession.instance.findElement(By.id("scr-res-table"))));
		JavascriptExecutor executor = (JavascriptExecutor) webSession.instance;
		*/
		
		
		
		
		//
		int page = 0;
		do {
			webSession.instance.get("https://finance.yahoo.com/gainers?count=25&offset="+page*25);
			
			//Get data from first page
			WebElement div = webSession.instance.findElement(By.id("scr-res-table"));
			
			allStocks.addAll(getGainedStockesFromPage(div));
			page++;
		}while(allStocks.get(allStocks.size()-1).getChangeInPercents()>7); //TODO env variable
		
		
		

		webSession.close();
		return allStocks;
	}

	private List<AdvancedStock> getGainedStockesFromPage(WebElement div) {
		WebElement stocksTable = div.findElement(By.tagName("tbody"));
		List<WebElement> allRows = stocksTable.findElements(By.tagName("tr"));
		List<AdvancedStock> stocksInPage = new ArrayList<AdvancedStock>();
		
		for (WebElement we : allRows) {
			AdvancedStock stock = new AdvancedStock();

			List<WebElement> allTd = we.findElements(By.tagName("td"));
			
			
			
			stock.setSymbol(allTd.get(0).getText());
			stock.setCompanyName(allTd.get(1).getText());
			String ClosingPriceString = allTd.get(2).getText().replace(",", "");
			stock.setDayOneClosingPrice(Double.parseDouble(ClosingPriceString));

			String changeInPriceString = allTd.get(3).getText().replace("+", "");

			double changeInPrice = Double.parseDouble(changeInPriceString);
			stock.setDayOneChangeInPrice(changeInPrice);
			String changeInPercentString = allTd.get(4).getText().replace("+", "").replace("%", "").replace(",", "");
			double changeInPercents = Double.parseDouble(changeInPercentString);
			stock.setChangeInPercents(changeInPercents);
			
			
			int shareVolume = convertVolume(allTd.get(5).getText());
			stock.setShareVolume(shareVolume);
			
			int shareVolumeAVG3Months = convertVolume(allTd.get(6).getText());
			stock.setShareVolumeAVGThreeMonths(shareVolumeAVG3Months);
			double marketCap = 0.0;
			if(allTd.get(7).getText().indexOf("M")>-1)
				marketCap = Double.parseDouble(allTd.get(7).getText().replace("M", ""))/1000;
			else
				marketCap = Double.parseDouble(allTd.get(7).getText().replace("B", "").replace("T", ""));
			stock.setMarketCap(marketCap);
			stocksInPage.add(stock);
		}
		return stocksInPage;
	}

	private int convertVolume(String volumeAsString) {
		int volume;
	if(volumeAsString.indexOf("M")>0) {
		volumeAsString = volumeAsString.replace("M","");
		Double VolumeAsM = Double.parseDouble(volumeAsString);
		volume = (int) (VolumeAsM*1000000);
	}else {
		if(volumeAsString.indexOf("B")>0) {
			volumeAsString = volumeAsString.replace("B","");
			Double VolumeAsM = Double.parseDouble(volumeAsString);
			volume = (int) (VolumeAsM*1000000000);
		}else {
			if(volumeAsString.indexOf("k")>0) {
				volumeAsString = volumeAsString.replace("k","");
				volumeAsString = volumeAsString.replace(".", "");
				volume = Integer.parseInt(volumeAsString);
			}else {
				if(volumeAsString.contains("N/A"))
					volume = 0;
					else {
						volumeAsString = volumeAsString.replace(",","");
						volume = Integer.parseInt(volumeAsString);
					}
			}
			
			
		}
		
	}
		return volume;
	}

	public double getHighestPriceLastYear(String symbol) {
		webSession.init();

		webSession.instance.get("https://finance.yahoo.com/quote/" + symbol + "?p=" + symbol);
		List<WebElement> alldata = webSession.instance.findElements(By.xpath("//td[@class='Ta(end) Fw(b) Lh(14px)']"));

		String yearRange = alldata.get(5).getText();
		int hyphen = yearRange.indexOf("-");

		String lastYearHighestSTRING = yearRange.substring(hyphen + 1).trim();
		double lastYearHighest = Double.parseDouble(lastYearHighestSTRING);

		webSession.close();
		return lastYearHighest;
	}


	public WebDriver loginToYahooFinance() {
		webSession.init();
		webSession.instance.get("https://login.yahoo.com/?.src=finance&.intl=us&.done=https%3A%2F%2Fca.finance.yahoo.com%2Fportfolios&add=1");
		webSession.instance.findElement(By.id("login-username")).sendKeys(yahooUserName);
		webSession.instance.findElement(By.id("login-signin")).click();
		webSession.instance.findElement(By.id("login-passwd")).sendKeys(yahooPassword);
		webSession.instance.findElement(By.id("login-signin")).click();
		webSession.instance.get("https://ca.finance.yahoo.com/portfolio/p_0/view/v1");
		
		
		return webSession.instance;
		
		
		
		
	}
	
	public void addStockToYahooPortfolio(String symbols) throws AWTException {
		
		//Call login method
		WebDriver driver = loginToYahooFinance();
		
		//Click on add symbol Icon
		WebElement inputText = driver.findElement(By.xpath("//button[@data-test='addSymbol']"));
		
		//Insert all symbols
		Actions actions = new Actions(driver);
		actions.moveToElement(inputText);
		actions.click();
		actions.sendKeys(symbols);
		actions.build().perform();
		
		//Click enter
		actions.sendKeys(Keys.ENTER);
		actions.build().perform();
		
		webSession.close();

	}

	public double getCurrentPrice(String symbol) {
		
		double currentPrice = 0;
		//openYahooPortfolio();
		
		
		WebElement symbolName = portfolioDriver.findElement(By.cssSelector("a[href*='"+symbol+"']"));
		//Get parent elements 2 levels go get the row
		WebElement parent = symbolName.findElement(By.xpath("..")).findElement(By.xpath("..")).findElement(By.xpath(".."));
		
		String[] data = parent.getText().split("\\s+");
		currentPrice = Double.parseDouble(data[1]);
		
		
		return currentPrice;
	}

	public void openYahooPortfolio() {
		
		//Call login method
		portfolioDriver = loginToYahooFinance();
		
	}

	public List<CurrentPrice> updateAllCurrentPrices(List<CurrentPrice> currentPrices) {
		openYahooPortfolio();
		for(CurrentPrice cp: currentPrices)
		{
			String symbol = cp.getSymbol();
			cp.setCurrentPrice(getCurrentPrice(symbol));
		}
		return currentPrices;
	}

	public void buyStock(String symbol, int shares) {
		
		
		/*
		webSession.init();
		
		webSession.instance.get("http://www.investopedia.com/accounts/login.aspx?returnurl=http://www.investopedia.com/simulator/");
		
		webSession.instance.findElement(By.id("edit-email")).sendKeys(investopediaUserName);
		webSession.instance.findElement(By.id("edit-password")).sendKeys(investopediaPassword);
		webSession.instance.findElement(By.id("edit-submit")).click();
		
		webSession.instance.get("http://www.investopedia.com/simulator/trade/tradestock.aspx?too=1&Sym="+symbol+"&Qty="+shares);
		webSession.instance.findElement(By.id("previewButton")).click();
		
		webSession.instance.findElement(By.id("submitOrder")).click();
		
		webSession.close();
		*/
		
		
	}

	public List <MarketWatchTransaction> updateThirdDayStats(String date) {
		
		
		MarketWatchTransaction marketWatchTransaction  =new MarketWatchTransaction();
		List <MarketWatchTransaction> marketWatchTransactionList = new ArrayList<MarketWatchTransaction>();
		List<BoughtStockToUpdate> stocksToUpdate = excelUtil.getBoughtStocks(date);
		
		try {
			
			//InputStream input = new File("F:\\CSVDemo.csv");
			File transactionsFile = new File("/Users/leongl/Downloads/download.csv");//download.cs
			Scanner inputStream = new Scanner(transactionsFile);
			
			while (inputStream.hasNext()) {
				
				marketWatchTransaction  =new MarketWatchTransaction();
		        String data = inputStream.nextLine();
		        marketWatchTransaction = parseTransactionsToObject(data);
		        if(marketWatchTransaction!=null )
		        	marketWatchTransactionList.add(marketWatchTransaction);
		        
		        
		    }
			inputStream.close();
		    
		   for(int i=0;i<stocksToUpdate.size();i++) {
			   System.out.println("Collecting for: "+stocksToUpdate.get(i).getSymbol());
		    	//get sell price and sell time 
		    	for(int j=0;j<marketWatchTransactionList.size();j++) {
		    		if(stocksToUpdate.get(i).isBought() && stocksToUpdate.get(i).getSymbol().equals(marketWatchTransactionList.get(j).getSymbol()) && marketWatchTransactionList.get(j).getType().equalsIgnoreCase("sell")){
		    			stocksToUpdate.get(i).setSellPrice(marketWatchTransactionList.get(j).getPrice());
		    			stocksToUpdate.get(i).setSellTime(marketWatchTransactionList.get(j).getTime());
		    			break;
		    		}
		    	}
		    	
		    //get time of max price per stock
		    //if(i==0) {
		    	String maxPriceTime = getMaxPriceTime(stocksToUpdate.get(i).getSymbol());
		    	stocksToUpdate.get(i).setMaxPricetime(maxPriceTime);
			    //System.out.println("maxPriceTime: "+stocksToUpdate.get(i).getSymbol()+", "+maxPriceTime);
			    //wait for a minute every 5 calls because of alphaVantage limitation
		    	System.out.println("stocksToUpdate: "+mapper.writeValueAsString(stocksToUpdate.get(i)));
		    	
				if(i%4==0 &i>0) {
					System.out.println("Waiting...");
					try {
						Thread.sleep(60000);
						} catch (InterruptedException e) {
							e.printStackTrace();
						} 
					} 		
		    //}
		    
				   

		    }
		   System.out.println("Done!");
		   excelUtil.updatethirdDayData(stocksToUpdate);
		    return marketWatchTransactionList;
		}catch (IOException e) {
			e.printStackTrace();
			return null;
		}
		
	}
	
	public void collectStocksIntraDay() throws JsonProcessingException {
		
		//Set <String> symbolsList = new HashSet<String>();
		Set <String> symbolsSet = new TreeSet<String>();
		
		symbolsSet = excelUtil.getAllsymbols();
		
		List<String> symbolsList = new ArrayList<String>(symbolsSet);
		System.out.println("symbolsList: "+symbolsList.size());
		System.out.println("MT index: "+symbolsList.indexOf("MT"));
		//String symbol1 = "GME";
		//System.out.println("symbolsList: "+mapper.writeValueAsString(symbolsList));
		String uri = "https://www.alphavantage.co/query?function=TIME_SERIES_INTRADAY_EXTENDED&symbol={symbol}&interval=1min&slice=year1month{month}&apikey=OHXM76UDX3U23VZG";
		
		
		RestTemplate restTemplate = new RestTemplate();
		/*
		String newUri = uri.replace("{symbol}", symbol1);
		 
		byte[] imageBytes = restTemplate.getForObject(newUri, byte[].class);
		 try {
			Files.write(Paths.get("/Users/leongl/Documents/Day3/stocks_data/"+symbol1+".csv"), imageBytes);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		*/
		int counter = 0;
		int totalCalls = 0;
		
		 long begin = System.currentTimeMillis();
		 for(int j=196;j<symbolsList.size();j++) {
			
			 String symbol = symbolsList.get(j);
			 String symbolUri = uri.replace("{symbol}", symbol);
			 
			 for(int i=1; i< 9;i++) {
				 String fullUri = symbolUri.replace("{month}", Integer.toString(i));
				 
				
				 String month = convertMonth(i);
				 //System.out.println("saving in /Users/leongl/Documents/Day3/stocks_data/"+symbol+"_"+month+".csv");
				 
				 if(totalCalls<500) {
					 System.out.println("calling "+fullUri);
					 totalCalls++;
					 byte[] imageBytes = restTemplate.getForObject(fullUri, byte[].class);
					 try {
						Files.write(Paths.get("/Users/leongl/Documents/Day3/stocks_data/"+symbol+"_"+month+".csv"), imageBytes);
											   
					} catch (IOException e) {
						System.err.println("error downloading "+symbol);
						e.printStackTrace();
					} 
					 
					 if(counter%5==0 && counter>0) {
						 long end = System.currentTimeMillis();
						 long dt = end - begin;
						 System.out.println("time after 5 runs-"+counter+": "+dt);
						 //System.out.println("begin: "+begin+", end: "+end);
						 if(dt<61000) { //
							 try {
									Thread.sleep(61000-dt); //60000
									} catch (InterruptedException e) {
										e.printStackTrace();
									}  
						 }
							
							begin = System.currentTimeMillis();
						} 
					 
					
				 }
				 counter++;
			 }
		}
		 
		 
		
		
	}

	private String convertMonth(int i) {
		String month = "";
		switch (i) {
		case 1:
			month = "June";
			break;
		case 2:
			month = "May";
			break;
		case 3:
			month = "April";
			break;
			
		case 4:
			month = "March";
			break;
		case 5:
			month = "February";
			break;
		case 6:
			month = "January";
			break;
		case 7:
			month = "December";
			break;
		case 8:
			month = "November";
			break;
		}
		
		return month;
	}

	private String getMaxPriceTime(String symbol) {
		List<AlphaVantageInfo> alphaVantageInfoList = new ArrayList<AlphaVantageInfo>();
		alphaVantageInfoList  = getIntraDayPriceListFromAlphaVantage(symbol);
		Collections.reverse(alphaVantageInfoList);
		String maxPriceTime = "";
		Double maxPrice = 0.0;
		for(AlphaVantageInfo avi:alphaVantageInfoList) {
			Double currentPrice = Double.parseDouble(avi.getHigh());
			if(currentPrice>maxPrice) {
				//System.out.println("currentPrice: "+currentPrice+", maxPrice: "+maxPrice+",time: "+avi.getTime());
				maxPrice = currentPrice;
				maxPriceTime = avi.getTime();
			}
				
			
		}
		return maxPriceTime;
	}

	private List<AlphaVantageInfo> getIntraDayPriceListFromAlphaVantage(String symbol) {
		List<AlphaVantageInfo> alphaVantageInfoList = new ArrayList<AlphaVantageInfo>();
		String uri = "https://www.alphavantage.co/query?function=TIME_SERIES_INTRADAY&symbol="+symbol+"&interval=1min&outputsize=full&apikey=QMJR32RKK2ES1V55&datatype=csv";
    	//File alphaVantageFile = new File("/Users/leongl/Downloads/intraday_1min_mara.csv");//download.cs
		
		try {
			
			InputStream inputAlphaVantage = new URL(uri).openStream();
			Scanner inputStreamAlphaVantage = new Scanner(inputAlphaVantage); //inputAlphaVantage alphaVantageFile
			int counter = 0;
			while (inputStreamAlphaVantage.hasNext() && counter < 700) { //skip previous days
				counter++;
				AlphaVantageInfo alphaVantageInfo;
		        String data = inputStreamAlphaVantage.nextLine();
		        alphaVantageInfo = mapRawAlphavAntageDataToObject(data);
		        
		        if(alphaVantageInfo!=null)
		        	alphaVantageInfoList.add(alphaVantageInfo);
		       
		    }
			inputStreamAlphaVantage.close();
			//System.out.println(alphaVantageInfoList.size());
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
			catch (IOException e) {
			e.printStackTrace();
			return null;
		}
		alphaVantageInfoList = filterLastDayTradingHoursInfo(alphaVantageInfoList);
		return alphaVantageInfoList;
	}

	private List<AlphaVantageInfo> filterLastDayTradingHoursInfo(List<AlphaVantageInfo> alphaVantageInfoList) {
		List<AlphaVantageInfo> filterLastDayInfoList = new ArrayList<AlphaVantageInfo>();
		String todaysDate = alphaVantageInfoList.get(0).getDate();
		for(AlphaVantageInfo avi:alphaVantageInfoList) {
			if(avi.getDate().equals(todaysDate)) {
				
				SimpleDateFormat sdf = new SimpleDateFormat("hh:mm:ss");
				
				try {
					Date startTime = sdf.parse("09:29:00");
					Date endTime = sdf.parse("16:01:00");
					Date time = sdf.parse(avi.getTime());
					if(time.after(startTime) && time.before(endTime))
						filterLastDayInfoList.add(avi);
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
				
		}
		
		
		return filterLastDayInfoList;
	}

	private AlphaVantageInfo mapRawAlphavAntageDataToObject(String data) {
		
		AlphaVantageInfo alphaVantageInfo = new AlphaVantageInfo();
		String[] splitData = data.split(",");
		if(splitData[0].equalsIgnoreCase("timestamp") || splitData[0].equalsIgnoreCase("{") )
			return null;
		else {
			try {
				String[] splitDate = splitData[0].split(" ");
				//alphaVantageInfo.setDatetime(parseStringtoDate(splitData[0]));
				alphaVantageInfo.setDate(splitDate[0]);
				alphaVantageInfo.setTime(splitDate[1]);
				alphaVantageInfo.setHigh(splitData[1]);
			}catch(Exception e) {
				System.err.println("datetime: "+splitData[0]);
			}
			
			
		}
		return alphaVantageInfo;
	}

	private Date parseStringtoDate(String datetime) {
		DateFormat dateFormat = new SimpleDateFormat("yyyy-mm-dd hh:MM");
		
		// TODO Auto-generated method stub
		try {
			return dateFormat.parse(datetime);
		} catch (ParseException e) {
			e.printStackTrace();
			return null;
		}
	}

	private List <MarketWatchTransaction> getLastDayTransactions(List <MarketWatchTransaction> marketWatchTransactionList) {
		
		List <MarketWatchTransaction> lastDayTransactions = new ArrayList<MarketWatchTransaction>();
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd"); 
		Date firstDate;
		try {
			firstDate = df.parse(marketWatchTransactionList.get(0).getDate());
			Date currentDate;
			//marketWatchTransactionList.stream().filter(s -> s.get)
			
			for (MarketWatchTransaction mwt: marketWatchTransactionList) {
				currentDate = new Date();
				currentDate = df.parse(mwt.getDate());
				if(firstDate.equals(currentDate))
					lastDayTransactions.add(mwt);
			}
			
			return lastDayTransactions;
		} catch (ParseException e) {
			
			e.printStackTrace();
			return lastDayTransactions;
		}
		
		
	}

	private MarketWatchTransaction parseTransactionsToObject(String data) {
		MarketWatchTransaction marketWatchTransaction  =new MarketWatchTransaction();
		String[] splitData = data.split(",");
		
		if(splitData[0].equals("Symbol"))
			return null;
		else {
			marketWatchTransaction.setSymbol(splitData[0].trim());
			
			String[] dateAndTime = splitData[2].split(" ");
			marketWatchTransaction.setDate(getDateFromTransactionMarketWatch(dateAndTime[0]));
			marketWatchTransaction.setTime(getTimeFromTransactionMarketWatch(dateAndTime[1]));
			marketWatchTransaction.setType(splitData[3]);
			marketWatchTransaction.setPrice(Double.parseDouble(splitData[splitData.length-1].replace("$", "").replace("\"", "")));
			
			return marketWatchTransaction;
		}
		
	}

	private String getTimeFromTransactionMarketWatch(String time) {
		if(time.indexOf("a")>0)
			return time.replace("a", "");
		else {
			return convert12hTo24h(time.replace("p", ""));
		}
	}

	private String convert12hTo24h(String time) {
		String[] splitTime = time.split(":");
		int hour = Integer.parseInt(splitTime[0])+12;
		String finalHour = Integer.toString(hour)+":"+splitTime[1];
		return finalHour;
	}

	private String getDateFromTransactionMarketWatch(String date ) {
		
		String[] splitDate = date.split("/");
		String month = (splitDate[0].length()==1?"0"+splitDate[0]:splitDate[0]);
		String day = (splitDate[1].length()==1?"0"+splitDate[1]:splitDate[1]);
		
		String finalDate = "20"+splitDate[2]+"-"+month+"-"+day;
		//System.out.println("finalDate: "+finalDate);
		
		return finalDate;
	}

	

	

	

	

}
