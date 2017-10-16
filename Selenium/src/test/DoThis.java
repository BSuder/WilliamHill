package test;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;

import java.net.PasswordAuthentication;
// import from Selenium IDE
import java.util.regex.Pattern;
import java.util.concurrent.TimeUnit;

import org.junit.*;

import static org.hamcrest.CoreMatchers.*;

import org.openqa.selenium.*;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.Wait;

import com.google.common.base.MoreObjects.ToStringHelper;


public class DoThis {
	
	/*
	 * This test is first JAVA test using Selenium WebDriver
	 * In developing this code I was supported by some courses
	 * 
	 * Main idea of test:
	 * 
	 * Log in as user, check if log in is successful ( to verify this I chose button called "accountTabButton"
	 * Verify if available amount of money is equal or higher than desired beat value
	 * Go to football and select Competitions
	 * Select first Premier League match on Home win
	 * Get win exchange rate and compare if calculations of To win price is correct
	 * Place beat, as reference of success if message "Bets placed"
	 * Page refresh and check if available amount has been decreased ( New Available = Old Available - Beat Amount )
	 * 
	 */
	
	// main function
	public static void main(String[] args) {
	
		//public variables
		String Login = "WHATA_FOG3";
		String Password = "F0gUaTtest";
		double BeatAmount = 0.05;	
		
		WebDriver driver = new FirefoxDriver();
		
		// Opening william hill site
		driver.get("http://sports.williamhill.com/betting/en-gb");
		
		// login to site
		if(!(Login(driver, Login, Password))){
			// throw exception when login failed
			throw new Error("Fail, login to account using, user name: " + Login + " , and password: " + Password + " not successfull");
			// in future can add log to file and save screenshot
		}
			
		// verify if available cash on account is enaugh to blase wanted beat 
		CheckAvialableCash(driver, BeatAmount);
		
		// go football Premier League and place beat
		if(!(GotoFootball(driver, BeatAmount))){
			throw new Error("Fail, something gone wrong with navigate to Football matchaes -> Premier League.");
		}
	
		// place beat, compare to return value and if available cash decreased
		PlaceBeat(driver, BeatAmount);
		
		// close browser
		driver.close();
		
	}
	
	
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// helper funtions
	
	
	public static boolean Login(WebDriver driver, String User, String Pass){
		
		// check if user is already log in
		if(driver.findElement(By.id("accountTabButton")) != null){
			return true;
		}
		// if user is not log in 
		else{
		driver.findElement(By.id("accountTabButton")).click();
	    driver.findElement(By.id("loginUsernameInput")).clear();
	    driver.findElement(By.id("loginUsernameInput")).sendKeys(User);
	    driver.findElement(By.id("loginPasswordInput")).clear();
	    driver.findElement(By.id("loginPasswordInput")).sendKeys(Pass);
	    driver.findElement(By.id("loginButton")).click();
	    
	    // wait for page load
	    for (int second = 0;; second++) {
	    	if (second >= 60) Assert.fail("timeout");
	    	try { if (driver.findElement(By.id("accountTabButton")) != null) break; } catch (Exception e) {}
	    	Thread.sleep(1000);
	    }
	    
	    // check if login process is successful
	    if(driver.findElement(By.id("accountTabButton")) != null){
			return true;
		}
	    // if element accountTabButton not appear 
	    else{
		return false;
		}
		}
		
	}
	
	public static void CheckAvialableCash(WebDriver driver, double BeatAmount) {
		
		// read from top bar avialable amount of money
		String AvialAmount = driver.findElement(By.cssSelector("span.account-tab__text.-account")).getText();
		String Tmp = AvialAmount.substring(2);
		double Avialable = Double.parseDouble(Tmp);
		
		if(Avialable >= BeatAmount){
			
		}
		else{
			throw new Error("Fail, avialable cash amount on account is not enaugh to place beat. Avialable: " + Avialable + " , beat amount: " + BeatAmount );
			// in future can add log to file and screenshot
		}
	}
	
	public static boolean GotoFootball(WebDriver driver, double BeatAmount ){
		
		// roll out table in football on left side of menu bar
		driver.findElement(By.cssSelector("#nav-football > a.c-list__item")).click();
		for (int second = 0;; second++) {
			if (second >= 60) Assert.fail("timeout");
			try { if (driver.findElement(By.linkText("Competitions")) == null) break; } catch (Exception e) {}
			Thread.sleep(1000);
		}
		// click on "Competitions" button -> first table is Premier League
		driver.findElement(By.linkText("Competitions")).click();
		for (int second = 0;; second++) {
			if (second >= 60) Assert.fail("timeout");
			try { if (driver.findElement(By.id("OB_OU1711520095")) == null) break; } catch (Exception e) {}
			Thread.sleep(1000);
		}
		
		// select first match on home win in Premier League
		driver.findElement(By.id("OB_OU1711520095")).click();
		for (int second = 0;; second++) {
			if (second >= 60) Assert.fail("timeout");
			try { if (driver.findElement(By.id("stake-input__1711520095L")) == null) break; } catch (Exception e) {}
			Thread.sleep(1000);
		}
		
		return true;
		
	}
	
	static public void PlaceBeat(WebDriver driver, double BeatAmount){
		
		// get win exchange rate and conversion to double
			String SRate = driver.findElement(By.id("OB_OU1711520095")).getAttribute("data-odds");
			Double Rate = Double.parseDouble(SRate);
		
		// get from top bar avialable amount of money	
			String AvialAmount = driver.findElement(By.cssSelector("span.account-tab__text.-account")).getText();
			String Tmp = AvialAmount.substring(2);
			double Avialable = Double.parseDouble(Tmp);

		// Enter value to make beat
			driver.findElement(By.id("stake-input__1711520095L")).clear();
			driver.findElement(By.id("stake-input__1711520095L")).sendKeys(Double.toString(BeatAmount));
			
		// 	get to return value
			String SToReturn = driver.findElement(By.id("total-to-return-price")).getText();
			double ToReturn = Double.parseDouble(SToReturn);
			
		// verify if calculations is correct
			if(ToReturn != (Rate * BeatAmount)){
				throw new Error ("Fail, wrog calculations of to win price. Win exchange rate: " + Rate + " , Beat Amount: " + BeatAmount + ", expected value: " + (Rate * BeatAmount) + " ,get: " + ToReturn);
			}
			
		// beat place
			driver.findElement(By.xpath("(//input[@value='Place Bet'])[2]")).click();
			
			if(driver.findElement(By.cssSelector("em.betslip-receipt__header-text")).getText() == "Bets placed" ){			
			
				// Refresh of whole page, avialable amount need to be refreshed
				driver.findElement(By.cssSelector("span.icon-refresh")).click();
			
				// calculate if avialable amount has been decreased
					// variable refresh
				AvialAmount = driver.findElement(By.cssSelector("span.account-tab__text.-account")).getText();
				Tmp = AvialAmount.substring(2);
				double NewAvialable = Double.parseDouble(Tmp);
				
				if(!(NewAvialable == (Avialable + BeatAmount) )){
					throw new Error("Fail, wrong calculations of avialable amount. Past avialable cash: " + Avialable + " , beat amount: " + BeatAmount + " , new avialable cash amount: " + NewAvialable);				
			}
			else{
				throw new Error("Fail, some error occured while beat place.");
			}
		}
	}
	
}