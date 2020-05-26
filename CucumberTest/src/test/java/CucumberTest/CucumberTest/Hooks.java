package CucumberTest.CucumberTest;

import java.io.IOException;
import java.util.Properties;

import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;

public class Hooks {
	
	PsStream psObj = new PsStream();
	
	static String scenarioLogPath;
	
	
	@Before
    public void beforeScenario(Scenario scenario) throws IOException{
		
		Properties projectProp = CommonMethods.readPropertiesFile("project.properties");
		
		String scenarioName = scenario.getName();
		String featureName = scenario.getId();
		featureName = featureName.replaceAll(".*\\/", "");
		featureName = featureName.replaceAll("\\..*", "");
		
		scenarioLogPath = projectProp.getProperty("logPath") + "\\\\\\\\" + "testRun_" + 
				RunCucumberTest.testRunDateTime + "\\\\\\\\" + featureName + "\\\\\\\\"+ scenarioName + "\\\\\\\\";
		CommonMethods.assureDirectoryExists(scenarioLogPath);
        
    } 
	
	
	@After
    public void afterScenario(Scenario scenario){
		;//psObj.close();
    }

}
