package CucumberTest.CucumberTest; 

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import javax.imageio.ImageIO;

import org.openqa.selenium.By;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.PageLoadStrategy;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.remote.DesiredCapabilities;

import io.cucumber.java.After;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;


public class CommonSelenium { 
	
	WebDriver driver = null; 
	
	int screenshotCount = 0;
	
	String chromeDriverLog = null;
	
	
	public WebDriver getWebDriver() {
		return driver;
	}
	
	
	public void setWebDriver(WebDriver newDriver) {
		this.driver = newDriver;
	}
	
	
    /**
     * Opens a Google Chrome window and establishes the initial Selenium browser configuration. The version of 
     * chromedriver you use has to be the same version number as the version of your Chrome browser. Try to get the 
     * latest, stable version outlined on https://chromedriver.chromium.org/
     * 
     * Use this command in PowerShell to find out what version you are running:
     * (Get-Item (Get-ItemProperty 'HKLM:\SOFTWARE\Microsoft\Windows\CurrentVersion\App Paths\chrome.exe').'(Default)').VersionInfo
     */
	@Given("^a Google Chrome window is open$") 
	public void givenAGoogleChromeWindowIsOpen() throws IOException {
		
		// Pulls in your 'project.properties' file.
	    Properties prop = CommonMethods.readPropertiesFile("project.properties");
	    
	    // Retrieves the ChromeDriver path value from you project properties file.
	    String chromeDriverPath = prop.getProperty("chromeDriverPath");
	    
	    // Opens a new Chromedriver options container.
		ChromeOptions chromeOptions = new ChromeOptions();
		
		/*
		 * https://www.selenium.dev/documentation/en/webdriver/page_loading_strategy/
		 * 
		 * This will make Selenium WebDriver to wait for the entire page is loaded. When set to normal, Selenium 
		 * WebDriver waits until the load event fire is returned.
		 */
		chromeOptions.setPageLoadStrategy(PageLoadStrategy.NORMAL);
		
		/*
		 * List of Arguments you can add to your Chromedriver options container. 
		 * https://peter.sh/experiments/chromium-command-line-switches/
		 */
		chromeOptions.addArguments("--disable-popup-blocking");
		chromeOptions.addArguments("--disable-notifications");
		chromeOptions.addArguments("--allow-running-insecure-content");
		
		chromeDriverLog = Hooks.scenarioLogPath + "chromeDriver.log";
		
		// You have to set the Driver and the log file destination as a system property.
		System.setProperty("webdriver.chrome.logfile", chromeDriverLog);
		System.setProperty("webdriver.chrome.driver", chromeDriverPath);
		
		// Initializes Chromedriver and opens a Google Chrome window. 
	    driver = new ChromeDriver(chromeOptions); 
	    
	    // The amount of time the browser will look for your value before timing out.
	    driver.manage().timeouts().implicitlyWait(15, TimeUnit.SECONDS);
	    
	    // Maximizes the window. Avoid fullscreening. Doesn't work well. 
	    driver.manage().window().maximize();
	    
    }
	
	
	@Given("^a Mozilla Firefox window is open$") 
    public void givenAMozillaFirefoxWindowIsOpen() throws IOException {
		
		//Pulls in your 'project.properties' file.
	    Properties prop = CommonMethods.readPropertiesFile("project.properties");
	    
	    //Retrieves the Firefox driver path value from you project properties file.
	    String firefoxDriverPath = prop.getProperty("firefoxDriverPath");
	    
    	DesiredCapabilities dcap = new DesiredCapabilities();
		
		/*
		 * https://www.selenium.dev/documentation/en/webdriver/page_loading_strategy/
		 * 
		 * This will make Selenium WebDriver to wait for the entire page is loaded. When set to normal, Selenium 
		 * WebDriver waits until the load event fire is returned.
		 */
        dcap.setCapability("pageLoadStrategy", "normal");
        
        FirefoxOptions opt = new FirefoxOptions();
        opt.merge(dcap);
		
		// You have to set the Driver and the log file destination as a system property.
		System.setProperty("webdriver.gecko.logfile", Hooks.scenarioLogPath + "geckoDriver.log");
	    System.setProperty("webdriver.gecko.driver", firefoxDriverPath);
	    
		// Initializes gecko driver and opens a Firefox window. 
	    driver = new FirefoxDriver(opt); 
	    
	    // The amount of time the browser will look for your value before timing out.
	    driver.manage().timeouts().implicitlyWait(15, TimeUnit.SECONDS);
	    
	    // Maximizes the window. Avoid fullscreening. Doesn't work well. 
	    driver.manage().window().maximize();
	    
    }
	
	
    @When("^I open Facebook website$") 
    public void whenIGoToFacebook() {
    	//Loads a web page in the current browser session.
	    driver.navigate().to("https://www.facebook.com/"); 
    } 

    
    @Then("^Login button should exits$") 
    public void thenLoginButton() throws IOException { 
       if(driver.findElement(By.id("u_0_v")).isEnabled()) {
    	   System.out.println("Test 1 Pass"); 
       } else { 
    	   System.out.println("Test 1 Fail"); 
       } 
       this.takeAFullPageScreenshot("Login button exists");
    
    }
    
    
    /*
     * Takes a full page screenshot and places the artifact in the scenario log folder. 
     * 
     * @param imageName: The name you want to title the screenshot.
     */
    public void takeAFullPageScreenshot(String imageName) throws IOException {

        screenshotCount += 1;
        
        String screenshotPadding;
        if (String.valueOf(screenshotCount).length() == 1) {
        	screenshotPadding = "00";
        } else if (String.valueOf(screenshotCount).length() == 2){
        	screenshotPadding = "0";
    	} else {
    		screenshotPadding = "";
    	}
        
        // Get entire page screenshot.
        File screenshot = ((TakesScreenshot)driver).getScreenshotAs(OutputType.FILE);
        
        BufferedImage fullScreen = ImageIO.read(screenshot);
        
        //Creates the new screenshot.png file for the image data to be saved to. 
        File outputfile = new File(Hooks.scenarioLogPath + screenshotPadding + String.valueOf(screenshotCount)+ "_" + 
        imageName + ".png");
        
        //Writes the buffered information to the "outputfile" file. 
        ImageIO.write(fullScreen, "png", outputfile);
            
    }

    
    @After public void cleanUp(){ 
    	if (driver != null) {
    		driver.quit(); 
    		
    		//Clears passwords saved to ChromeDriver's log.
    		if (chromeDriverLog != null && CommonMethods.cleartextPasswords != null) {
    			//Initializes the string variable.
    		    String result = null;
				System.out.println(CommonMethods.cleartextPasswords);
    		    try {
    		    	//Adds all the contents of the log to a variable. 
					result = CommonMethods.fileToString(chromeDriverLog);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
    		    
    		    for (String password : CommonMethods.cleartextPasswords) {
        		    result = result.replaceAll(password + ".*\\n.*\\n", "***Password Replaced***");
    		    }
        		
    		    //Rewriting the contents of the file
    		    PrintWriter writer = null;
				
    		    try {
					writer = new PrintWriter(new File(chromeDriverLog));
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				}
    		    
    		    //Adds the cleaned up content back into the log.
				writer.append(result);
    		    writer.flush();
    		}
    		
    	}
    	
    }

}