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

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Color;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;

import com.seleniumapi.dto.BoughtStockToUpdate;
import com.seleniumapi.dto.ExcelStockStat;
import com.seleniumapi.dto.FilteredStock;
import com.seleniumapi.dto.Price;

@Component
public class ExcelUtil {

	public List<String> getSymbolsFromExcel(String inputDate) {
		
		List<String> stocksToUpdate = new ArrayList<String>();
		
		String excelFilePath = "/Users/leongl/Documents/Day3/Track.xlsx";
        FileInputStream inputStream;
		try {
			inputStream = new FileInputStream(new File(excelFilePath));
			Workbook workbook;
			workbook = new XSSFWorkbook(inputStream);
			Sheet stocks2Sheet = workbook.getSheet("stock2.0");
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
	
	public List<ExcelStockStat> getExcelStockStat(){
		String excelFilePath = "/Users/leongl/Documents/Day3/Track.xlsx";
        FileInputStream inputStream;
        List<ExcelStockStat> excelStockStat = new ArrayList<ExcelStockStat>();
        try {
        	inputStream = new FileInputStream(new File(excelFilePath));
			Workbook workbook;
			workbook = new XSSFWorkbook(inputStream);
			Sheet stocks2Sheet = workbook.getSheet("stock2.0"); //Sheet4
			
			for(int i= 2; i < stocks2Sheet.getLastRowNum()-1; i++) {
				ExcelStockStat singleStock = new ExcelStockStat();
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
