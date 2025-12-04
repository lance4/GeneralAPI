package com.seleniumapi.repositories;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.seleniumapi.dto.ExcelStock;

@Repository
public class StockRepository {
	
	@Autowired
	private JdbcTemplate jdbcTemplate;
	
	public int count() {
		String query ="select count(*) from stocks;";
		return jdbcTemplate.queryForObject(query, Integer.class);
	}
	
	public int updateStocks(List<ExcelStock> stocksList) {
		
		String query = "insert into stocks(date, symbol, price, change_in_price, volume, three_months_avg, market_cap, adv, price_day2, gain_day2, price_day3, gain_day3, open_price, high_price"
				+",low_price, close_price, max_1, max_2)"
				+" VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);";
		System.out.println("query: "+query);
		int[] batchUpdate = jdbcTemplate.batchUpdate(query, new BatchPreparedStatementSetter() {
			
			@Override
			public void setValues(PreparedStatement ps, int i) throws SQLException {
				ExcelStock s = stocksList.get(i);
				ps.setString(1, s.getDate());
				ps.setString(2, s.getSymbol());
				ps.setBigDecimal(3, new BigDecimal(s.getPrice()));
				ps.setBigDecimal(4, new BigDecimal(s.getChange()));
				
				ps.setInt(5, s.getVolume().intValue());
				ps.setString(6, s.getThreeMonthsAvg());
				ps.setString(7, s.getMarketCap());
				ps.setInt(8, s.getAdv().intValue());
				ps.setBigDecimal(9, new BigDecimal(s.getPricaeDay2()));
				ps.setBigDecimal(10, new BigDecimal(s.getGainDay2()));
				ps.setBigDecimal(11, new BigDecimal(s.getPricaeDay3()));
				ps.setBigDecimal(12, new BigDecimal(s.getGainDay3()));
				ps.setBigDecimal(13, new BigDecimal(s.getOpenPrice()));
				ps.setBigDecimal(14, new BigDecimal(s.getHighPrice()));
				ps.setBigDecimal(15, new BigDecimal(s.getLowPrice()));
				ps.setBigDecimal(16, new BigDecimal(s.getClosePrice()));
				ps.setBigDecimal(17, new BigDecimal(s.getMax1()));
				ps.setBigDecimal(18, new BigDecimal(s.getMax2()));
				
			}
			
			@Override
			public int getBatchSize() {
				// TODO Auto-generated method stub
				return stocksList.size();
			}
		});
		
		return batchUpdate.length;
		
	}

}
