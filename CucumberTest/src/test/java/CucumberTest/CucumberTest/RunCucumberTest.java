package CucumberTest.CucumberTest;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;


//https://cucumber.io/docs/cucumber/api/

@RunWith(Cucumber.class)
@CucumberOptions(plugin = {"pretty", "html:target/cucumber-results/html",
				 		   "json:target/cucumber-results/cucumber.json",
				 		   "junit:target/cucumber-results/cucumber.xml"},
				 monochrome = true,
				 //driRun = false,
				 strict = true, 
				 //snippets = SnippetType.CAMELCASE,
				 features = "classpath:",
				 tags = "@tag1"
				 //glue = {"classpath:CucumberTest.CucumberTeset"}
				 )


public class RunCucumberTest {
	
	static String testRunDateTime = null;
	
    @BeforeClass
    public static void setup() {
        testRunDateTime = CommonMethods.getDateTime();
    }

    @AfterClass
    public static void teardown() {
        ;
    }
}
