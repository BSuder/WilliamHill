package com.example.tests;

import java.util.regex.Pattern;
import java.util.concurrent.TimeUnit;
import org.junit.*;
import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;
import org.openqa.selenium.*;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.Select;

public class WilliamHillPlaceBet {
  private WebDriver driver;
  private String baseUrl;
  private boolean acceptNextAlert = true;
  private StringBuffer verificationErrors = new StringBuffer();

  @Before
  public void setUp() throws Exception {
    driver = new FirefoxDriver();
    baseUrl = "https://www.google.pl/";
    driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
  }

  @Test
  public void testWilliamHillPlaceBet() throws Exception {
    // Variables for login and amount of bet
    // ERROR: Caught exception [Error: locator strategy either id or name must be specified explicitly.]
    String Login = "WHATA_FOG3";
    String Password = "F0gUaTtest";
    // Openning main site od williamhill
    driver.get("http://sports.williamhill.com/betting/en-gb");
    // LoginProcess
    driver.findElement(By.id("accountTabButton")).click();
    driver.findElement(By.id("loginUsernameInput")).clear();
    driver.findElement(By.id("loginUsernameInput")).sendKeys(Login);
    driver.findElement(By.id("loginPasswordInput")).clear();
    driver.findElement(By.id("loginPasswordInput")).sendKeys(Password);
    driver.findElement(By.id("loginButton")).click();
    // Wait for page load after log in
    for (int second = 0;; second++) {
    	if (second >= 60) fail("timeout");
    	try { if (isElementPresent(By.id("accountTabButton"))) break; } catch (Exception e) {}
    	Thread.sleep(1000);
    }

    // If avialable amount is lower than wanted to bet amount test should stop
    String AvialAmount = driver.findElement(By.cssSelector("span.account-tab__text.-account")).getText();
    // I was not able to handle converssion from text '£12.80' to double '12.8' in Selenium IDE,
    // It is possible in Java, first currency code must be cut, eg. String Tmp = AvialAmount.substring(2); 
    // Next converssion from string to double double Avialable = Double.parsedouble(Tmp);

// Navigate to Football and Premier Leauge
driver.findElement(By.cssSelector("#nav-football > a.c-list__item")).click();
for (int second = 0;; second++) {
	if (second >= 60) fail("timeout");
	try { if (isElementPresent(By.linkText("Competitions"))) break; } catch (Exception e) {}
	Thread.sleep(1000);
}

driver.findElement(By.linkText("Competitions")).click();
for (int second = 0;; second++) {
	if (second >= 60) fail("timeout");
	try { if (isElementPresent(By.id("OB_OU1711520095"))) break; } catch (Exception e) {}
	Thread.sleep(1000);
}

// Click to make bet for first match in Premier Leauge on Home win
driver.findElement(By.id("OB_OU1711520095")).click();
for (int second = 0;; second++) {
	if (second >= 60) fail("timeout");
	try { if (isElementPresent(By.id("stake-input__1711520095L"))) break; } catch (Exception e) {}
	Thread.sleep(1000);
}

// Enter value to make bet
driver.findElement(By.id("stake-input__1711520095L")).clear();
driver.findElement(By.id("stake-input__1711520095L")).sendKeys(BetAmount);
// Verify amount to win based on: BetAmount * win exchange rate (from web - data-odds)
String Rate = driver.findElement(By.id("OB_OU1711520095")).getAttribute("id=OB_OU1711520095");
assertTrue(driver.findElement(By.id("total-to-return-price")).getAttribute("value").matches("^\\$\\{BetAmount [\\s\\S]* Rate\\}$"));
// Place bet
driver.findElement(By.xpath("(//input[@value='Place Bet'])[2]")).click();
assertEquals("Bets placed", driver.findElement(By.cssSelector("em.betslip-receipt__header-text")).getText());
// Refresh of whole page, avialable amount need to be refreshed
driver.findElement(By.cssSelector("span.icon-refresh")).click();
// Verify if avialable amoun has been decreased after bet
// The same issue as in beginnig of test, I was not able to convert text '£12.80' to double '£2.80' in Selenium IDE,
//  it is possible in Java, in the same way as mentioned before
assertEquals("${AvialAmount - BetAmount}", driver.findElement(By.cssSelector("span.account-tab__text.-account")).getAttribute("value"));
// ERROR: Caught exception [unknown command []]
  }

  @After
  public void tearDown() throws Exception {
    driver.quit();
    String verificationErrorString = verificationErrors.toString();
    if (!"".equals(verificationErrorString)) {
      fail(verificationErrorString);
    }
  }

  private boolean isElementPresent(By by) {
    try {
      driver.findElement(by);
      return true;
    } catch (NoSuchElementException e) {
      return false;
    }
  }

  private boolean isAlertPresent() {
    try {
      driver.switchTo().alert();
      return true;
    } catch (NoAlertPresentException e) {
      return false;
    }
  }

  private String closeAlertAndGetItsText() {
    try {
      Alert alert = driver.switchTo().alert();
      String alertText = alert.getText();
      if (acceptNextAlert) {
        alert.accept();
      } else {
        alert.dismiss();
      }
      return alertText;
    } finally {
      acceptNextAlert = true;
    }
  }
}
