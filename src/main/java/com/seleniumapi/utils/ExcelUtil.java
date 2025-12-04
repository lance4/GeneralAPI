package com.seleniumapi.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.apache.catalina.mapper.Mapper;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Color;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.seleniumapi.dto.BoughtStockToUpdate;
import com.seleniumapi.dto.ExcelStock;
import com.seleniumapi.dto.ExcelStockStatV2;
import com.seleniumapi.dto.FilteredStock;
import com.seleniumapi.dto.Price;
import com.seleniumapi.services.seleniumAPIService;

@Component
public class ExcelUtil {
	private  ObjectMapper mapper = new ObjectMapper();
	
	@Autowired
	private seleniumAPIService seleniumAPIService;

	public List<String> getSymbolsFromExcel(String inputDate) {
		
		List<String> stocksToUpdate = new ArrayList<String>();
		
		String excelFilePath = "/Users/leongl/Documents/Day3/Track.xlsx";
        FileInputStream inputStream;
		try {
			inputStream = new FileInputStream(new File(excelFilePath));
			Workbook workbook;
			workbook = new XSSFWorkbook(inputStream);
			Sheet stocks2Sheet = workbook.getSheet("Stock");
			//TODO fix
			for(int i= 400; i <= stocks2Sheet.getLastRowNum(); i++) {
				Row row = stocks2Sheet.getRow(i);
				
				if(row.getCell(0).getDateCellValue()!=null) {
					Date dateCellValue = row.getCell(0).getDateCellValue();
					DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		            String cellValue = df.format(dateCellValue);
		   
		            if(cellValue.trim().equalsIgnoreCase(inputDate))
		            	if(row.getCell(1).getCellType() == CellType.STRING)
		            		stocksToUpdate.add(row.getCell(1).getStringCellValue().trim());
				}
				
	            
			}
			workbook.close();
	        inputStream.close();
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}catch (IOException e) {
			e.printStackTrace();
		}
         
        
		
       

        
        
		return stocksToUpdate;
	}

	public List<FilteredStock> updateExcel() {
		String excelFilePath = "/Users/leongl/Documents/Day3/Track.xlsx";
        FileInputStream inputStream;
        List<FilteredStock> stocksToUpdate = new ArrayList<FilteredStock>();
        
		try {
			inputStream = new FileInputStream(new File(excelFilePath));
			Workbook workbook;
			workbook = new XSSFWorkbook(inputStream);
			Sheet stocks2Sheet = workbook.getSheet("Sheet4");
			
			for(int i= 2; i < stocks2Sheet.getLastRowNum()-1; i++) {
				FilteredStock price = new FilteredStock();
				Row row = stocks2Sheet.getRow(i);
				if(row.getCell(0).getDateCellValue()!=null) {
					Date dateCellValue = row.getCell(0).getDateCellValue();
					DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		            String cellValue = df.format(dateCellValue);
		           
		            if(row.getCell(0).getCellType() == CellType.NUMERIC && row.getCell(1).getCellType() == CellType.STRING) {
		            	price.setDate(cellValue);
		            	price.setSymbol(row.getCell(1).getStringCellValue().trim());
		            	stocksToUpdate.add(price);
		            }
		            	
		            	
				}
				
	            
			}
			
			workbook.close();
	        inputStream.close();
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}catch (IOException e) {
			e.printStackTrace();
		}
		return stocksToUpdate;
	}
	
