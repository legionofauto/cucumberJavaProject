package CucumberTest.CucumberTest;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * These methods are used to save the user time creating the same action over and over. Also to clean up space in other 
 * methods by keeping it short and sweet.
 */
public class CommonMethods {
	
	//A list of passwords that will be used in the after hooks to clear from log files. 
	static List<String> cleartextPasswords = null;
	
	
	/*
	 * Returns the target file as a properties file for you to pull values from based off the key used.
	 * 
	 * @param fileName: The properties file location and name.
	 * @return: The properties found inside the input file. 
	 */
	public static Properties readPropertiesFile(String fileName) throws IOException  {
		      
		FileInputStream fis = null;
		Properties prop = null;
		      
		try {
			fis = new FileInputStream(fileName);
			prop = new Properties();
			prop.load(fis);
		} catch(FileNotFoundException fnfe) {
			fnfe.printStackTrace();
		} catch(IOException ioe) {
			ioe.printStackTrace();
		} finally {
			fis.close();
		}
		return prop;
	}
	
	
	/*
	 * Cleans all bad reserved from the file or directory you put through it. DO NOT PUT A DRIRECTORY STRING THROUGH 
	 * THIS METHOD. It will turn your directory into one long file and probably kill you test.
     * 
     * See Microsoft's "Naming Files, Paths, and Namespaces":
     * https://docs.microsoft.com/en-us/windows/win32/fileio/naming-a-file
     * 
     * The following are reserved characters for Windows:
	 * 
     * < (less than)
     * > (greater than)
     * : (colon)
     * " (double quote)
     * / (forward slash)
     * \ (backslash)
     * | (vertical bar or pipe)
     * ? (question mark)
     * * (asterisk)
     * 
     * @param file_name: (str) The file name you want to clean.
     * @return: The file name without any reserved characters.
	 */
	public static String forbiddenDirectoryCharacterFilter(String fileName) throws Throwable {

		fileName = fileName.replaceAll("[\\|\\?\\*\\<\\>\\:\\\"\\/\\\\]", "-");
		return fileName;
	}
	
	
	/*
	 * If your regular expression is found, returns a true statement, else, the statement returns false.
	 * 
	 * @param regex: The regular expression to check against the string output. 
	 * @param output: The string to be checked against. 
	 * @return: A true or false value based on the regex parameter.
	 */
	public static boolean regexFound(String regex, String output) throws IOException  {
		Pattern pattern = Pattern.compile(regex, Pattern.MULTILINE);
		Matcher isMatched = pattern.matcher(output);
		return isMatched.find();
	}
	
	
	/*
	 * Takes your regular expression and puts the first match to a string.
	 * 
	 * @param regex: The regular expression to check against the string output. 
	 * @param output: The string to be checked against. 
	 * @return: A string that matches the regular expression.
	 */
	public static String regexToString(String regex, String output) throws IOException  {
		Pattern pattern = Pattern.compile(regex, Pattern.MULTILINE);
		Matcher isMatched = pattern.matcher(output);
		return isMatched.group(1);
	}
	
	
	/*
	 * Takes every match in your regular expression and puts them into a list. 
	 * 
	 * @param regex: The regular expression to check against the string output. 
	 * @param output: The string to be checked against. 
	 * @return: A list of matches found. 
	 */
	public static List<String> regexAllToList(String regex, String output) throws IOException  {
		List<String> allMatches = new ArrayList<String>();
		
		Pattern pattern = Pattern.compile(regex, Pattern.MULTILINE);
		Matcher isMatched = pattern.matcher(output);
		
		while (isMatched.find()) { 
			allMatches.add(isMatched.group());
		}
		
		return allMatches;
		
	}
	
	
	/*
	 * Clears the StringBuilder string you put through here. 
	 * 
	 * @param stringBldr: The StringBuilder string to be reset.
	 * @return: A clear StringBuilder string. 
	 */
	public static StringBuilder clearStringBuilder(StringBuilder stringBldr) {
		
		//Sets the length of the character sequence.
		stringBldr.setLength(0);
		return stringBldr;
	}
	
	
	/*
	 * 
	 * 
	 * @param csvFile: 
	 * @param csvKey: 
	 * @param columnFoundIn: 
	 * @param columnReturn: 
	 * @return: 
	 */
	public static String returnCsvValue(String csvFile, String csvKey, int columnFoundIn, int columnReturn) throws IOException {
		
		// open file input stream
        BufferedReader reader = new BufferedReader(new FileReader(csvFile));
		
        //Initializes the variable.
        String returnValue = null;
        
        /*
         * If your CSV file has empty spaces at the beginning, you can uncomment the line below to allow for the reader
         * to start after that space. Repeat as many times as needed.
         */
        //reader.readLine();
        
        String line;
        //Reads each line in your CSV file. 
        while ((line = reader.readLine()) !=null) {
        	//Splits the line into a list based off of the delimiter ","
            String[] b = line.split(",");
            
            //If the csvKey is found in the column # you outlined
            if (regexFound(csvKey, b[columnFoundIn])) {
            	//The value in the columnReturn # will be returned. 
            	returnValue = b[columnReturn];
            	break;
            }
        }
        
        //Closes read stream.
        reader.close();
        
        //If no value was found
        if (returnValue == null) {
        	//Prints this statement to the terminal.
        	System.out.println("Unable to find the value in your CSV file.");
        }
        
		return returnValue;
		
	}
	
	
	public static void assureDirectoryExists(String directoryName) {
		
		//If you are trying to use your current working directory
		if (directoryName.startsWith(".")) {
			//Replaces the working directory marker.
			directoryName = directoryName.replaceFirst(".", "");
		
			//Gets the current working directory.
			String cwd = System.getProperty("user.dir");
	        
			// Detecting the operating system using os.name System property.
			String os = System.getProperty("os.name").toLowerCase();
	        
			if (os.contains("nix") || os.contains("nux") || os.contains("aix")) {
				// To do: Add Linux replacements
				directoryName = cwd + "/" + directoryName;
			} else {	
				// Crazy amount of escape characters
				cwd = cwd.replaceAll("\\\\", "\\\\\\\\");
				directoryName = cwd + directoryName;
			}
			
		}
		
	    File directory = new File(directoryName);
	    if (!directory.exists()){
	        directory.mkdirs();
	    }
	    
	}
	
	
	public void writeToScenarioLog(String value) throws IOException{
		
		Properties projectProp = CommonMethods.readPropertiesFile("project.properties");
		
	    File file = new File(Hooks.scenarioLogPath + projectProp.getProperty("scenarioLogName"));
	    
	    try{
	        FileWriter fw = new FileWriter(file.getAbsoluteFile());
	        BufferedWriter bw = new BufferedWriter(fw);
	        bw.write(value);
	        bw.close();
	    }
	    catch (IOException e){
	        e.printStackTrace();
	        System.exit(-1);
	    }
	}
	
	
	public static String getDateTime() {
	    LocalDateTime myDateObj = LocalDateTime.now();
	    DateTimeFormatter myFormatObj = DateTimeFormatter.ofPattern("ddMMyyyy_HHmmss");
	    String formattedDate = myDateObj.format(myFormatObj);
	    return formattedDate;
	}
	
	
	public static String fileToString(String filePath) throws Exception{
		String input = null;
	    Scanner sc = new Scanner(new File(filePath));
	    StringBuffer sb = new StringBuffer();
	    while (sc.hasNextLine()) {
	       input = sc.nextLine();
	       sb.append(input);
	    }
	    return sb.toString();
	}
	
	
}
