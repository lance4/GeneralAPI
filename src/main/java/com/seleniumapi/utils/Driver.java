package com.seleniumapi.utils;

import java.util.concurrent.TimeUnit;

import org.openqa.selenium.Point;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.springframework.stereotype.Component;

import io.github.bonigarcia.wdm.WebDriverManager;

@Component
public class Driver {
	
	public WebDriver instance;
	
	public void init() {
		WebDriverManager.chromedriver().setup();
		instance = new ChromeDriver();
		instance.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
		instance.manage().window().setPosition(new Point(0, 0));
		
	}
	
	public void close() {
		instance.close();
		instance = null;
	}
	

}