	public List<ExcelStock> getExcelStockStat(){
		String excelFilePath = "/Users/leongl/Documents/Day3/Track.xlsx";
        FileInputStream inputStream;
        List<ExcelStock> excelStockList = new ArrayList<>();
        try {
        	inputStream = new FileInputStream(new File(excelFilePath));
			Workbook workbook;
			workbook = new XSSFWorkbook(inputStream);
			Sheet stocksSheet = workbook.getSheet("Stock"); //Sheet4
			ExcelStock singleStock;
			for(int i= 1; i < stocksSheet.getLastRowNum()-1; i++) { //stocksSheet.getLastRowNum()-1
				
				
				Row row = stocksSheet.getRow(i);
				
				if(row.getCell(0).getStringCellValue()!=null) {
					singleStock = new ExcelStock();
					String date = row.getCell(0).getStringCellValue().replaceAll("/", "-");
					String symbol = row.getCell(1).getStringCellValue().trim();
					List<Price> stocksDataApi = seleniumAPIService.callThreeDatsData(symbol, date);
					System.out.println("i: "+i);
					//System.out.println("stocksDataApi.get(2): "+mapper.writeValueAsString(stocksDataApi));
					singleStock.setDate(date);
	            	singleStock.setSymbol(symbol);
	            	singleStock.setName(row.getCell(2).getStringCellValue());
	            	singleStock.setPrice(row.getCell(3).getNumericCellValue());
	            	singleStock.setChange(row.getCell(4).getNumericCellValue());
	            	singleStock.setVolume(row.getCell(5).getCellType()== CellType.BLANK? stocksDataApi.get(0).getVolume():row.getCell(5).getNumericCellValue());
	            	singleStock.setThreeMonthsAvg(row.getCell(6).getStringCellValue());
	            	singleStock.setMarketCap(row.getCell(7).getStringCellValue());
	            	singleStock.setAdv(row.getCell(8).getNumericCellValue());
	            	singleStock.setPricaeDay2(row.getCell(10).getCellType()== CellType.BLANK?stocksDataApi.get(1).getClose():row.getCell(10).getNumericCellValue() );
	            	singleStock.setGainDay2(row.getCell(11)!=null && row.getCell(11).getCellType()!= CellType.BLANK?row.getCell(11).getNumericCellValue():(singleStock.getPricaeDay2()-singleStock.getPrice())/singleStock.getPrice()*100);
	            	singleStock.setPricaeDay3(row.getCell(12)!=null && row.getCell(12).getCellType()!= CellType.BLANK?row.getCell(12).getNumericCellValue():singleStock.getPricaeDay2()*1.03);
	            	singleStock.setGainDay3(row.getCell(13)!=null && row.getCell(13).getCellType()!= CellType.BLANK? row.getCell(13).getNumericCellValue():(singleStock.getPricaeDay3()-singleStock.getPricaeDay2())/singleStock.getPricaeDay2()*100);
	            	
	            	singleStock.setOpenPrice(row.getCell(14)!=null && row.getCell(14).getCellType()!= CellType.BLANK?row.getCell(14).getNumericCellValue():stocksDataApi.get(2).getOpen());
	            	singleStock.setHighPrice(row.getCell(15)!=null && row.getCell(15).getCellType()!= CellType.BLANK?row.getCell(15).getNumericCellValue():stocksDataApi.get(2).getHigh());
	            	singleStock.setLowPrice(row.getCell(16)!=null && row.getCell(16).getCellType()!= CellType.BLANK?row.getCell(16).getNumericCellValue():stocksDataApi.get(2).getLow());
	            	singleStock.setClosePrice(row.getCell(17)!=null && row.getCell(17).getCellType()!= CellType.BLANK?row.getCell(17).getNumericCellValue():stocksDataApi.get(2).getClose());
	            	
	            	singleStock.setMax1(row.getCell(18)!=null && row.getCell(18).getCellType() != CellType.BLANK?row.getCell(18).getNumericCellValue():(singleStock.getHighPrice()-singleStock.getPricaeDay2())/singleStock.getPricaeDay2()*100); 
	            	
	            	//
	            	//System.out.println("singleStock.getHighPrice(): "+singleStock.getHighPrice() +", singleStock.getOpenPrice(): "+singleStock.getOpenPrice());
	            	singleStock.setMax2(row.getCell(19)!=null && row.getCell(19).getCellType() != CellType.BLANK?row.getCell(19).getNumericCellValue(): (singleStock.getHighPrice()-singleStock.getOpenPrice())/singleStock.getOpenPrice()*100);
            		
	            	
	            	
	            	//System.out.println("singleStock: "+mapper.writeValueAsString(singleStock));
	            	excelStockList.add(singleStock);
	            	//stocksToUpdate.add(price);
		           
		            	
		            
		            	
		            	
				}
				
	            
			}
			
			workbook.close();
	        inputStream.close();
        }catch(Exception e) {
        	e.printStackTrace();
	       
        }
        
        return excelStockList;
        
	}
	
	public List<ExcelStockStatV2> getExcelStockStatV2(){
		String excelFilePath = "/Users/leongl/Documents/Day3/Track.xlsx";
        FileInputStream inputStream;
        List<ExcelStockStatV2> excelStockStat = new ArrayList<ExcelStockStatV2>();
        try {
        	inputStream = new FileInputStream(new File(excelFilePath));
			Workbook workbook;
			workbook = new XSSFWorkbook(inputStream);
			Sheet stocks2Sheet = workbook.getSheet("stock2.0"); //Sheet4
			
			for(int i= 2; i < stocks2Sheet.getLastRowNum()-1; i++) {
				ExcelStockStatV2 singleStock = new ExcelStockStatV2();
				Price day2 = new Price();
				Price day3 = new Price();
				Row row = stocks2Sheet.getRow(i);
				if(row.getCell(0).getDateCellValue()!=null) {
					Date dateCellValue = row.getCell(0).getDateCellValue();
					DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		            String cellValue = df.format(dateCellValue);
		           
		            if(row.getCell(0).getCellType() == CellType.NUMERIC && row.getCell(1).getCellType() == CellType.STRING) {
		            	singleStock.setDate(cellValue);
		            	singleStock.setSymbol(row.getCell(1).getStringCellValue().trim());
		            	singleStock.setDay1ClosingPrice(row.getCell(2).getNumericCellValue());
		            	singleStock.setAvgProfit(row.getCell(3).getNumericCellValue());
		            	singleStock.setSuccessRate(row.getCell(4).getNumericCellValue());
		            	singleStock.setScore(row.getCell(5).getNumericCellValue());
		            	day2.setOpen(row.getCell(6).getNumericCellValue());
		            	day2.setHigh(row.getCell(7).getNumericCellValue());
		            	day2.setLow(row.getCell(8).getNumericCellValue());
		            	day2.setClose(row.getCell(9).getNumericCellValue());
		            	singleStock.setDay2(day2);
		            	day3.setOpen(row.getCell(10).getNumericCellValue());
		            	day3.setHigh(row.getCell(11).getNumericCellValue());
		            	day3.setLow(row.getCell(12).getNumericCellValue());
		            	day3.setClose(row.getCell(13).getNumericCellValue());
		            	singleStock.setDay3(day3);
		            	singleStock.setMaxGain(row.getCell(14).getNumericCellValue());
		            	singleStock.setOpenGain(row.getCell(15).getNumericCellValue());
		            	singleStock.setPotentialGain(row.getCell(16).getNumericCellValue());
		            	singleStock.setMaxGainDay2(row.getCell(17).getNumericCellValue());
		            	singleStock.setOpenGainDay2(row.getCell(18).getNumericCellValue());
		            	singleStock.setPotentialGainDay2(row.getCell(19).getNumericCellValue());
		            	excelStockStat.add(singleStock);
		            	//stocksToUpdate.add(price);
		            }
		            	
		            	
				}
				
	            
			}
			
			workbook.close();
	        inputStream.close();
        }catch(Exception e) {
        	e.printStackTrace();
        }
        
        return excelStockStat;
        
	}

	public List<BoughtStockToUpdate> getBoughtStocks(String date) {
		String excelFilePath = "/Users/leongl/Documents/Day3/Track.xlsx";
        FileInputStream inputStream;
        List<BoughtStockToUpdate> symbols = new ArrayList<BoughtStockToUpdate>();
        
        try {
			inputStream = new FileInputStream(new File(excelFilePath));
			Workbook workbook;
			workbook = new XSSFWorkbook(inputStream);
			Sheet stocks2Sheet = workbook.getSheet("stock2.0");
			
			for(int i=2;i<stocks2Sheet.getLastRowNum();i++) {
				Row row = stocks2Sheet.getRow(i);
				Date dateCellValue = row.getCell(0).getDateCellValue();
				DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
	            String cellValue = df.format(dateCellValue);
	            
	            if(date.equals(cellValue)) {
	            	//System.out.println("success, "+row.getCell(1).getStringCellValue().trim() );
	            	BoughtStockToUpdate boughtStockToUpdate = new BoughtStockToUpdate();
	            	boughtStockToUpdate.setSymbol(row.getCell(1).getStringCellValue().trim());
	            	boughtStockToUpdate.setLineToUpdate(i);
	            	CellStyle cellStyle = row.getCell(1).getCellStyle();
	            	Color color = cellStyle.getFillForegroundColorColor();
	            	if(color!=null) 
	            		boughtStockToUpdate.setBought(true);
	            	else
	            		boughtStockToUpdate.setBought(false);
	            	
	            	
	            	symbols.add(boughtStockToUpdate);
	            }
				
			}
			workbook.close();
	        inputStream.close();
		} catch (Exception e) {
			
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
       
		return symbols;
	}

	public void updatethirdDayData(List<BoughtStockToUpdate> stocksToUpdate) {
		String excelFilePath = "/Users/leongl/Documents/Day3/Track.xlsx";
        FileInputStream inputStream;
        try {
			inputStream = new FileInputStream(new File(excelFilePath));
			Workbook workbook;
			workbook = new XSSFWorkbook(inputStream);
			Sheet stocks2Sheet = workbook.getSheet("stock2.0");
			

			for(BoughtStockToUpdate stu:stocksToUpdate) {
				Row row = stocks2Sheet.getRow(stu.getLineToUpdate());
				
				
				if(stu.getMaxPricetime()!=null) {
					Cell maxPriceTimeCell = row.getCell(21);
					if(maxPriceTimeCell==null) {
						maxPriceTimeCell = row.createCell(21);
					}
					maxPriceTimeCell.setCellValue(stu.getMaxPricetime());
				}
					
				
				if(stu.isBought()) {
					Cell sellPriceCell = row.getCell(16);
					if(sellPriceCell==null) {
						sellPriceCell = row.createCell(16); 
					}
					sellPriceCell.setCellValue(stu.getSellPrice());
					Cell sellTimeCell = row.getCell(20);
					if(sellTimeCell==null) {
						sellTimeCell = row.createCell(20); 
					}
					sellTimeCell.setCellValue(stu.getSellTime());
				}
			}
			try (FileOutputStream outputStream = new FileOutputStream("/Users/leongl/Documents/Day3/Track.xlsx")) {
	            workbook.write(outputStream);
	        }
			workbook.close();
	        inputStream.close();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
        

		
	}

	

	public Set<String> getAllsymbols() {
		String excelFilePath = "/Users/leongl/Documents/Day3/Track.xlsx";
        FileInputStream inputStream;
        Set <String> symbolsList = new TreeSet<String>();
        try {
        	inputStream = new FileInputStream(new File(excelFilePath));
			Workbook workbook;
			workbook = new XSSFWorkbook(inputStream);
			Sheet stocks2Sheet = workbook.getSheet("stock2.0");
			
			for(int i= 2; i <= stocks2Sheet.getLastRowNum(); i++) {
				Row row = stocks2Sheet.getRow(i);
				
				if(row.getCell(1).getStringCellValue()!=null) {
					String symbol = row.getCell(1).getStringCellValue().trim();
					symbolsList.add(symbol);
		   
		        }
			}
			workbook.close();
	        inputStream.close();
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}catch (IOException e) {
			e.printStackTrace();
		}
		return symbolsList;
	}

	public void insertAdvancedStocks(List<FilteredStock> advancedStocks) {
		// TODO Auto-generated method stub
		
	}
	
	 

}
